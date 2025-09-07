package com.loopers.infrastructure.like;

import com.loopers.domain.like.LikeEvent;
import com.loopers.domain.like.LikeEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Primary
public class LikeEventPublisherImpl implements LikeEventPublisher {

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void publish(LikeEvent.Like event) {
        eventPublisher.publishEvent(event);
    }

    @Override
    public void publish(LikeEvent.Unlike event) {
        eventPublisher.publishEvent(event);
    }
}
