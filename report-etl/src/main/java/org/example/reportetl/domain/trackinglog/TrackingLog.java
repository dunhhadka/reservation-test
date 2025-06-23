package org.example.reportetl.domain.trackinglog;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "tracking_logs")
public class TrackingLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int logId;

    private String logType;

    private int rootId;

    @Column(name = "\"offset\"")
    private int offset;

    @NotNull
    private LocalDateTime createdAt;

    public TrackingLog(int logId, String logType, int offset, int orderId) {
        this.logId = logId;
        this.logType = logType;
        this.offset = offset;
        this.rootId = orderId;
    }
}
