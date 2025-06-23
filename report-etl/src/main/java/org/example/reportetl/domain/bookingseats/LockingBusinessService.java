package org.example.reportetl.domain.bookingseats;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

@Slf4j
@Service
@RequiredArgsConstructor
public class LockingBusinessService {

    private static final ConcurrentHashMap<String, List<LockInfo>> locks = new ConcurrentHashMap<>();

    public boolean acquireLock(LockInfo lockToAdd) {
        var key = lockToAdd.generateKey();

        return locks.compute(key, (k, exitingLocks) -> {
            if (exitingLocks == null) {
                exitingLocks = new ArrayList<>();
            }

            exitingLocks.removeIf(LockInfo::isExpired);

            for (var lock : exitingLocks) {
                if (checkOverlapRule(lockToAdd.startTime, lockToAdd.endTime).test(lock)) {
                    return exitingLocks;
                }
            }

            exitingLocks.add(lockToAdd);
            return exitingLocks;
        }).contains(lockToAdd);
    }

    @Scheduled(fixedRate = 60000)
    public void cleanExpiredLocks() {
        log.info("Cleaning expired lock");

        this.cleanLocks();
    }

    private void cleanLocks() {
        locks.entrySet().removeIf((entry) -> {
            var existingLocks = entry.getValue();
            if (existingLocks == null || existingLocks.isEmpty()) {
                return true;
            }
            existingLocks.removeIf(LockInfo::isExpired);
            return existingLocks.isEmpty();
        });
    }

    public boolean checkOverlap(int assetId, ReservationService.ReservationRequestModel reservationModel) {
        var key = String.valueOf(assetId);

        if (!locks.containsKey(key)) return false;

        return locks.getOrDefault(key, List.of())
                .stream()
                .anyMatch(lock -> checkOverlapRule(reservationModel.startTime(), reservationModel.endTime()).test(lock));
    }

    private Predicate<LockInfo> checkOverlapRule(LocalDateTime startTime, LocalDateTime endTime) {
        return lock -> lock.startTime.isBefore(endTime)
                && lock.endTime.isAfter(startTime);
    }

    public void releaseLocks(List<UUID> lockingIdCached) {
        this.cleanLocks();

        locks.entrySet().removeIf((entry) -> {
            var existingLocks = entry.getValue();
            if (existingLocks == null || existingLocks.isEmpty()) {
                return false;
            }
            existingLocks.removeIf(lock -> lockingIdCached.contains(lock.uuid));
            return existingLocks.isEmpty();
        });
    }


    public record LockInfo(
            int assetId,
            int employeeId,
            LocalDateTime startTime,
            LocalDateTime endTime,
            LocalDateTime expiredAt,
            UUID uuid
    ) {

        public String generateKey() {
            return String.valueOf(assetId);
        }

        public boolean isExpired() {
            return expiredAt.isBefore(LocalDateTime.now());
        }
    }
}
