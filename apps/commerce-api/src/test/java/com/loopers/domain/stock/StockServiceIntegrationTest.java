package com.loopers.domain.stock;

import com.loopers.utils.DatabaseCleanUp;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class StockServiceIntegrationTest {

    @Autowired
    private StockService stockService;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @Nested
    @DisplayName("재고 조회/변경 통합 테스트")
    class StockTests {

        @Test
        @DisplayName("재고가 충분할 경우 예외를 던지지 않는다")
        void shouldNotThrow_whenStockIsSufficient() {
            // given
            Long productId = 1L;
            stockRepository.save(Stock.create(productId, 50L));

            // when & then
            Assertions.assertThatCode(() ->
                    stockService.validateStock(
                            StockCommand.OrderProducts.of(
                                    List.of(StockCommand.OrderProduct.of(productId, 30L))
                            )
                    )
            ).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("재고가 부족할 경우 IllegalArgumentException 예외를 던진다")
        void shouldThrowException_whenStockIsInsufficient() {
            // given
            Long productId = 1L;
            stockRepository.save(Stock.create(productId, 30L));

            // when & then
            Assertions.assertThatThrownBy(() ->
                    stockService.validateStock(
                            StockCommand.OrderProducts.of(
                                    List.of(StockCommand.OrderProduct.of(productId, 50L))
                            )
                    )
            ).isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("재고가 부족합니다.");
        }

        @Test
        @DisplayName("재고 증가 시 수량이 증가한다")
        void shouldIncreaseStockQuantity() {
            // given
            Long productId = 1L;
            stockRepository.save(Stock.create(productId, 10L));

            StockCommand.OrderProduct orderProduct = StockCommand.OrderProduct.of(productId, 5L);
            StockCommand.OrderProducts orderProducts = StockCommand.OrderProducts.of(java.util.List.of(orderProduct));

            // when
            stockService.increaseStock(orderProducts);

            // then
            Stock updated = stockRepository.findByProductId(productId);
            assertThat(updated.getQuantity()).isEqualTo(15L);
        }

        @Test
        @DisplayName("재고 감소 시 수량이 감소한다")
        void shouldDecreaseStockQuantity() {
            // given
            Long productId = 1L;
            stockRepository.save(Stock.create(productId, 10L));

            StockCommand.OrderProduct orderProduct = StockCommand.OrderProduct.of(productId, 4L);
            StockCommand.OrderProducts orderProducts = StockCommand.OrderProducts.of(List.of(orderProduct));

            // when
            stockService.decreaseStock(orderProducts);

            // then
            Stock updated = stockRepository.findByProductId(productId);
            assertThat(updated.getQuantity()).isEqualTo(6L);
        }

        @Test
        @DisplayName("getStock 호출 시 상품의 재고 정보를 반환한다")
        void shouldReturnStockInfoByProductId() {
            // given
            Long productId = 1L;
            stockRepository.save(Stock.create(productId, 25L));

            // when
            StockInfo.Stock stockInfo = stockService.getStock(productId);

            // then
            assertThat(stockInfo.getProductId()).isEqualTo(productId);
            assertThat(stockInfo.getQuantity()).isEqualTo(25L);
        }
    }
}
