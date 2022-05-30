package com.xm.recommendationservice.controller;

import com.xm.recommendationservice.entity.CryptoPrice;
import com.xm.recommendationservice.service.PriceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/prices")
@RequiredArgsConstructor
public class PricesController {

    private final PriceService priceService;

    @GetMapping("/{name}/allMonthPrices")
    public List<CryptoPrice> getAllMonthPrices(@PathVariable String name) throws IOException {
        return priceService.getAllPricesForMonth(name);
    }

    @GetMapping("/{name}/minMonthPrice")
    public CryptoPrice getMinMonthPrice(@PathVariable String name) throws IOException {
        return priceService.getMinMonthPrice(name);
    }

    @GetMapping("/{name}/maxMonthPrice")
    public CryptoPrice getMaxMonthPrice(@PathVariable String name) throws IOException {
        return priceService.getMaxMonthPrice(name);
    }

    @GetMapping("/{name}/oldestMonthPrice")
    public CryptoPrice getOldestMonthPrice(@PathVariable String name) throws IOException {
        return priceService.getOldestMonthPrice(name);
    }

    @GetMapping("/{name}/newestMonthPrice")
    public CryptoPrice getNewestMonthPrice(@PathVariable String name) throws IOException {
        return priceService.getNewestMonthPrice(name);
    }
}
