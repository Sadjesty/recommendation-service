package com.xm.recommendationservice.controller;

import com.xm.recommendationservice.domain.CryptoNormalizedRange;
import com.xm.recommendationservice.domain.CryptoPrice;
import com.xm.recommendationservice.service.PriceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/prices")
@RequiredArgsConstructor
@Tag(name = "Recommendation Service", description = "API for recommendation service")
public class PricesController {

    private final PriceService priceService;

    @GetMapping("/{symbol}/allPrices")
    @Operation(summary = "All prices for currency", description = "Get all prices for this currency")
    public List<CryptoPrice> getAllPrices(
            @Parameter(description = "Currency symbol", required = true, example = "BTC")
            @PathVariable String symbol)
            throws IOException {
        return priceService.getAllPrices(symbol);
    }

    @GetMapping("/{name}/minPrice")
    @Operation(summary = "Min price", description = "Get minimum price of the currency")
    public CryptoPrice getMinMonthPrice(
            @Parameter(description = "Currency symbol", required = true, example = "BTC")
            @PathVariable String name) throws IOException {
        return priceService.getMinPrice(name);
    }

    @GetMapping("/{name}/maxPrice")
    @Operation(summary = "Max price", description = "Get maximum price of the currency")
    public CryptoPrice getMaxMonthPrice(
            @Parameter(description = "Currency symbol", required = true, example = "BTC")
            @PathVariable String name) throws IOException {
        return priceService.getMaxPrice(name);
    }

    @GetMapping("/{name}/oldestMonthPrice")
    @Operation(summary = "Oldest price", description = "Get oldest price of the currency")
    public CryptoPrice getOldestMonthPrice(
            @Parameter(description = "Currency symbol", required = true, example = "BTC")
            @PathVariable String name) throws IOException {
        return priceService.getOldestMonthPrice(name);
    }

    @GetMapping("/{name}/newestMonthPrice")
    @Operation(summary = "Newest price", description = "Get newest price of the currency")
    public CryptoPrice getNewestMonthPrice(
            @Parameter(description = "Currency symbol", required = true, example = "BTC")
            @PathVariable String name) throws IOException {
        return priceService.getNewestMonthPrice(name);
    }

    @GetMapping("/normalizedRangeCryptos")
    @Operation(summary = "Normalized Range Cryptos", description = "Descending sorted list of all the cryptos, " +
            "comparing the normalized range")
    public Set<CryptoNormalizedRange> getAllCryptosWithNormalizedRange() throws IOException {
        return priceService.getAllCryptosWithNormalizedRange();
    }

    @GetMapping("/bestCrypto")
    @Operation(summary = "Best crypto for date", description = "Crypto with the highest normalized range for a specific day")
    public CryptoNormalizedRange getBestCrypto(
            @Parameter(description = "Date", required = true, example = "2022-02-05")
            @RequestParam(value = "date")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date) throws IOException {
        return priceService.getCryptoWithHighestNormalizedRangeByDate(date);
    }
}
