package org.example.reportetl.domain.bookingseats;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Entity
@Builder
@Table(name = "assets")
@NoArgsConstructor
@AllArgsConstructor
public class Asset {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Setter
    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "floor_id", referencedColumnName = "id")
    private Floor floor;

    @NotNull
    @Enumerated(value = EnumType.STRING)
    private Type type;

    private String code;

    @Embedded
    @JsonUnwrapped
    private AssetIdentity identity;

    @NotNull
    @Enumerated(value = EnumType.STRING)
    private Status status;

    @CreationTimestamp
    private LocalDateTime createdAt;

    public enum Status {
        AVAILABLE, UNAVAILABLE, BROKEN
    }

    public enum Type {
        SEAT, ROOM, PANTRY
    }
}
