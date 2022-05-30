package com.xm.recommendationservice.domain;

import com.opencsv.bean.AbstractBeanField;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Data
public class CryptoPrice {

    @CsvBindByName(column = "symbol")
    private String currencySymbol;
    @CsvCustomBindByName(column = "timestamp", converter = TimestampToLocalDateTimeConverter.class)
    private LocalDateTime priceTime;
    @CsvBindByName(column = "price")
    private BigDecimal priceValue;

    @NoArgsConstructor
    public static class TimestampToLocalDateTimeConverter
            extends AbstractBeanField<LocalDateTime, String> {

        @Override
        protected LocalDateTime convert(String value) {
            long epochMilli = Long.parseLong(value);
            return LocalDateTime.ofInstant(Instant.ofEpochMilli(epochMilli), ZoneId.systemDefault());
        }
    }

}
