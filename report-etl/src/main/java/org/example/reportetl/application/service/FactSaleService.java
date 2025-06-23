package org.example.reportetl.application.service;

import lombok.RequiredArgsConstructor;
import org.example.reportetl.domain.factsale.FactSale;
import org.example.reportetl.domain.factsale.FactSaleRepository;
import org.example.reportetl.external.model.Order;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FactSaleService {

    private final FactSaleRepository factSaleRepository;

    public List<FactSale> getStoreAndOrderId(int storeId, int orderId) {
        return this.factSaleRepository.findByStoreIdAndOrderId(storeId, orderId);
    }

    public FactSale initBase(int storeId, Order order, FactSale example, boolean isCreate) {
        return null;
    }
}
