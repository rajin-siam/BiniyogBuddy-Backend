package com.biniyogbuddy.scraper.fetcher;

import com.biniyogbuddy.scraper.config.ScraperProperties;
import com.biniyogbuddy.scraper.dto.RawScrapedData.RawIndexData;
import com.biniyogbuddy.scraper.dto.RawScrapedData.RawMarketStats;
import com.biniyogbuddy.scraper.dto.RawScrapedData.RawStockRow;
import com.biniyogbuddy.scraper.dto.RawScrapedData.ScrapedPage;
import com.biniyogbuddy.scraper.entity.ScraperLog;
import com.biniyogbuddy.scraper.entity.ScraperStatus;
import com.biniyogbuddy.scraper.repository.ScraperLogRepository;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Fetches DSE data from TWO pages using Playwright.
 *
 *  Page 1 — latest_share_price_scroll_l.php
 *    → table.table tbody tr → ~396 stock rows with live prices
 *
 *  Page 2 — index.php  (the market homepage)
 *    → div.midrow blocks in LeftColHome contain:
 *       - DSEX / DSES / DS30 index values (value, change, changePct)
 *       - Total Trade, Total Volume, Total Value in Taka (mn)
 *       - Issues Advanced, Issues Declined, Issues Unchanged
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DsePriceFetcher {

    private final ScraperProperties props;
    private final ScraperLogRepository scraperLogRepository;

    public ScrapedPage fetchLivePrices() {
        long start = System.currentTimeMillis();
        Exception lastError = null;

        for (int attempt = 1; attempt <= props.getRetryAttempts(); attempt++) {
            try {
                return doFetch(start);
            } catch (Exception e) {
                lastError = e;
                log.warn("Fetch attempt {}/{} failed: {}", attempt, props.getRetryAttempts(), e.getMessage());
                if (attempt < props.getRetryAttempts()) {
                    try {
                        Thread.sleep(props.getRetryDelayMs());
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        }

        long ms = System.currentTimeMillis() - start;
        String errorMsg = lastError != null ? lastError.getMessage() : "unknown";
        saveLog(ScraperStatus.FAILED, 0, 0, errorMsg, ms);
        throw new RuntimeException("DSE fetch failed after " + props.getRetryAttempts() + " attempts: " + errorMsg, lastError);
    }

    // ─────────────────────────────────────────────────────────────────────────

    private ScrapedPage doFetch(long start) {
        try (Playwright playwright = Playwright.create()) {
            java.nio.file.Path chromiumPath = resolveChromiumPath();
            BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions().setHeadless(true);
            if (chromiumPath != null) launchOptions.setExecutablePath(chromiumPath);
            Browser browser = playwright.chromium().launch(launchOptions);

            // Page 1 — stock prices
            Document pricePage  = fetchPage(browser, props.getPriceUrl());
            List<RawStockRow> stockRows = parseStockRows(pricePage);
            String marketStatus = detectMarketStatus(pricePage);

            // Page 2 — index values + market stats (both from index.php)
            Document indexPage         = fetchPage(browser, props.getIndexUrl());
            List<RawIndexData> indexData = parseIndexPage(indexPage);
            RawMarketStats marketStats   = parseMarketStats(indexPage);

            browser.close();

            long ms = System.currentTimeMillis() - start;
            log.info("Scrape complete: stocks={} indices={} status={} time={}ms",
                    stockRows.size(), indexData.size(), marketStatus, ms);

            saveLog(ScraperStatus.SUCCESS, stockRows.size(), 0, null, ms);

            return ScrapedPage.builder()
                    .stockRows(stockRows)
                    .indexData(indexData)
                    .marketStats(marketStats)
                    .marketStatus(marketStatus)
                    .fetchDurationMs(ms)
                    .build();
        }
    }

    // ─────────────────────────────────────────────────────────────────────────

    private void saveLog(ScraperStatus status, int scraped, int failed, String error, long ms) {
        try {
            scraperLogRepository.save(ScraperLog.builder()
                    .status(status)
                    .stocksScraped(scraped)
                    .stocksFailed(failed)
                    .errorMessage(error)
                    .durationMs(ms)
                    .ranAt(LocalDateTime.now())
                    .build());
        } catch (Exception e) {
            log.error("Failed to save scraper log: {}", e.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────────────────────────

    private Document fetchPage(Browser browser, String url) {
        Page page = browser.newPage();
        try {
            page.setExtraHTTPHeaders(Map.of(
                    "User-Agent",      props.getUserAgent(),
                    "Accept-Language", "en-US,en;q=0.9",
                    "Referer",         "https://dsebd.org/"
            ));
            page.navigate(url);
            page.waitForLoadState(
                    com.microsoft.playwright.options.LoadState.NETWORKIDLE,
                    new Page.WaitForLoadStateOptions().setTimeout(props.getTimeoutMs())
            );
            return Jsoup.parse(page.content());
        } finally {
            page.close();
        }
    }

    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Confirmed working: selector "table.table tbody tr" returns 396 valid rows.
     * Columns: [0]# [1]CODE [2]LTP [3]HIGH [4]LOW [5]CLOSEP [6]YCP
     *          [7]CHANGE [8]TRADE [9]VALUE [10]VOLUME
     */
    private List<RawStockRow> parseStockRows(Document doc) {
        List<RawStockRow> rows = new ArrayList<>();
        Elements tableRows = doc.select("table.table tbody tr");
        if (tableRows.isEmpty()) tableRows = doc.select("table tbody tr");

        for (Element row : tableRows) {
            Elements cells = row.select("td");
            if (cells.size() < 11) continue;
            String code = cells.get(1).text().trim();
            if (code.isEmpty() || code.equalsIgnoreCase("TRADING CODE")) continue;

            rows.add(RawStockRow.builder()
                    .tradingCode(code)
                    .ltp(cells.get(2).text().trim())
                    .high(cells.get(3).text().trim())
                    .low(cells.get(4).text().trim())
                    .closePrice(cells.get(5).text().trim())
                    .yesterdayClose(cells.get(6).text().trim())
                    .change(cells.get(7).text().trim())
                    .trades(cells.get(8).text().trim())
                    .valueMn(cells.get(9).text().trim())
                    .volume(cells.get(10).text().trim())
                    .build());
        }
        if (rows.isEmpty()) log.warn("Zero stock rows — check price page selector");
        return rows;
    }

    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Parses DSEX/DSES/DS30 index values from index.php.
     *
     * Each index sits in a div.midrow block inside LeftColHome:
     *   .m_col-1 → index name (e.g. "DSEX Index")
     *   .m_col-2 → value     (e.g. "5300.57240")
     *   .m_col-3 → change    (e.g. "-15.61085")
     *   .m_col-4 → changePct (e.g. "-0.29365%")
     */
    private List<RawIndexData> parseIndexPage(Document doc) {
        List<RawIndexData> results = new ArrayList<>();

        for (Element row : doc.select("div.midrow")) {
            Element nameEl = row.selectFirst(".m_col-1");
            if (nameEl == null) continue;

            String name = nameEl.text().trim();
            String indexName;
            if      (name.contains("DSEX")) indexName = "DSEX";
            else if (name.contains("DSES")) indexName = "DSES";
            else if (name.contains("DS30")) indexName = "DS30";
            else continue;

            Element valEl     = row.selectFirst(".m_col-2");
            Element changeEl  = row.selectFirst(".m_col-3");
            Element pctEl     = row.selectFirst(".m_col-4");
            if (valEl == null) continue;

            String value = valEl.text().trim();
            results.add(RawIndexData.builder()
                    .name(indexName)
                    .value(value)
                    .change(changeEl  != null ? changeEl.text().trim()  : null)
                    .changePct(pctEl  != null ? pctEl.text().trim()     : null)
                    .build());

            log.debug("Index: {} = {}", indexName, value);
        }

        if (results.isEmpty()) log.warn("No index values found on {}", props.getIndexUrl());
        return results;
    }

    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Parses market stats from index.php — both stat blocks live in div.midrow rows
     * inside LeftColHome, using .m_col-wid / .m_col-wid1 / .m_col-wid2 columns.
     *
     * Block 1 (trade stats):
     *   header: "Total Trade" | "Total Volume" | "Total Value in Taka (mn)"
     *   values: .m_col-wid.colorlight | .m_col-wid1.colorlight | .m_col-wid2.colorlight
     *
     * Block 2 (breadth stats):
     *   header: "Issues Advanced" | "Issues declined" | "Issues Unchanged"
     *   values: same column classes
     */
    private RawMarketStats parseMarketStats(Document doc) {
        String totalTrade = "0", totalVolume = "0", totalValueMn = "0";
        String advances = "0", declines = "0", unchanged = "0";

        Elements midrows = doc.select("div.midrow");
        for (int i = 0; i < midrows.size(); i++) {
            Element row = midrows.get(i);
            String headerText = row.text();

            if (headerText.contains("Total Trade") && i + 1 < midrows.size()) {
                Element valRow = midrows.get(i + 1);
                totalTrade   = textOf(valRow, ".m_col-wid.colorlight",  "0");
                totalVolume  = textOf(valRow, ".m_col-wid1.colorlight", "0");
                totalValueMn = textOf(valRow, ".m_col-wid2.colorlight", "0");
                log.debug("Stats: trade={} volume={} value={}", totalTrade, totalVolume, totalValueMn);
            }

            if (headerText.contains("Issues Advanced") && i + 1 < midrows.size()) {
                Element valRow = midrows.get(i + 1);
                advances  = textOf(valRow, ".m_col-wid.colorlight",  "0");
                declines  = textOf(valRow, ".m_col-wid1.colorlight", "0");
                unchanged = textOf(valRow, ".m_col-wid2.colorlight", "0");
                log.debug("Breadth: adv={} dec={} unc={}", advances, declines, unchanged);
            }
        }

        return RawMarketStats.builder()
                .totalTrade(totalTrade)
                .totalVolume(totalVolume)
                .totalValueMn(totalValueMn)
                .advances(advances)
                .declines(declines)
                .unchanged(unchanged)
                .build();
    }

    private java.nio.file.Path resolveChromiumPath() {
        String[] candidates = {"/usr/bin/chromium", "/usr/bin/chromium-browser", "/usr/bin/google-chrome"};
        for (String path : candidates) {
            java.io.File f = new java.io.File(path);
            if (f.exists()) {
                log.info("Using system Chromium: {}", path);
                return f.toPath();
            }
        }
        log.info("Using Playwright bundled Chromium");
        return null;
    }

    private String textOf(Element parent, String cssSelector, String fallback) {
        Element el = parent.selectFirst(cssSelector);
        return el != null ? el.text().trim() : fallback;
    }

    // ─────────────────────────────────────────────────────────────────────────

    private String detectMarketStatus(Document doc) {
        Element statusEl = doc.selectFirst("span.time.pull-right span b");
        if (statusEl == null) return "closed";

        String status = statusEl.text().toLowerCase().trim();
        if (status.contains("pre-open") || status.contains("pre open")) return "pre_open";
        if (status.contains("open")) return "open";
        return "closed";
    }
}
