package org.example.reportetl.external.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
public class DiscountAllocation {
    private int id;

    private BigDecimal amount;

    private Long targetId;
    private String targetType;

    private Integer applicationId;
    private Integer applicationIndex;

    private Date createdAt;
}
