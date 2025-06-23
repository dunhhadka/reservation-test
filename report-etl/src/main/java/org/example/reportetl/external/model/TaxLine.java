package org.example.reportetl.external.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class TaxLine {
    private float rate;
    private String title;
    private BigDecimal price;
}
