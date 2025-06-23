package org.example.reportetl.domain.bookingseats;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FloorEditAssetRequest {
    private int id;

    private Integer width;

    private Integer height;

    private List<AssetRequest> assets;

    @Getter
    @Setter
    public static class AssetRequest {
        @Positive
        private Long id;

        @NotNull
        private Asset.Type type;

        private String code;

        @JsonUnwrapped
        private @NotNull AssetIdentityRequest identityRequest;
    }

    @Getter
    public static class AssetIdentityRequest {
        @PositiveOrZero
        private int grid_x;
        @PositiveOrZero
        private int grid_y;
        @PositiveOrZero
        private int width;
        @PositiveOrZero
        private int height;
    }
}
