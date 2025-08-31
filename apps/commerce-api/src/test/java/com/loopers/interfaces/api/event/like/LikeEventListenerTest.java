package com.loopers.interfaces.api.event.like;

import com.loopers.domain.like.LikeEvent;
import com.loopers.domain.product.ProductCommand;
import com.loopers.domain.product.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class LikeEventListenerTest {

    private final ProductService productService = mock(ProductService.class);
    private final LikeEventListener likeEventListener = new LikeEventListener(productService);

    @Nested
    @DisplayName("Like 이벤트 리스너 테스트")
    class Event {

        @Test
        @DisplayName("Like 이벤트를 수신한다면, increaseLikeCount 함수가 호출된다.")
        void callIncreaseLikeCount_whenLikeEvent_received() {
            // given
            Long productId = 1L;
            String userId = "user123";

            LikeEvent.Like event = LikeEvent.Like.of(productId, userId);

            // when
            likeEventListener.handle(event);

            // then
            ArgumentCaptor<ProductCommand.Product> captor = ArgumentCaptor.forClass(ProductCommand.Product.class);
            verify(productService).increaseLikeCount(captor.capture());
            assertThat(captor.getValue().getProductId()).isEqualTo(productId);
        }

        @Test
        @DisplayName("Unlike 이벤트를 수신한다면, decreaseLikeCount 함수가 호출된다.")
        void callDecreaseLikeCount_whenUnlikeEvent_received() {
            // given
            Long productId = 1L;
            String userId = "user123";

            LikeEvent.Unlike event = LikeEvent.Unlike.of(productId, userId);

            // when
            likeEventListener.handle(event);

            // then
            ArgumentCaptor<ProductCommand.Product> captor = ArgumentCaptor.forClass(ProductCommand.Product.class);
            verify(productService).decreaseLikeCount(captor.capture());
            assertThat(captor.getValue().getProductId()).isEqualTo(productId);
        }
    }
}
