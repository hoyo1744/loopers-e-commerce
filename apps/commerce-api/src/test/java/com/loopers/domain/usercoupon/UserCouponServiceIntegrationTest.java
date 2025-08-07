package com.loopers.domain.usercoupon;

import com.loopers.support.error.CoreException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.concurrent.*;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class UserCouponServiceIntegrationTest {

    @Autowired
    private UserCouponRepository userCouponRepository;

    @Autowired
    private UserCouponService userCouponService;

    @Nested
    @DisplayName("쿠폰 사용 동시성 테스트")
    class Concurrent {

        @Test
        @DisplayName("동시에 같은 쿠폰을 사용하면 단 1개만 성공하고 나머지는 이미 사용된 쿠폰 예외(409 Conflict)가 발생한다.")
        public void shouldAllowOnlyOneSuccess_whenMultipleThreadsUseSameCouponConcurrently() throws Exception{
            // given
            UserCoupon userCoupon = userCouponRepository.save(UserCoupon.create("user-1", 10L));
            int threads = 20;
            ExecutorService executor = Executors.newFixedThreadPool(threads);
            CountDownLatch startLatch = new CountDownLatch(1);

            // when
            try {
                List<CompletableFuture<Boolean>> futures = IntStream.range(0, threads)
                        .mapToObj(i -> CompletableFuture.supplyAsync(() -> {
                            try {
                                startLatch.await();
                                userCouponService.useCoupon(userCoupon.getId());
                                return true; // 성공
                            } catch (CoreException e) {
                                return false;
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                                return false;
                            }
                        }, executor))
                        .toList();

                startLatch.countDown();

                CompletableFuture<Void> all =
                        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
                all.get(5, TimeUnit.SECONDS);

                long successCount = futures.stream()
                        .map(CompletableFuture::join)
                        .filter(b -> b)
                        .count();

                // then
                assertThat(successCount).isEqualTo(1);

                UserCoupon after = userCouponRepository.findById(userCoupon.getId());
                assertThat(after.getUserCouponStatus()).isEqualTo(UserCouponStatus.USED);
                assertThat(after.getUsedAt()).isNotNull();

            } finally {
                executor.shutdownNow();
            }
        }
    }


}
