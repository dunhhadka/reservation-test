package org.example.reportetl.domain.bookingseats;

import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FloorService {

    private final FloorRepository floorRepository;

    @Transactional
    public int createAssets(FloorEditAssetRequest request, int floorId) {
        var floor = this.floorRepository.findById(floorId)
                .orElseThrow(() -> new CustomException("Floor not found"));

        this.validateAssets(request, floor);

        var assets = this.createFloorAssets(request.getAssets(), floor);

        this.updateInfoIfNeed(request, floor);

        floor.addNewAssets(assets);

        this.floorRepository.save(floor);

        return floorId;
    }

    private void updateInfoIfNeed(FloorEditAssetRequest request, Floor floor) {
        floor.setWidth(BigDecimal.valueOf(request.getWidth()));
        floor.setHeight(BigDecimal.valueOf(request.getHeight()));
    }

    private List<Asset> createFloorAssets(List<FloorEditAssetRequest.AssetRequest> assets, Floor floor) {
        if (CollectionUtils.isEmpty(assets)) return Collections.emptyList();

        return assets.stream()
                .map(request -> createAsset(request, floor))
                .toList();
    }

    private Asset createAsset(FloorEditAssetRequest.AssetRequest request, Floor floor) {
        return Asset.builder()
                .floor(floor)
                .type(request.getType())
                .code(request.getCode())
                .createdAt(LocalDateTime.now())
                .identity(createIdentity(request.getIdentityRequest()))
                .status(Asset.Status.AVAILABLE)
                .build();
    }

    private AssetIdentity createIdentity(FloorEditAssetRequest.AssetIdentityRequest request) {
        return AssetIdentity.builder()
                .grid_x(request.getGrid_x())
                .grid_y(request.getGrid_y())
                .width(request.getWidth())
                .height(request.getHeight())
                .build();
    }

    private void validateAssets(FloorEditAssetRequest request, Floor floor) {
        var width = Optional.ofNullable(request.getWidth())
                .orElse(floor.getWidth() == null ? null : floor.getWidth().intValue());
        var height = Optional.ofNullable(request.getHeight())
                .orElse(floor.getHeight() == null ? null : floor.getHeight().intValue());
        if (width == null || height == null) {
            throw new CustomException("Require width and height Before setting floor");
        }

        request.setWidth(width);
        request.setHeight(height);

        if (CollectionUtils.isEmpty(request.getAssets())) return;

        boolean isInValidArea = request.getAssets().stream()
                .anyMatch(asset -> asset.getIdentityRequest().getGrid_x() > width
                        || asset.getIdentityRequest().getHeight() > height);
        if (isInValidArea) throw new CustomException("Invalid Custom Asset Area");
    }

    public FloorResponse getById(int floorId) {
        var floor = this.floorRepository.findById(floorId)
                .orElseThrow();

        var response = new FloorResponse();
        response.setId(floorId);
        response.setBuildingId(floor.getBuilding().getId());
        response.setFloorNumber(floor.getFloorNumber());
        response.setWidth(floor.getWidth());
        response.setHeight(floor.getHeight());

        response.setAssets(this.mappAssets(floor.getAssets(), floor));

        return response;
    }

    private List<AssetResponse> mappAssets(List<Asset> assets, Floor floor) {
        if (CollectionUtils.isEmpty(assets)) return List.of();

        return assets.stream()
                .map(asset ->
                        AssetResponse.builder()
                                .floorId(floor.getId())
                                .type(asset.getType())
                                .code(asset.getCode())
                                .assetIdentity(asset.getIdentity())
                                .status(asset.getStatus())
                                .build())
                .toList();
    }
}
