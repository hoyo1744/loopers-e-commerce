package com.loopers.domain.stock;

import com.loopers.support.error.CoreException;
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
import java.util.concurrent.*;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
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
        @DisplayName("재고가 부족할 경우 CoreException 예외를 던진다")
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
            ).isInstanceOf(CoreException.class)
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


    @DisplayName("재고 동시성 테스트")
    @Nested
    public class Concurrent {
        @Test
        @DisplayName("동시에 재고 감소해도 성공한 횟수만큼 정확히 감소한다.")
        void concurrentDecrease_withPessimisticLock_shouldDecreaseBySuccessCount() throws Exception {
            // given
            long productId = 10_002L;
            long initialQty = 10_000L;
            long decEach = 100L;
            int threads = 40;

            // 초기 재고 등록
            stockRepository.save(Stock.create(productId, initialQty));

            ExecutorService pool = Executors.newFixedThreadPool(16);
            CountDownLatch start = new CountDownLatch(1);

            try {
                // when
                List<CompletableFuture<Boolean>> jobs = IntStream.range(0, threads)
                        .mapToObj(i -> CompletableFuture.supplyAsync(() -> {
                            try {
                                start.await();
                                stockService.decreaseStock(singleItemCmd(productId, decEach));
                                return true;
                            } catch (CoreException e) {
                                return false;
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                                return false;
                            }
                        }, pool)).toList();

                start.countDown();

                CompletableFuture.allOf(jobs.toArray(new CompletableFuture[0]))
                        .get(10, TimeUnit.SECONDS);

                // then
                long successCount = jobs.stream()
                        .map(CompletableFuture::join)
                        .filter(Boolean::booleanValue)
                        .count();

                long finalQty = stockRepository.findByProductId(productId).getQuantity();

                assertThat(finalQty).isEqualTo(initialQty - (successCount * decEach));
                assertThat(finalQty).isGreaterThanOrEqualTo(0);
                assertThat(successCount).isLessThanOrEqualTo(threads);
            } finally {
                pool.shutdownNow();
            }
        }

        private StockCommand.OrderProducts singleItemCmd(Long productId, long qty) {
            return StockCommand.OrderProducts.of(
                    List.of(StockCommand.OrderProduct.of(productId, qty))
            );
        }
    }
}
