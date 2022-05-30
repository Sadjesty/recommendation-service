package com.xm.recommendationservice.service;

import com.opencsv.bean.CsvToBeanBuilder;
import com.xm.recommendationservice.domain.CryptoNormalizedRange;
import com.xm.recommendationservice.domain.CryptoPrice;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

@Service
@RequiredArgsConstructor
public class PriceService {

    private static final String FOLDER_NAME = "prices";
    private static final String FILE_SUFFIX = "_values.csv";
    public static final String FILE_NAME_REGEXP = "..._values\\.csv";

    public List<CryptoPrice> getAllPricesForMonth(String symbol) throws IOException {
        File file = getFileFromSymbol(symbol);
        return getAllPricesForMonth(file);
    }

    public CryptoPrice getMinMonthPrice(String symbol) throws IOException {
        File file = getFileFromSymbol(symbol);
        return getMinMonthPrice(file);
    }

    public CryptoPrice getMaxMonthPrice(String symbol) throws IOException {
        File file = getFileFromSymbol(symbol);
        return getMaxMonthPrice(file);
    }

    public CryptoPrice getOldestMonthPrice(String symbol) throws IOException {
        return getAllPricesForMonth(symbol).stream().min(Comparator.comparing(CryptoPrice::getPriceTime)).orElseThrow();
    }

    public CryptoPrice getNewestMonthPrice(String symbol) throws IOException {
        return getAllPricesForMonth(symbol).stream().max(Comparator.comparing(CryptoPrice::getPriceTime)).orElseThrow();
    }

    public Set<CryptoNormalizedRange> getAllCryptos() throws IOException {
        File directory = new ClassPathResource(FOLDER_NAME).getFile();
        File[] files = getAllSuitableFiles(directory);
        if (files != null) {
            return getCryptoNormalizedRangesSetInDescedingOrder(files);
        } else {
            return null;
        }
    }

    private Set<CryptoNormalizedRange> getCryptoNormalizedRangesSetInDescedingOrder(File[] files) throws IOException {
        Comparator<CryptoNormalizedRange> comparator =
                Comparator.comparing(CryptoNormalizedRange::getNormalizedRange).reversed();
        Set<CryptoNormalizedRange> allCryptos = new TreeSet<>(comparator);
        for (File file : files) {
            CryptoNormalizedRange cryptoNormalizedRange = getCryptoNormalizedRange(file);
            allCryptos.add(cryptoNormalizedRange);
        }
        return allCryptos;
    }

    private CryptoNormalizedRange getCryptoNormalizedRange(File file) throws IOException {
        CryptoPrice maxMonthCryptoPrice = getMaxMonthPrice(file);
        CryptoPrice minMonthCryptoPrice = getMinMonthPrice(file);
        BigDecimal maxMonthPrice = maxMonthCryptoPrice.getPriceValue();
        BigDecimal minMonthPrice = minMonthCryptoPrice.getPriceValue();
        BigDecimal normalizedRange = maxMonthPrice.subtract(minMonthPrice).divide(minMonthPrice, RoundingMode.CEILING);
        CryptoNormalizedRange cryptoNormalizedRange = new CryptoNormalizedRange();
        cryptoNormalizedRange.setSymbol(maxMonthCryptoPrice.getCurrencySymbol());
        cryptoNormalizedRange.setNormalizedRange(normalizedRange);
        return cryptoNormalizedRange;
    }

    private List<CryptoPrice> getAllPricesForMonth(File file) throws IOException {
        return new CsvToBeanBuilder<CryptoPrice>(new FileReader(file))
                .withType(CryptoPrice.class)
                .build()
                .parse();
    }

    private File[] getAllSuitableFiles(File directory) {
        return directory.listFiles(file ->
                file.isFile() &&
                        file.canRead() &&
                        file.getName().matches(FILE_NAME_REGEXP));
    }

    private CryptoPrice getMinMonthPrice(File file) throws IOException {
        return getAllPricesForMonth(file).stream().min(Comparator.comparing(CryptoPrice::getPriceValue)).orElseThrow();
    }

    private CryptoPrice getMaxMonthPrice(File file) throws IOException {
        return getAllPricesForMonth(file).stream().max(Comparator.comparing(CryptoPrice::getPriceValue)).orElseThrow();
    }

    private File getFileFromSymbol(String symbol) throws IOException {
        return new ClassPathResource(FOLDER_NAME + File.separator + symbol + FILE_SUFFIX).getFile();
    }
}
