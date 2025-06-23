package org.example.reportetl.domain.bookingseats;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AssetResponse {
    private int id;

    private int floorId;

    private Asset.Type type;

    private String code;

    @JsonUnwrapped
    private AssetIdentity assetIdentity;

    private Asset.Status status;
}
