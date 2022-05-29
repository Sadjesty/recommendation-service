package com.xm.recommendationservice.entity;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;

import java.math.BigDecimal;

@Data
//todo ADD DTO WITH LOCALDATETIME INSTAED TIMESTAMP
public class CryptoPrice {

    @CsvBindByName(column = "symbol")
    private String currencySymbol;
    @CsvBindByName(column = "timestamp")
    private String priceTime;
    @CsvBindByName(column = "price")
    private BigDecimal priceValue;

}
