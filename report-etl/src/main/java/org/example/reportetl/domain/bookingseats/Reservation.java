package org.example.reportetl.domain.bookingseats;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "reservations")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Positive
    private int assetId;

    @Positive
    private int floorId;

    @Positive
    private int employeeId;

    @NotNull
    private LocalDateTime startTime;

    @NotNull
    private LocalDateTime endTime;

    @JsonUnwrapped
    @Embedded
    private AssetIdentity assetIdentity;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @NotNull
    @Enumerated(value = EnumType.STRING)
    private Status status;

    public enum Status {
        PENDING,
        RESERVED,
        IN_USE, CANCELLED, NO_SHOW,
        COMPLETED, FORCED_CANCEL,
        FAIL
    }
}
