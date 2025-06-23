package org.example.reportetl.domain.bookingseats;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssetRepository extends JpaRepository<Asset, Integer> {

    List<Asset> findByIdIn(List<Integer> ids);

}
