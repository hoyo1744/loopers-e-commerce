package com.loopers.interfaces.api.event.like;

import com.loopers.domain.like.LikeEvent;
import com.loopers.domain.product.ProductCommand;
import com.loopers.domain.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class LikeEventListener {
    private final ProductService productService;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    @Async
    public void handle(LikeEvent.Like event) {
        productService.increaseLikeCount(ProductCommand.Product.of(event.getProductId()));
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    @Async
    public void handle(LikeEvent.Unlike event) {
        productService.decreaseLikeCount(ProductCommand.Product.of(event.getProductId()));
    }
}
