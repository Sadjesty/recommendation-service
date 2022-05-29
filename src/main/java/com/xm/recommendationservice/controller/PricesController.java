package com.xm.recommendationservice.controller;

import com.opencsv.bean.CsvToBeanBuilder;
import com.xm.recommendationservice.entity.CryptoPrice;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;

@RestController
@RequestMapping("/prices")
public class PricesController {

    @GetMapping("/{name}")
    public List<CryptoPrice> getAllPrices(@PathVariable String name) throws FileNotFoundException {
        //todo rework filePath
        String filename = "/home/sadjesty/projects/recommendation-service/src/main/resources/prices/" + name + "_values.csv";

        return new CsvToBeanBuilder<CryptoPrice>(new FileReader(filename))
                .withType(CryptoPrice.class)
                .build()
                .parse();
    }
}
