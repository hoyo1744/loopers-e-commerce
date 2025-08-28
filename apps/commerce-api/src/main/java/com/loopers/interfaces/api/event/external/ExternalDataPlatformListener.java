package com.loopers.interfaces.api.event.external;

import com.loopers.domain.external.ExternalDataService;
import com.loopers.domain.external.OrderResultPayload;
import com.loopers.domain.order.OrderEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class ExternalDataPlatformListener {

    private final ExternalDataService externalDataService;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handle(OrderEvent.Completed event) {
        externalDataService.send("ORDER", OrderResultPayload.of(event.getOrderId(), event.getOrderNumber(), event.getAmount()));
    }

}
