package org.example.reportetl.domain.bookingseats;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/floors")
public class FloorController {

    private final FloorService floorService;

    @PostMapping("/{floorId}/assets")
    public FloorResponse createAssets(
            @PathVariable int floorId,
            @RequestBody @Valid FloorEditAssetRequest request
    ) {
        floorService.createAssets(request, floorId);
        return this.floorService.getById(floorId);
    }

    @GetMapping("/{floorId}")
    public FloorResponse getFloorById(@PathVariable int floorId) {
        return this.floorService.getById(floorId);
    }
}
