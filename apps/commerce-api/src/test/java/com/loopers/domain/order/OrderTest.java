package com.loopers.domain.order;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrderTest {

    @DisplayName("Order 도메인 생성 테스트")
    @Nested
    class Create {

        @Test
        @DisplayName("유효한 사용자 ID와 상품들로 주문을 생성하면 주문 상태는 PENDING이고 총 가격이 계산된다")
        void shouldCreateOrderWithPendingStatusAndCalculatedTotalPrice() {
            // given
            String userId = "user123";
            OrderCommand.OrderProducts orderProducts = OrderCommand.OrderProducts.of(List.of(
                    OrderCommand.OrderProduct.of(1L, 1000L, 2L), // 총 2000
                    OrderCommand.OrderProduct.of(2L, 1500L, 1L)  // 총 1500
            ));

            // when
            Order order = Order.create(userId, orderProducts);

            // then
            assertThat(order.getUserId()).isEqualTo(userId);
            assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.PENDING);
            assertThat(order.getTotalPrice()).isEqualTo(3500L);
            assertThat(order.getOrderProducts()).hasSize(2);
        }
    }

    @DisplayName("주문 상태 업데이트 테스트")
    @Nested
    class UpdateOrderStatus {

        @Test
        @DisplayName("주문 상태가 유효한 값이면 상태가 정상적으로 변경된다")
        void shouldUpdateOrderStatusSuccessfully_whenValid() {
            // given
            Order order = Order.create("user123", OrderCommand.OrderProducts.of(List.of(
                    OrderCommand.OrderProduct.of(1L, 1000L, 1L)
            )));

            // when
            order.updateOrderStatus(OrderStatus.COMPLETE);

            // then
            assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.COMPLETE);
        }

        @Test
        @DisplayName("주문 상태가 null이면 400 Bad Request 예외가 발생한다")
        void shouldThrow_whenOrderStatusIsNull() {
            // given
            Order order = Order.create("user123", OrderCommand.OrderProducts.of(List.of(
                    OrderCommand.OrderProduct.of(1L, 1000L, 1L)
            )));

            // when & then
            assertThatThrownBy(() -> order.updateOrderStatus(null))
                    .isInstanceOf(CoreException.class)
                    .satisfies(ex -> {
                        CoreException ce = (CoreException) ex;
                        assertThat(ce.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
                        assertThat(ce.getMessage()).contains("주문 상태는 비어있을 수 없습니다");
                    });
        }

        @Test
        @DisplayName("주문 상태가 null이면 400 Bad Request 예외가 발생한다")
        void shouldThrow_whenOrderStatusIsEmpty() {
            // given
            Order order = Order.create("user123", OrderCommand.OrderProducts.of(List.of(
                    OrderCommand.OrderProduct.of(1L, 1000L, 1L)
            )));

            // when & then
            assertThatThrownBy(() -> order.updateOrderStatus(null))
                    .isInstanceOf(CoreException.class)
                    .satisfies(ex -> {
                        CoreException ce = (CoreException) ex;
                        assertThat(ce.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
                        assertThat(ce.getMessage()).contains("주문 상태는 비어있을 수 없습니다");
                    });
        }
    }
}
