package com.xm.recommendationservice.service;

import com.opencsv.bean.CsvToBeanBuilder;
import com.xm.recommendationservice.entity.CryptoPrice;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PriceService {

    private static final String FOLDER_NAME = "prices";
    private static final String FILE_SUFFIX = "_values.csv";

    public List<CryptoPrice> getAllPricesForMonth(String symbol) throws IOException {
        File file = new ClassPathResource(FOLDER_NAME + File.separator + symbol + FILE_SUFFIX).getFile();

        return new CsvToBeanBuilder<CryptoPrice>(new FileReader(file))
                .withType(CryptoPrice.class)
                .build()
                .parse();
    }

    public CryptoPrice getMinMonthPrice(String symbol) throws IOException {
        return getAllPricesForMonth(symbol).stream().min(Comparator.comparing(CryptoPrice::getPriceValue)).orElseThrow();
    }

    public CryptoPrice getMaxMonthPrice(String symbol) throws IOException {
        return getAllPricesForMonth(symbol).stream().max(Comparator.comparing(CryptoPrice::getPriceValue)).orElseThrow();
    }

    public CryptoPrice getOldestMonthPrice(String symbol) throws IOException {
        return getAllPricesForMonth(symbol).stream().min(Comparator.comparing(CryptoPrice::getPriceTime)).orElseThrow();
    }

    public CryptoPrice getNewestMonthPrice(String symbol) throws IOException {
        return getAllPricesForMonth(symbol).stream().max(Comparator.comparing(CryptoPrice::getPriceTime)).orElseThrow();
    }
}
