package com.xm.recommendationservice.service;

import com.xm.recommendationservice.domain.CryptoNormalizedRange;
import com.xm.recommendationservice.domain.CryptoPrice;
import com.xm.recommendationservice.exception.ServiceException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class PriceServiceTest {

    private static final String BTC = "BTC";
    public static final CryptoNormalizedRange BTC_NORMALIZED_RANGE = new CryptoNormalizedRange(BTC, BigDecimal.valueOf(0.01));
    private static final CryptoPrice BTC_PRICE_3 = new CryptoPrice(BTC, LocalDateTime.parse("2022-01-01T16:00:00"), BigDecimal.valueOf(47143.98));
    private static final CryptoPrice BTC_PRICE_2 = new CryptoPrice(BTC, LocalDateTime.parse("2022-01-01T13:00:00"), BigDecimal.valueOf(46979.61));
    private static final CryptoPrice BTC_PRICE_1 = new CryptoPrice(BTC, LocalDateTime.parse("2022-01-01T10:00:00"), BigDecimal.valueOf(46813.21));
    private static final String NEW_CRYPTO = UUID.randomUUID().toString();
    public static final LocalDate LOCAL_DATE = LocalDate.parse("2022-01-01");
    public static final LocalDateTime AT_DATE = LocalDateTime.parse("2022-01-01T12:00:00");
    public static final LocalDateTime TO_DATE = LocalDateTime.parse("2022-01-01T16:30:00");

    @Mock
    private Logger log;
    @InjectMocks
    private PriceService priceService;

    @Test
    void testGetAllPricesOk() throws ServiceException {
        List<CryptoPrice> expected = Arrays.asList(
                BTC_PRICE_1,
                BTC_PRICE_2,
                BTC_PRICE_3
        );

        List<CryptoPrice> result = priceService.getAllPrices(BTC);
        assertEquals(expected, result);
    }

    @Test
    void testGetAllPricesException() {
        assertThrows(ServiceException.class, () -> priceService.getAllPrices(NEW_CRYPTO));
    }

    @Test
    void testGetMinPriceOk() throws ServiceException {
        CryptoPrice result = priceService.getMinPrice(BTC);
        assertEquals(BTC_PRICE_1, result);
    }

    @Test
    void testGetMinPriceException() {
        assertThrows(ServiceException.class, () -> priceService.getMinPrice(NEW_CRYPTO));
    }

    @Test
    void testGetMaxPriceOk() throws ServiceException {
        CryptoPrice result = priceService.getMaxPrice(BTC);
        Assertions.assertEquals(BTC_PRICE_3, result);
    }

    @Test
    void testGetOldestMonthPriceOk() throws ServiceException {
        CryptoPrice result = priceService.getOldestMonthPrice(BTC);
        Assertions.assertEquals(BTC_PRICE_1, result);
    }

    @Test
    void testGetOldestMonthPriceException() {
        assertThrows(ServiceException.class, () -> priceService.getOldestMonthPrice(NEW_CRYPTO));
    }

    @Test
    void testGetNewestMonthPriceOk() throws ServiceException {
        CryptoPrice result = priceService.getNewestMonthPrice(BTC);
        Assertions.assertEquals(BTC_PRICE_3, result);
    }

    @Test
    void testGetAllCryptosWithNormalizedRangeOk() throws ServiceException {
        Set<CryptoNormalizedRange> result = priceService.getAllCryptosWithNormalizedRange();
        assertEquals(Set.of(BTC_NORMALIZED_RANGE), result);
    }

    @Test
    void testGetCryptoWithHighestNormalizedRangeByDateOk() throws ServiceException {
        CryptoNormalizedRange result = priceService.getCryptoWithHighestNormalizedRangeByDate(LOCAL_DATE);
        Assertions.assertEquals(BTC_NORMALIZED_RANGE, result);
    }

    @Test
    void getAllPricesInPeriodOk() throws ServiceException {
        List<CryptoPrice> expected = Arrays.asList(
                BTC_PRICE_2,
                BTC_PRICE_3
        );
        List<CryptoPrice> result = priceService.getAllPricesInPeriod(BTC, AT_DATE, TO_DATE);
        Assertions.assertEquals(expected, result);
    }
}
