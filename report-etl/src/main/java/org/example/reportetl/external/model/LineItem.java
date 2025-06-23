package org.example.reportetl.external.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class LineItem {
    private int id;

    private Integer variantId;
    private Integer productId;

    private String title;
    private String variantTitle;
    private String name;

    private String sku;
    private String vendor;

    private BigDecimal quantity;
    private BigDecimal price;
    private int grams;

    private String discountCode;
    private BigDecimal totalDiscount;

    private boolean productExists;
    private boolean requiresShipping;
    private String variantInventoryManagement;
    private boolean giftCard;

    private List<DiscountAllocation> discountAllocations;

    private List<TaxLine> taxLines;
}
