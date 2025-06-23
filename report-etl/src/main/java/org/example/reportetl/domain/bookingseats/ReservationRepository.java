package org.example.reportetl.domain.bookingseats;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Integer> {

    @Query("SELECT r " +
            "FROM Reservation r " +
            "WHERE r.assetId = :assetId " +
            "AND r.status != 'CANCELLED' " +
            "AND r.startTime < :endTime " +
            "AND r.endTime > :startTime")
    List<Reservation> getOverlappingReservations(int assetId, LocalDateTime startTime, LocalDateTime endTime);
}
