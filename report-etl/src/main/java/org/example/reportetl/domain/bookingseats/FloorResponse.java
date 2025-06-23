package org.example.reportetl.domain.bookingseats;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class FloorResponse {
    private int id;

    private int buildingId;

    private int floorNumber;

    private BigDecimal width;

    private BigDecimal height;

    private List<AssetResponse> assets;
}
