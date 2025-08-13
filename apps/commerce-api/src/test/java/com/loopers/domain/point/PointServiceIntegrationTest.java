package com.loopers.domain.point;

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

@SpringBootTest
class PointServiceIntegrationTest {

    @Autowired
    private PointService pointService;

    @Autowired
    private PointRepository pointRepository;

    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }


    /**
     * - [O]  해당 ID 의 회원이 존재하지 않을 경우, null 이 반환된다.
     */

    @DisplayName("포인트 조회 서비스 통합 테스트")
    @Nested
    public class Get {

        @Test
        @DisplayName("해당 ID 의 회원이 존재하지 않을 경우, null 이 반환된다.")
        public void returnsNull_whenUserDoesNotExist() throws Exception{
            //given
            String id = "hoyong.eom";

            //when
            PointInfo.Point point = pointService.getPoint("hoyong.eom");

            //then
            Assertions.assertThat(point).isNull();
        }
    }

    @DisplayName("포인트 동시성 테스트")
    @Nested
    public class Concurrent {

        @Test
        @DisplayName("동시에 충전하면 최종 잔액은 모든 충전 합만큼 증가한다.")
        void concurrentCharge_shouldAccumulateExactly() throws Exception {
            // given
            String userId = "user-c1";
            pointRepository.saveAndFlush(Point.create(userId, 1_000L));
            long initial = pointRepository.findByUserId(userId).orElseThrow().getAmount();

            int threads = 50;
            long chargeEach = 10L; // 50 * 10 = +500

            ExecutorService pool = Executors.newFixedThreadPool(12);
            CountDownLatch start = new CountDownLatch(1);

            // when
            try {
                List<CompletableFuture<Void>> jobs = IntStream.range(0, threads)
                        .mapToObj(i -> CompletableFuture.runAsync(() -> {
                            try {
                                start.await();
                                pointService.chargePoint(userId, chargeEach);
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                            }
                        }, pool))
                        .toList();

                start.countDown();

                // then
                CompletableFuture.allOf(jobs.toArray(new CompletableFuture[0])).get(5, TimeUnit.SECONDS);

                long finalAmount = pointRepository.findByUserId(userId).orElseThrow().getAmount();
                Assertions.assertThat(finalAmount).isEqualTo(initial + threads * chargeEach);
            } finally {
                pool.shutdownNow();
            }
        }
        
        
        @Test
        @DisplayName("충분한 잔액이 존재할 경우 동시에 차감해도 음수가 되지 않고 정확히 감소한다.")
        public void concurrentDeduct_shouldDecreaseExactly_whenBalanceEnough() throws Exception{
            String userId = "user-c2";
            pointRepository.saveAndFlush(Point.create(userId, 10_000L));
            long initial = 10_000L;

            int threads = 40;
            long deductEach = 100L; // 40 * 100 = -4000

            ExecutorService pool = Executors.newFixedThreadPool(12);
            CountDownLatch start = new CountDownLatch(1);

            try {
                List<CompletableFuture<Boolean>> jobs = IntStream.range(0, threads)
                        .mapToObj(i -> CompletableFuture.supplyAsync(() -> {
                            try {
                                start.await();
                                pointService.deductPoint(PointCommand.Use.of(userId, deductEach));
                                return true;
                            } catch (CoreException e) {
                                return false;
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                                return false;
                            }
                        }, pool))
                        .toList();

                start.countDown();
                CompletableFuture.allOf(jobs.toArray(new CompletableFuture[0])).get(5, TimeUnit.SECONDS);

                long success = jobs.stream().map(CompletableFuture::join).filter(b -> b).count();
                long finalAmount = pointRepository.findByUserId(userId).orElseThrow().getAmount();

                Assertions.assertThat(success).isEqualTo(threads);
                Assertions.assertThat(finalAmount).isEqualTo(initial - threads * deductEach);
                Assertions.assertThat(finalAmount).isGreaterThanOrEqualTo(0);
            } finally {
                pool.shutdownNow();
            }
        }

    }

}
