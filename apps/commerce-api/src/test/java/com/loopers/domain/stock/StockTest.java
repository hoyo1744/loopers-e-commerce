package com.loopers.domain.stock;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class StockTest {

    @DisplayName("Stock 도메인 생성 테스트")
    @Nested
    class Create {

        @ParameterizedTest
        @NullSource
        @ValueSource(longs = {0, -1})
        @DisplayName("상품 ID가 null 또는 0 이하이면 IllegalArgumentException 예외를 발생시킨다.")
        public void throwBadRequest_whenProductIdInvalid(Long productId) {
            // given & when
            IllegalArgumentException result = assertThrows(
                    IllegalArgumentException.class,
                    () -> Stock.create(productId, 10L)
            );

            // then
            Assertions.assertThat(result.getMessage()).isEqualTo("존재하지 않는 상품 ID 입니다.");
        }

        @ParameterizedTest
        @NullSource
        @ValueSource(longs = {-1})
        @DisplayName("수량이 null 또는 음수이면 IllegalArgumentException 예외를 발생시킨다.")
        public void throwBadRequest_whenQuantityInvalid(Long quantity) {
            // given & when
            IllegalArgumentException result = assertThrows(
                    IllegalArgumentException.class,
                    () -> Stock.create(1L, quantity)
            );

            // then
            Assertions.assertThat(result.getMessage()).isEqualTo("재고 수량은 0 이상이어야 합니다.");
        }

        @Test
        @DisplayName("정상적인 입력값으로 생성 시 Stock 객체를 반환한다.")
        public void createStockSuccessfully() {
            // given
            Long productId = 1L;
            Long quantity = 50L;

            // when
            Stock stock = Stock.create(productId, quantity);

            // then
            Assertions.assertThat(stock.getProductId()).isEqualTo(productId);
            Assertions.assertThat(stock.getQuantity()).isEqualTo(quantity);
        }
    }

    @DisplayName("재고 수량 관련 메서드 테스트")
    @Nested
    class Quantity {

        @Test
        @DisplayName("재고 수량 증가")
        public void shouldIncreaseQuantity() {
            // given
            Stock stock = Stock.create(1L, 10L);

            // when
            stock.incrementQuantity(5L);

            // then
            Assertions.assertThat(stock.getQuantity()).isEqualTo(15L);
        }

        @Test
        @DisplayName("재고 수량 감소")
        public void shouldDecreaseQuantity() {
            // given
            Stock stock = Stock.create(1L, 10L);

            // when
            stock.decrementQuantity(4L);

            // then
            Assertions.assertThat(stock.getQuantity()).isEqualTo(6L);
        }

        @Test
        @DisplayName("요청 수량이 재고 이상이면 IllegalArgumentException 예외를 던진다")
        void hasEnough_shouldThrowException_whenNotEnough() {
            // given
            Stock stock = Stock.create(1L, 5L);

            // when & then
            Assertions.assertThatThrownBy(() -> stock.hasEnough(10L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("재고가 부족합니다.");
        }

        @Test
        @DisplayName("요청 수량이 재고 이하이면 예외를 던지지 않는다")
        void hasEnough_shouldNotThrow_whenEnough() {
            // given
            Stock stock = Stock.create(1L, 10L);

            // when & then
            Assertions.assertThatCode(() -> stock.hasEnough(5L))
                    .doesNotThrowAnyException();
        }

        @ParameterizedTest
        @NullSource
        @ValueSource(longs = {0, -1})
        @DisplayName("요청 수량이 null 또는 1 미만이면 IllegalArgumentException 예외를 발생시킨다.")
        public void shouldThrow_whenRequestedQuantityInvalid(Long requestedQuantity) {
            // given
            Stock stock = Stock.create(1L, 10L);

            // when
            RuntimeException result = assertThrows(RuntimeException.class, () -> stock.hasEnough(requestedQuantity));

            // then
            Assertions.assertThat(result.getMessage()).isEqualTo("요청 수량은 1 이상이어야 합니다.");
        }
    }
}
