package org.example.reportetl.domain.bookingseats;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ReservationRequest {

    private int floorId;

    private int employeeId;

    private List<@Valid AssetReservationRequest> assets;

    @Getter
    @Setter
    public static class AssetReservationRequest {
        @NotNull
        private LocalDateTime startTime;

        @NotNull
        private LocalDateTime endTime;

        @Positive
        private int assetId;
    }
}
