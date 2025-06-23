package org.example.reportetl.external.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class Order {
    private int id;
    private int storeId;

    private String email;

    private Date createdOn;
    private Date modifiedOn;
    private Date processedOn;
    private Date closedOn;

    private Date cancelledOn;
    private String cancelReason;

    private int number;
    private int orderNumber;
    private String name;

    private String status;

    private String source;
    private String sourceName;

    private String note;

    private String token;
    private String checkoutToken;
    private String cartToken;
    private String gateway;
    private String processingMethod;

    private boolean test;

    private String currency;
    private String financialStatus;

    private BigDecimal totalPrice;
    private BigDecimal subTotalPrice;
    private BigDecimal totalDiscounts;
    private BigDecimal totalLineItemsPrice;

    private boolean buyerAcceptsMarketing;
    private String referringSite;
    private String landingSite;
    private String reference;
    private String sourceIdentifier;
    private String sourceUrl;
    private String landingSiteRef;

    private int totalWeight;
    private String fulfillmentStatus;

    private Integer customerId;

    private OrderAddress billingAddress;
    private OrderAddress shippingAddress;

    private List<LineItem> lineItems;
}
