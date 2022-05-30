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
import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PriceService {

    private static final String FOLDER_NAME = "prices";
    private static final String FILE_SUFFIX = "_values.csv";
    public static final String FILE_NAME_REGEXP = "..._values\\.csv";

    public List<CryptoPrice> getAllPrices(String symbol) throws IOException {
        File file = getFileFromSymbol(symbol);
        return getAllPrices(file);
    }

    public CryptoPrice getMinPrice(String symbol) throws IOException {
        File file = getFileFromSymbol(symbol);
        return getMinPrice(file);
    }

    public CryptoPrice getMaxPrice(String symbol) throws IOException {
        File file = getFileFromSymbol(symbol);
        return getMaxPrice(file);
    }

    public CryptoPrice getOldestMonthPrice(String symbol) throws IOException {
        return getAllPrices(symbol).stream().min(Comparator.comparing(CryptoPrice::getPriceTime)).orElseThrow();
    }

    public CryptoPrice getNewestMonthPrice(String symbol) throws IOException {
        return getAllPrices(symbol).stream().max(Comparator.comparing(CryptoPrice::getPriceTime)).orElseThrow();
    }

    public Set<CryptoNormalizedRange> getAllCryptos() throws IOException {
        File directory = new ClassPathResource(FOLDER_NAME).getFile();
        File[] files = getAllSuitableFiles(directory);
        if (files != null) {
            return getCryptoNormalizedRangesSetInDescendingOrder(files);
        } else {
            return null;
        }
    }

    public CryptoNormalizedRange getCryptoWithHighestNormalizedRangeByDate(LocalDate date) throws IOException {
        File directory = new ClassPathResource(FOLDER_NAME).getFile();
        File[] files = getAllSuitableFiles(directory);
        if (files != null) {
            return getCryptoWithHighestNormalizedRangeByDate(files, date);
        } else {
            return null;
        }
    }

    private Set<CryptoNormalizedRange> getCryptoNormalizedRangesSetInDescendingOrder(File[] files) throws IOException {
        Comparator<CryptoNormalizedRange> comparator =
                Comparator.comparing(CryptoNormalizedRange::getNormalizedRange).reversed();
        Set<CryptoNormalizedRange> allCryptos = new TreeSet<>(comparator);
        for (File file : files) {
            CryptoPrice maxCryptoPrice = getMaxPrice(file);
            CryptoPrice minCryptoPrice = getMinPrice(file);
            CryptoNormalizedRange cryptoNormalizedRange = getCryptoNormalizedRange(maxCryptoPrice.getPriceValue(),
                    minCryptoPrice.getPriceValue(), maxCryptoPrice.getCurrencySymbol());
            allCryptos.add(cryptoNormalizedRange);
        }
        return allCryptos;
    }

    private CryptoNormalizedRange getCryptoWithHighestNormalizedRangeByDate(File[] files, LocalDate date)
            throws IOException {
        List<CryptoNormalizedRange> allCryptos = new ArrayList<>();
        for (File file : files) {
            try {
                CryptoPrice maxDateCryptoPrice = getMaxDatePrice(file, date);
                CryptoPrice minDateCryptoPrice = getMinDayPrice(file, date);
                CryptoNormalizedRange cryptoNormalizedRange = getCryptoNormalizedRange(maxDateCryptoPrice.getPriceValue(),
                        minDateCryptoPrice.getPriceValue(), maxDateCryptoPrice.getCurrencySymbol());
                allCryptos.add(cryptoNormalizedRange);
            } catch (NoSuchElementException exception) {
                continue;
            }
        }
        return allCryptos.stream().max(Comparator.comparing(CryptoNormalizedRange::getNormalizedRange)).orElseThrow();
    }

    private CryptoNormalizedRange getCryptoNormalizedRange(BigDecimal maxMonthPrice, BigDecimal minMonthPrice,
                                                           String symbol) {
        BigDecimal normalizedRange = maxMonthPrice.subtract(minMonthPrice).divide(minMonthPrice, RoundingMode.CEILING);
        CryptoNormalizedRange cryptoNormalizedRange = new CryptoNormalizedRange();
        cryptoNormalizedRange.setSymbol(symbol);
        cryptoNormalizedRange.setNormalizedRange(normalizedRange);
        return cryptoNormalizedRange;
    }

    private List<CryptoPrice> getAllPrices(File file) throws IOException {
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

    private CryptoPrice getMinPrice(File file) throws IOException {
        return getAllPrices(file).stream().min(Comparator.comparing(CryptoPrice::getPriceValue)).orElseThrow();
    }

    private CryptoPrice getMaxPrice(File file) throws IOException {
        return getAllPrices(file).stream().max(Comparator.comparing(CryptoPrice::getPriceValue)).orElseThrow();
    }

    private CryptoPrice getMinDayPrice(File file, LocalDate date) throws IOException {
        return getAllPrices(file).stream()
                .filter(cryptoPrice -> cryptoPrice.getPriceTime().toLocalDate().isEqual(date))
                .min(Comparator.comparing(CryptoPrice::getPriceValue))
                .orElseThrow();
    }

    private CryptoPrice getMaxDatePrice(File file, LocalDate date) throws IOException {
        return getAllPrices(file).stream()
                .filter(cryptoPrice -> cryptoPrice.getPriceTime().toLocalDate().isEqual(date))
                .max(Comparator.comparing(CryptoPrice::getPriceValue))
                .orElseThrow();
    }

    private File getFileFromSymbol(String symbol) throws IOException {
        return new ClassPathResource(FOLDER_NAME + File.separator + symbol + FILE_SUFFIX).getFile();
    }
}
