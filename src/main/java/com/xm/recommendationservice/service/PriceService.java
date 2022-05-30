package com.xm.recommendationservice.service;

import com.opencsv.bean.CsvToBeanBuilder;
import com.xm.recommendationservice.constants.ErrorCode;
import com.xm.recommendationservice.domain.CryptoNormalizedRange;
import com.xm.recommendationservice.domain.CryptoPrice;
import com.xm.recommendationservice.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class PriceService {

    private static final String FOLDER_NAME = "prices";
    private static final String FILE_SUFFIX = "_values.csv";
    public static final String FILE_NAME_REGEXP = "..._values\\.csv";

    public List<CryptoPrice> getAllPrices(String symbol) throws ServiceException {
        File file = getFileFromSymbol(symbol);
        return getAllPrices(file);
    }

    public CryptoPrice getMinPrice(String symbol) throws ServiceException {
        File file = getFileFromSymbol(symbol);
        return getMinPrice(file);
    }

    public CryptoPrice getMaxPrice(String symbol) throws ServiceException {
        File file = getFileFromSymbol(symbol);
        return getMaxPrice(file);
    }

    public CryptoPrice getOldestMonthPrice(String symbol) throws ServiceException {
        return getAllPrices(symbol).stream()
                .min(Comparator.comparing(CryptoPrice::getPriceTime))
                .orElseThrow(() -> new ServiceException(ErrorCode.NO_SUCH_ELEMENT));
    }

    public CryptoPrice getNewestMonthPrice(String symbol) throws ServiceException {
        return getAllPrices(symbol).stream()
                .max(Comparator.comparing(CryptoPrice::getPriceTime))
                .orElseThrow(() -> new ServiceException(ErrorCode.NO_SUCH_ELEMENT));
    }

    public Set<CryptoNormalizedRange> getAllCryptosWithNormalizedRange() throws ServiceException {
        File directory = getDirectory();
        File[] files = getAllSuitableFiles(directory);
        if (files != null) {
            return getCryptoNormalizedRangesSetInDescendingOrder(files);
        } else {
            throw new ServiceException(ErrorCode.COMMON_IO_EXCEPTION);
        }
    }

    public CryptoNormalizedRange getCryptoWithHighestNormalizedRangeByDate(LocalDate date) throws ServiceException {
        File directory = getDirectory();
        File[] files = getAllSuitableFiles(directory);
        if (files != null) {
            return getCryptoWithHighestNormalizedRangeByDate(files, date);
        } else {
            throw new ServiceException(ErrorCode.COMMON_IO_EXCEPTION);
        }
    }

    private Set<CryptoNormalizedRange> getCryptoNormalizedRangesSetInDescendingOrder(File[] files) {
        Comparator<CryptoNormalizedRange> comparator =
                Comparator.comparing(CryptoNormalizedRange::getNormalizedRange).reversed();
        Set<CryptoNormalizedRange> allCryptos = new TreeSet<>(comparator);
        for (File file : files) {
            processFile(allCryptos, file);
        }
        return allCryptos;
    }

    private void processFile(Collection<CryptoNormalizedRange> allCryptos, File file) {
        try {
            CryptoPrice maxCryptoPrice = getMaxPrice(file);
            CryptoPrice minCryptoPrice = getMinPrice(file);
            CryptoNormalizedRange cryptoNormalizedRange = getCryptoNormalizedRange(maxCryptoPrice.getPriceValue(),
                    minCryptoPrice.getPriceValue(), maxCryptoPrice.getCurrencySymbol());
            allCryptos.add(cryptoNormalizedRange);
        } catch (ServiceException exception) {
            log.debug(exception.getMessage(), exception);
        }
    }

    private CryptoNormalizedRange getCryptoWithHighestNormalizedRangeByDate(File[] files, LocalDate date)
            throws ServiceException {
        List<CryptoNormalizedRange> allCryptos = new ArrayList<>();
        for (File file : files) {
            try {
                processFile(date, allCryptos, file);
            } catch (ServiceException exception) {
                log.debug(exception.getMessage(), exception);
            }
        }
        return allCryptos.stream().max(Comparator.comparing(CryptoNormalizedRange::getNormalizedRange))
                .orElseThrow(() -> new ServiceException(ErrorCode.NO_SUCH_ELEMENT));
    }

    private void processFile(LocalDate date, List<CryptoNormalizedRange> allCryptos, File file) throws ServiceException {
        CryptoPrice maxDateCryptoPrice = getMaxDatePrice(file, date);
        CryptoPrice minDateCryptoPrice = getMinDayPrice(file, date);
        CryptoNormalizedRange cryptoNormalizedRange = getCryptoNormalizedRange(maxDateCryptoPrice.getPriceValue(),
                minDateCryptoPrice.getPriceValue(), maxDateCryptoPrice.getCurrencySymbol());
        allCryptos.add(cryptoNormalizedRange);
    }

    private CryptoNormalizedRange getCryptoNormalizedRange(BigDecimal maxMonthPrice, BigDecimal minMonthPrice,
                                                           String symbol) {
        BigDecimal normalizedRange = maxMonthPrice.subtract(minMonthPrice).divide(minMonthPrice, RoundingMode.CEILING);
        CryptoNormalizedRange cryptoNormalizedRange = new CryptoNormalizedRange();
        cryptoNormalizedRange.setSymbol(symbol);
        cryptoNormalizedRange.setNormalizedRange(normalizedRange);
        return cryptoNormalizedRange;
    }

    private List<CryptoPrice> getAllPrices(File file) throws ServiceException {
        try (FileReader reader = new FileReader(file)) {
            return new CsvToBeanBuilder<CryptoPrice>(reader)
                    .withType(CryptoPrice.class)
                    .build()
                    .parse();
        } catch (IOException exception) {
            throw new ServiceException(ErrorCode.COMMON_IO_EXCEPTION, exception);
        }
    }

    private File[] getAllSuitableFiles(File directory) {
        return directory.listFiles(file ->
                file.isFile() &&
                        file.canRead() &&
                        file.getName().matches(FILE_NAME_REGEXP));
    }

    private CryptoPrice getMinPrice(File file) throws ServiceException {
        return getAllPrices(file).stream().min(Comparator.comparing(CryptoPrice::getPriceValue))
                .orElseThrow(() -> new ServiceException(ErrorCode.NO_SUCH_ELEMENT));
    }

    private CryptoPrice getMaxPrice(File file) throws ServiceException {
        return getAllPrices(file).stream().max(Comparator.comparing(CryptoPrice::getPriceValue))
                .orElseThrow(() -> new ServiceException(ErrorCode.NO_SUCH_ELEMENT));
    }

    private CryptoPrice getMinDayPrice(File file, LocalDate date) throws ServiceException {
        return getAllPrices(file).stream()
                .filter(cryptoPrice -> cryptoPrice.getPriceTime().toLocalDate().isEqual(date))
                .min(Comparator.comparing(CryptoPrice::getPriceValue))
                .orElseThrow(() -> new ServiceException(ErrorCode.NO_SUCH_ELEMENT));
    }

    private CryptoPrice getMaxDatePrice(File file, LocalDate date) throws ServiceException {
        return getAllPrices(file).stream()
                .filter(cryptoPrice -> cryptoPrice.getPriceTime().toLocalDate().isEqual(date))
                .max(Comparator.comparing(CryptoPrice::getPriceValue))
                .orElseThrow(() -> new ServiceException(ErrorCode.NO_SUCH_ELEMENT));
    }

    private File getFileFromSymbol(String symbol) throws ServiceException {
        try {
            return new ClassPathResource(FOLDER_NAME + File.separator + symbol + FILE_SUFFIX).getFile();
        } catch (FileNotFoundException exception) {
            throw new ServiceException(ErrorCode.CURRENCY_NOT_SUPPORTED_YET, exception);
        } catch (IOException exception) {
            throw new ServiceException(ErrorCode.COMMON_IO_EXCEPTION, exception);
        }
    }

    private File getDirectory() throws ServiceException {
        try {
            return new ClassPathResource(FOLDER_NAME).getFile();
        } catch (IOException exception) {
            throw new ServiceException(ErrorCode.COMMON_IO_EXCEPTION, exception);
        }
    }
}
