package org.example.reportetl.domain.factsale;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FactSaleRepository extends JpaRepository<FactSale, Integer> {

    List<FactSale> findByStoreIdAndOrderId(int storeId, int orderId);

}
