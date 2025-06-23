package org.example.reportetl.domain.bookingseats;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class AssetIdentity {
    private int grid_x;
    private int grid_y;
    private int width;
    private int height;
}
