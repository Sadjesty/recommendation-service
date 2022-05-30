package com.xm.recommendationservice.controller;

import com.xm.recommendationservice.domain.CryptoNormalizedRange;
import com.xm.recommendationservice.domain.CryptoPrice;
import com.xm.recommendationservice.service.PriceService;
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
public class PricesController {

    private final PriceService priceService;

    @GetMapping("/{name}/allMonthPrices")
    public List<CryptoPrice> getAllMonthPrices(@PathVariable String name) throws IOException {
        return priceService.getAllPrices(name);
    }

    @GetMapping("/{name}/minMonthPrice")
    public CryptoPrice getMinMonthPrice(@PathVariable String name) throws IOException {
        return priceService.getMinPrice(name);
    }

    @GetMapping("/{name}/maxMonthPrice")
    public CryptoPrice getMaxMonthPrice(@PathVariable String name) throws IOException {
        return priceService.getMaxPrice(name);
    }

    @GetMapping("/{name}/oldestMonthPrice")
    public CryptoPrice getOldestMonthPrice(@PathVariable String name) throws IOException {
        return priceService.getOldestMonthPrice(name);
    }

    @GetMapping("/{name}/newestMonthPrice")
    public CryptoPrice getNewestMonthPrice(@PathVariable String name) throws IOException {
        return priceService.getNewestMonthPrice(name);
    }

    @GetMapping("/allCryptos")
    public Set<CryptoNormalizedRange> getAllCryptos() throws IOException {
        return priceService.getAllCryptos();
    }

    @GetMapping("/bestCrypto")
    public CryptoNormalizedRange getBestCrypto(@RequestParam(value = "date")
                                               @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                               LocalDate date) throws IOException {
        return priceService.getCryptoWithHighestNormalizedRangeByDate(date);
    }
}
