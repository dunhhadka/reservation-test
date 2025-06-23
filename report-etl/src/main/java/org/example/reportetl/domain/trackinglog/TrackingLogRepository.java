package org.example.reportetl.domain.trackinglog;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TrackingLogRepository extends JpaRepository<TrackingLog, Integer> {

    boolean existsByLogIdAndLogType(int logId, String logType);

}
