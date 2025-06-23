package org.example.reportetl.application.service;

import lombok.RequiredArgsConstructor;
import org.example.reportetl.domain.trackinglog.TrackingLogRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TrackingLogBusiness {

    private final TrackingLogRepository trackingLogRepository;

    public boolean wasProcessed(int logId, int orderId, int offset, String logType) {
        return this.trackingLogRepository.existsByLogIdAndLogType(logId, logType);
    }
}
