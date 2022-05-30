package com.xm.recommendationservice.domain;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CryptoNormalizedRange {

    private String symbol;
    private BigDecimal normalizedRange;

}
