package com.loopers.interfaces.api.event.like;

import com.loopers.common.kafka.event.EventMessage;
import com.loopers.common.kafka.event.EventType;
import com.loopers.common.kafka.event.like.LikeOutEvent;
import com.loopers.common.kafka.topic.Topics;
import com.loopers.domain.like.LikeEvent;
import com.loopers.domain.product.ProductCommand;
import com.loopers.domain.product.ProductService;
import com.loopers.domain.sender.MessageSender;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class LikeEventListener {
    private final ProductService productService;

    private final MessageSender messageSender;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handle(LikeEvent.Like event) {
        productService.increaseLikeCount(ProductCommand.Product.of(event.getProductId()));
        messageSender.send(Topics.LIKE, event.getProductId().toString(),
                EventMessage.<LikeOutEvent.Changed>builder()
                        .eventType(EventType.LIKE_CHANGED)
                        .version("v1")
                        .payload(LikeOutEvent.Changed.of(event.getUserId(), event.getProductId(), true, LocalDateTime.now()))
                        .build()
        );
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handle(LikeEvent.Unlike event) {
        productService.decreaseLikeCount(ProductCommand.Product.of(event.getProductId()));
        messageSender.send(Topics.LIKE, event.getProductId().toString(),
                EventMessage.<LikeOutEvent.Changed>builder()
                        .eventType(EventType.LIKE_CHANGED)
                        .version("v1")
                        .payload(LikeOutEvent.Changed.of(event.getUserId(), event.getProductId(), false, LocalDateTime.now()))
                        .build()
        );
    }
}
