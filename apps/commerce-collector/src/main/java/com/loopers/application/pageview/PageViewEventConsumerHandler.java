package com.loopers.application.pageview;

import com.loopers.domain.eventhandled.EventHandledService;
import com.loopers.domain.metric.ProductMetricService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class PageViewEventConsumerHandler {
    private final EventHandledService eventHandledService;

    private final ProductMetricService productMetricService;

    @Transactional
    public void handlePageViewEvent(PageViewEventCriteria.View event) {

        boolean isNewEvent = eventHandledService.processIfNotHandled(event.getEventId(), event.getTopic(), event.getPartition(),
                event.getOffset());
        if (!isNewEvent) {
            return;
        }

        productMetricService.upsertPageView(event.getProductId(), event.metricDate(), event.delta(), event.getEventTime());
    }
}
