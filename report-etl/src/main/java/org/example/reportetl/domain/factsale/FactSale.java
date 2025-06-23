package org.example.reportetl.domain.factsale;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "fact_sales")
public class FactSale {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int storeId;

    private int storeKey;

    private int timeKey;

    private int dateKey;

    private int orderId;
    private int orderKey;

    private int customerId;
    private int customerKey;
    private int customerTypeKey;

    private int trafficKey;

    private Integer productKey;
    private Integer variantKey;

    @Setter
    private int billingAddressKey;
    @Setter
    private int shippingAddressKey;

    @NotNull
    private String saleKind;
    @NotNull
    private String saleItemType;

    private int quantity;
    private int returnedQuantity;
    private int netQuantity;

    @NotNull
    private BigDecimal price = BigDecimal.ZERO;
    @NotNull
    private BigDecimal shipping = BigDecimal.ZERO;
    private BigDecimal tax = BigDecimal.ZERO;

    @NotNull
    private BigDecimal discounts = BigDecimal.ZERO;
    @NotNull
    private BigDecimal returns = BigDecimal.ZERO;
    @NotNull
    private BigDecimal grossSales = BigDecimal.ZERO;

    @NotNull
    private BigDecimal netSales = BigDecimal.ZERO;
    @NotNull
    private BigDecimal totalSales = BigDecimal.ZERO;

    //section: aggregate
    @Setter
    private Integer customers; //customer distinct count

    @Setter
    private Integer orders; //order distinct count

    private BigDecimal averageOrderValue = BigDecimal.ZERO; // (total sales / order distinct count)
    //end_section

    //section: optional, nullable, removable
    @Nullable
    private String lineTitle;
    @Nullable
    private String lineVariantTitle;
    @Nullable
    private String lineVendor;
    @Nullable
    private String lineSku;
    //end_section

    @NotNull
    @Setter
    private LocalDateTime createdAt;
    @NotNull
    @Setter
    private LocalDateTime updatedAt;

    //region new field
    @Setter
    private Long userKey;
    @Setter
    private Long lineItemKey;
    private BigDecimal costPerItem = BigDecimal.ZERO;
    private BigDecimal cost = BigDecimal.ZERO;
    private String adjustment;
    private BigDecimal grossProfit = BigDecimal.ZERO;
    private BigDecimal grossMargin = BigDecimal.ZERO;
    @Transient
    @Setter
    private boolean taxIncluded;
    @Setter
    private Long locationKey;
    @Transient
    private BigDecimal totalDiscountAllocation = BigDecimal.ZERO;
}
