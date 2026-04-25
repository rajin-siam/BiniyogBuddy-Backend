package com.biniyogbuddy.stocks.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "stock_prices")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_id", unique = true, nullable = false)
    private Stock stock;

    // Prices
    private BigDecimal ltp;              // last traded price
    @Column(name = "open_price")
    private BigDecimal openPrice;
    private BigDecimal high;
    private BigDecimal low;
    @Column(name = "close_price")
    private BigDecimal closePrice;
    @Column(name = "yesterday_close")
    private BigDecimal yesterdayClose;

    // Change
    private BigDecimal change;
    @Column(name = "change_pct", precision = 10, scale = 4)
    private BigDecimal changePct;

    // Volume
    private Long volume;
    @Column(name = "value_mn", precision = 18, scale = 4)
    private BigDecimal valueMn;          // turnover in millions BDT
    private Integer trades;

    // Extra stats (may be null — scraped separately)
    @Column(name = "week_52_high")
    private BigDecimal week52High;
    @Column(name = "week_52_low")
    private BigDecimal week52Low;
    @Column(name = "floor_price")
    private BigDecimal floorPrice;
    @Column(name = "circuit_up")
    private BigDecimal circuitUp;
    @Column(name = "circuit_down")
    private BigDecimal circuitDown;

    @Column(name = "fetched_at", nullable = false)

    private LocalDateTime fetchedAt;
}