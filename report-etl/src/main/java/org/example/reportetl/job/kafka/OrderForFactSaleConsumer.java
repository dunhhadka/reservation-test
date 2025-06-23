package org.example.reportetl.job.kafka;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.example.reportetl.application.service.FactSaleService;
import org.example.reportetl.application.service.TrackingLogBusiness;
import org.example.reportetl.application.type.ActionType;
import org.example.reportetl.domain.factsale.FactSale;
import org.example.reportetl.domain.trackinglog.TrackingLog;
import org.example.reportetl.external.model.Order;
import org.example.reportetl.external.model.OrderLog;
import org.example.reportetl.external.utils.JsonUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
// custom hành động của bean
@Service
@Profile("job")
@RequiredArgsConstructor
public class OrderForFactSaleConsumer {

    private static final String LOG_TYPE = "report-etl";

    private boolean isProduction;

    private final Environment environment;

    private final TrackingLogBusiness trackingLogBusiness;

    private final FactSaleService factSaleService;

    @PostConstruct
    public void afterPropertiesSet() {
        log.info("afterPropertiesSet");
        this.isProduction = Arrays.stream(environment.getActiveProfiles())
                .anyMatch(profile -> StringUtils.equals(profile, "live"));
    }

    // listen from kafka
    public void listen(String value, int offset) {
        process(value, offset);
    }

    private void process(String value, int offset) {
        final var orderLog = JsonUtils.unmarshal(value, OrderLog.class);

        // check xem log đã được xử lý hay chưa
        if (trackingLogBusiness.wasProcessed(orderLog.getId(), orderLog.getOrderId(), offset, LOG_TYPE)) {
            if (log.isDebugEnabled()) {
                log.debug("Log with Id: {}, LogType: {} was processed", orderLog.getId(), LOG_TYPE);
            }
            return;
        }

        if (StringUtils.isBlank(orderLog.getData())) {
            if (log.isDebugEnabled()) {
                log.debug("Order Record: {} is missing Data for LogId {}", value, orderLog.getId());
            }
            return;
        }

        int storeId = orderLog.getStoreId();
        int orderId = orderLog.getOrderId();

        final var order = JsonUtils.unmarshal(orderLog.getData(), Order.class);
        if (order.isTest()) {
            return;
        }

        var trackingLog = createTrackingLog(orderLog.getId(), offset, order.getId());

        List<FactSale> allFactSales = new ArrayList<>();
        boolean isCreate = ActionType.ADD.equals(orderLog.getVerb());
        if (!isCreate) {
            allFactSales.addAll(this.factSaleService.getStoreAndOrderId(storeId, orderId));
        }

        FactSale baseFactModel = this.factSaleService.initBase(storeId, order, allFactSales.isEmpty() ? null : allFactSales.get(0), isCreate);
    }

    private TrackingLog createTrackingLog(int logId, int offset, int orderId) {
        return new TrackingLog(
                logId,
                LOG_TYPE,
                offset,
                orderId
        );
    }
}
