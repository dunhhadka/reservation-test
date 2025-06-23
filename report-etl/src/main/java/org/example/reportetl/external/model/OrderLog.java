package org.example.reportetl.external.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class OrderLog {
    private int id;
    private int storeId;
    private int orderId;
    private String verb;
    private String data;
    private String actor;
    private Date createdOn;
    private String properties;
}
