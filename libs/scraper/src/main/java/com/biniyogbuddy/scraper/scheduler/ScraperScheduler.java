package com.biniyogbuddy.scraper.scheduler;

import com.biniyogbuddy.scraper.dto.RawScrapedData.ScrapedPage;
import com.biniyogbuddy.scraper.fetcher.DsePriceFetcher;
import com.biniyogbuddy.scraper.service.ScraperIngestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScraperScheduler {

    private final DsePriceFetcher fetcher;
    private final ScraperIngestionService ingestionService;

    // Runs immediately on startup (initialDelay = 0), then every hour after
    // each run completes (fixedDelay = 3_600_000 ms).
    @Scheduled(initialDelay = 15_000, fixedDelay = 3_600_000)
    public void scrapeAndIngest() {
        log.info("Scrape job started");
        try {
            ScrapedPage page = fetcher.fetchLivePrices();
            ingestionService.ingest(page);
            log.info("Scrape job finished");
        } catch (Exception e) {
            log.error("Scrape job failed: {}", e.getMessage(), e);
        }
    }
}
