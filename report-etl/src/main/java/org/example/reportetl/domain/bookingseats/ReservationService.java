package org.example.reportetl.domain.bookingseats;

import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final LockingBusinessService lockingBusinessService;

    private final FloorRepository floorRepository;
    private final EmployeeRepository employeeRepository;
    private final AssetRepository assetRepository;
    private final ReservationRepository reservationRepository;

    @Transactional
    public void create(ReservationRequest request) {
        List<UUID> lockingIdCached = new ArrayList<>();
        try {
            var floor = this.findFloorById(request.getFloorId());

            var employee = this.employeeRepository.findById(request.getEmployeeId())
                    .orElseThrow();

            this.validateRequests(request);

            var assetMap = this.toMapAsset(request);

            var reservationModel = this.mapToModel(request.getAssets());

            this.checkConcurrentLocking(reservationModel);

            this.lockAssetInTimes(reservationModel, employee, lockingIdCached);

            this.checkExistedReservationTimes(assetMap, reservationModel);

            this.createReservations(floor, employee, assetMap, reservationModel);
        } finally {
            this.lockingBusinessService.releaseLocks(lockingIdCached);
        }
    }

    private void checkConcurrentLocking(Map<Integer, ReservationRequestModel> reservationModel) {
        for (var entry : reservationModel.entrySet()) {
            if (this.lockingBusinessService.checkOverlap(entry.getKey(), entry.getValue())) {
                throw new CustomException("Seat " + entry.getKey() + " is being booked or time slot conflicts");
            }
        }
    }

    private void lockAssetInTimes(Map<Integer, ReservationRequestModel> reservationModel, Employee employee, List<UUID> lockingIdCached) {
        reservationModel.entrySet()
                .forEach(entry -> {
                            var lockItem = createLockInfo(entry, employee);
                            if (this.lockingBusinessService.acquireLock(lockItem)) {
                                lockingIdCached.add(lockItem.uuid());
                            }
                        }
                );
    }

    private LockingBusinessService.LockInfo createLockInfo(Map.Entry<Integer, ReservationRequestModel> entry, Employee employee) {
        return new LockingBusinessService.LockInfo(
                entry.getKey(),
                employee.getId(),
                entry.getValue().startTime(),
                entry.getValue().endTime(),
                LocalDateTime.now().plusSeconds(5),
                UUID.randomUUID()
        );
    }

    private void createReservations(
            Floor floor,
            Employee employee,
            Map<Integer, Asset> assetMap,
            Map<Integer, ReservationRequestModel> reservationModel
    ) {
        List<Reservation> reservations = new ArrayList<>();
        for (var entry : reservationModel.entrySet()) {
            var assetId = entry.getKey();
            var startTime = entry.getValue().startTime;
            var endTime = entry.getValue().endTime;

            var reservation = createReservation(
                    assetId, assetMap.get(assetId),
                    floor.getId(), employee.getId(),
                    startTime, endTime
            );

            reservations.add(reservation);
        }

        this.reservationRepository.saveAll(reservations);
    }

    private Reservation createReservation(
            Integer assetId,
            Asset asset,
            int floorId,
            int employeeId,
            LocalDateTime startTime,
            LocalDateTime endTime
    ) {
        assert asset != null;

        return Reservation.builder()
                .assetId(assetId)
                .floorId(floorId)
                .employeeId(employeeId)
                .startTime(startTime)
                .endTime(endTime)
                .assetIdentity(asset.getIdentity())
                .createdAt(LocalDateTime.now())
                .status(Reservation.Status.PENDING)
                .build();
    }

    private Map<Integer, Asset> toMapAsset(ReservationRequest request) {
        var assetRequests = request.getAssets();

        var assetIds = assetRequests.stream()
                .map(ReservationRequest.AssetReservationRequest::getAssetId)
                .distinct()
                .toList();
        if (assetIds.size() != assetRequests.size()) {
            throw new CustomException("Duplicate assets");
        }

        var assets = this.assetRepository.findByIdIn(assetIds);
        var assetMap = assets.stream()
                .collect(Collectors.toMap(Asset::getId, Function.identity()));
        String assetsNotFound = assetIds.stream()
                .filter(id -> !assetMap.containsKey(id))
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        if (!assetsNotFound.isEmpty()) {
            throw new CustomException("Asset not found");
        }

        return assetMap;
    }

    private void checkExistedReservationTimes(
            Map<Integer, Asset> assetMap,
            Map<Integer, ReservationRequestModel> reservationModel
    ) {
        List<CompletableFuture<List<Reservation>>> futures = reservationModel.entrySet()
                .stream()
                .map(entry ->
                        CompletableFuture.supplyAsync(() -> {
                            var assetId = entry.getKey();
                            var startTime = entry.getValue().startTime;
                            var endTime = entry.getValue().endTime;
                            return this.reservationRepository.getOverlappingReservations(assetId, startTime, endTime);
                        }))
                .toList();

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        var overlappingReservationFetched = futures.stream()
                .map(CompletableFuture::join)
                .flatMap(Collection::stream)
                .collect(Collectors.groupingBy(Reservation::getAssetId));

        assetMap.keySet()
                .forEach((key -> {
                    var overlapping = overlappingReservationFetched.getOrDefault(key, List.of());
                    if (!overlapping.isEmpty()) {
                        throw new CustomException("Time slot unavailable for seats: " + key);
                    }
                }));
    }

    private Map<Integer, ReservationRequestModel> mapToModel(List<ReservationRequest.AssetReservationRequest> assets) {
        return assets.stream()
                .sorted(Comparator.comparing(ReservationRequest.AssetReservationRequest::getStartTime))
                .collect(Collectors.toMap(
                        ReservationRequest.AssetReservationRequest::getAssetId,
                        asset -> new ReservationRequestModel(asset.getStartTime(), asset.getEndTime()),
                        (first, second) -> second,
                        LinkedHashMap::new
                ));
    }

    public record ReservationRequestModel(
            LocalDateTime startTime,
            LocalDateTime endTime
    ) {
    }

    private void validateRequests(ReservationRequest request) {
        if (CollectionUtils.isEmpty(request.getAssets())) {
            throw new CustomException("Not empty");
        }

        var assetRequests = request.getAssets();

        var isInValidTime = assetRequests.stream()
                .anyMatch(asset -> asset.getStartTime().isAfter(asset.getEndTime())
                        || asset.getStartTime().isEqual(asset.getEndTime()));
        if (isInValidTime) {
            throw new CustomException("Invalid time inputs");
        }

        var now = LocalDateTime.now();
        var isValidStartTime = assetRequests.stream()
                .anyMatch(asset -> asset.getStartTime().isBefore(now)
                        || asset.getStartTime().isEqual(now));
        if (isValidStartTime) {
            throw new CustomException("Invalid Start Time");
        }
    }

    private Floor findFloorById(int floorId) {
        return this.floorRepository.findById(floorId)
                .orElseThrow();
    }
}
