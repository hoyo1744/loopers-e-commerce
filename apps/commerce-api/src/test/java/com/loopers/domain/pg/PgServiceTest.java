package com.loopers.domain.pg;

import com.loopers.domain.payment.CardType;
import com.loopers.support.error.PgServiceRetryException;
import com.loopers.support.error.RetryableBusinessException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.AopTestUtils;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@SpringBootTest
@TestPropertySource(properties = {
        // ---------- Retry(pgRetry) ----------
        "resilience4j.retry.instances.pgRetry.max-attempts=3",
        "resilience4j.retry.instances.pgRetry.wait-duration=1000ms",
        "resilience4j.retry.instances.pgRetry.retry-exceptions=java.lang.RuntimeException," +
                "com.loopers.support.error.RetryableBusinessException",
        "resilience4j.retry.instances.pgRetry.fail-after-max-attempts=true",

        // ---------- CircuitBreaker(pgCircuit) ----------
        "resilience4j.circuitbreaker.instances.pgCircuit.minimum-number-of-calls=5",
        "resilience4j.circuitbreaker.instances.pgCircuit.sliding-window-size=10",
        "resilience4j.circuitbreaker.instances.pgCircuit.failure-rate-threshold=50",
        "resilience4j.circuitbreaker.instances.pgCircuit.wait-duration-in-open-state=10s",
        "resilience4j.circuitbreaker.instances.pgCircuit.permitted-number-of-calls-in-half-open-state=2",
        "resilience4j.circuitbreaker.instances.pgCircuit.slow-call-duration-threshold= 3s",
        "resilience4j.circuitbreaker.instances.pgCircuit.slow-call-rate-threshold= 50",


})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class PgServiceTest {

    @TestConfiguration
    static class TestConfig {
        @Bean
        @Primary
        PgClient pgClient() {
            return Mockito.mock(PgClient.class);
        }

        @Bean
        @Primary
        PgHistoryRepository pgHistoryRepository() {
            return Mockito.mock(PgHistoryRepository.class);
        }

        @Bean
        @Primary
        PgService pgService(PgClient pgClient, PgHistoryRepository pgHistoryRepository) {
            return Mockito.spy(new PgService(pgClient,pgHistoryRepository));
        }
    }


    @Autowired
    PgService pgService;

    @Autowired
    PgClient pgClient;

    @Autowired
    CircuitBreakerRegistry circuitBreakerRegistry;

    @Value("${resilience4j.retry.instances.pgRetry.max-attempts}")
    private int retryMaxAttemps;

    @Value("${resilience4j.circuitbreaker.instances.pgCircuit.wait-duration-in-open-state}")
    private String waitDurationInOpenState;

    @Value("${resilience4j.circuitbreaker.instances.pgCircuit.permitted-number-of-calls-in-half-open-state}")
    private int permittedNumberOfCallsInHalfOpenState;

    private PgService targetSpy;

    @BeforeEach
    void resetMocks() throws Exception {
        CircuitBreaker pgCircuit = circuitBreakerRegistry.circuitBreaker("pgCircuit");
        pgCircuit.reset();
        targetSpy = AopTestUtils.getTargetObject(pgService);
        Mockito.clearInvocations(pgClient, targetSpy);
        Mockito.reset(pgClient, targetSpy);
    }


    private PgCommonResponse<PgInfo.PaymentResult> successResponse() {
        return new PgCommonResponse<>(
                PgCommonResponse.Meta.builder()
                        .result("SUCCESS")
                        .build(),
                PgInfo.PaymentResult.builder()
                        .status("PENDING")
                        .transactionKey("20250818:TR:e2ad1b")
                        .build()
        );
    }

    private PgCommonResponse<PgInfo.PaymentResult> failResponse() {
        return new PgCommonResponse<>(
                PgCommonResponse.Meta.builder()
                        .result("FAIL")
                        .build(),
                null
        );
    }

    private PgCommand.PaymentRequest paymentRequest() {
        return PgCommand.PaymentRequest.of(
                "550e8400-e29b-41d4-a716-446655440000",
                CardType.SAMSUNG,
                "1234-5678-9814-1451",
                "1000",
                "http://localhost:8080/api/v1/payment/callback"
        );
    }

    @DisplayName("@Retry 테스트")
    @Nested
    public class Retry {
        @Test
        @DisplayName("정상 응답이면 재시도 없이 1회 호출로 성공한다.")
        void callOnce_whenReturnSuccess() {
            // given
            String userId = "userId";
            PgCommonResponse<PgInfo.PaymentResult> successResponse = successResponse();
            PgCommand.PaymentRequest paymentRequest = paymentRequest();

            // when
            when(pgClient.requestPayment(userId, paymentRequest))
                    .thenReturn(successResponse);

            PgInfo.PaymentResult result =
                    pgService.requestPayment(userId, paymentRequest);

            // then
            assertThat(result.getTransactionKey()).isEqualTo(successResponse.getData().getTransactionKey());
            verify(pgClient, times(1)).requestPayment(any(), any());
        }

        @Test
        @DisplayName("RetryableBusinessException 발생시 재시도를 시도하고, retryMaxAttemps 번째 성공시 정상 응답을 반환한다.")
        void should_returnSuccess_when_thirdAttemptSucceeds_afterRetryableBusinessExceptions() {
            // given
            PgCommonResponse<PgInfo.PaymentResult> successResponse = successResponse();
            String userId = "userId";
            PgCommand.PaymentRequest paymentRequest = paymentRequest();

            // when
            when(pgClient.requestPayment(eq(userId), any(PgCommand.PaymentRequest.class)))
                    .thenThrow(new RetryableBusinessException("First"))
                    .thenThrow(new RetryableBusinessException("Second"))
                    .thenReturn(successResponse());

            PgInfo.PaymentResult result =
                    pgService.requestPayment(userId, paymentRequest);

            // then
            assertThat(result.getTransactionKey()).isEqualTo(successResponse.getData().getTransactionKey());
            verify(pgClient, times(retryMaxAttemps)).requestPayment(eq(userId), any(PgCommand.PaymentRequest.class));
        }

        @Test
        @DisplayName("정해진 재시도 횟수 초과 시, PgServiceRetryException 예외가 발생한다.")
        void should_throwPgServiceRetryException_when_retryAttemptsExceeded() {
            // given
            String userId = "userId";
            PgCommand.PaymentRequest paymentRequest = paymentRequest();

            // when
            when(pgClient.requestPayment(userId, paymentRequest))
                    .thenReturn(failResponse());

            // then
            assertThatThrownBy(() ->
                    pgService.requestPayment(userId, paymentRequest)
            ).isInstanceOf(PgServiceRetryException.class)
                    .hasMessageContaining("PG 서버가 불안정합니다. 잠시후 재시도해주세요.");

            verify(pgClient, times(retryMaxAttemps)).requestPayment(any(), any());
        }

        @Test
        @DisplayName("정해진 재시도 횟수 초과 시,  requestPaymentRetryFallback 함수가 호출된다.")
        void should_CallRequestPaymentRetryFallbackFunction_when_retryAttemptsExceeded() {
            String userId = "userId";
            PgCommand.PaymentRequest paymentRequest = paymentRequest();
            PgCommonResponse<PgInfo.PaymentResult> failResponse = failResponse();

            when(pgClient.requestPayment(userId, paymentRequest))
                    .thenReturn(failResponse);

            assertThatThrownBy(() ->
                    pgService.requestPayment(userId, paymentRequest)
            ).isInstanceOf(PgServiceRetryException.class)
                    .hasMessageContaining("PG 서버가 불안정합니다. 잠시후 재시도해주세요.");

            verify(pgClient, times(retryMaxAttemps)).requestPayment(any(), any());
            verify(pgService, times(1)).requestPaymentRetryFallback(eq(userId), any(), any());
        }
    }

    @DisplayName("@CircuitBreaker 테스트")
    @Nested
    public class CircuitBreakerTest {
        @Test
        @DisplayName("실패율이 기준 이하이면 CircuitBreaker는 닫힌 상태를 유지한다.")
        void should_not_openCircuitBreaker_when_failureRateIsBelowThreshold() {
            // given
            String userId = "userId";
            PgCommand.PaymentRequest paymentRequest = paymentRequest();
            PgCommonResponse<PgInfo.PaymentResult> successResponse = successResponse();

            // when
            AtomicBoolean failedOnce = new AtomicBoolean(false);

            when(pgClient.requestPayment(userId, paymentRequest))
                    .thenAnswer(invocation -> {
                        if (failedOnce.compareAndSet(false, true)) {
                            return failResponse();
                        }
                        return successResponse;
                    });

            for (int i = 0; i < 3; i++) {
                try {
                    pgService.requestPayment(userId, paymentRequest);
                } catch (Exception ignored) {
                }
            }

            // then
            CircuitBreaker pgCircuit = circuitBreakerRegistry.circuitBreaker("pgCircuit");
            assertThat(pgCircuit.getState()).isEqualTo(CircuitBreaker.State.CLOSED);
            verify(pgClient, atLeast(1)).requestPayment(any(), any());
        }

        @Test
        @DisplayName("실패율이 임계값 이상이면 CircuitBreaker는 열린다.")
        void should_openCircuitBreaker_when_failureRateExceedsThreshold() {
            // given
            String userId = "userId";
            PgCommand.PaymentRequest request = paymentRequest();

            // when
            when(pgClient.requestPayment(eq(userId), any()))
                    .thenThrow(new RetryableBusinessException("fail"));

            for (int i = 0; i < 3; i++) {
                try {
                    pgService.requestPayment(userId, request);
                } catch (Exception ignored) {}
            }

            // then
            CircuitBreaker pgCircuit = circuitBreakerRegistry.circuitBreaker("pgCircuit");
            assertThat(pgCircuit.getState()).isEqualTo(CircuitBreaker.State.OPEN);
        }

        @Test
        @DisplayName("CircuitBreaker가 열린 상태에서는 함수 호출 시, fallback 메서드가 호출된다.")
        void should_callFallback_whenCircuitIsOpen() {
            // given
            String userId = "userId";
            PgCommand.PaymentRequest request = paymentRequest();

            int fallbackCount = 0;

            // when
            when(pgClient.requestPayment(eq(userId), any()))
                    .thenThrow(new RetryableBusinessException("fail"));

            for (int i = 0; i < 3; i++) {
                try {
                    pgService.requestPayment(userId, request);
                    CircuitBreaker pgCircuit = circuitBreakerRegistry.circuitBreaker("pgCircuit");
                    if (pgCircuit.getState().equals(CircuitBreaker.State.OPEN)) {
                        fallbackCount = fallbackCount + 1;
                    }

                } catch (Exception ignored) {}
            }

            Assertions.assertThatThrownBy(
                            () -> pgService.requestPayment(userId, request)
                    )
                    .isInstanceOf(PgServiceRetryException.class)
                    .hasMessageContaining("PG 서버가 불안정합니다. 잠시후 재시도해주세요.");


            //then
            CircuitBreaker pgCircuit = circuitBreakerRegistry.circuitBreaker("pgCircuit");
            assertThat(pgCircuit.getState()).isEqualTo(CircuitBreaker.State.OPEN);
            verify(pgService, atLeast(fallbackCount)).requestPaymentCircuitFallback(eq(userId), any(), any());
        }

        @Test
        @DisplayName("CircuitBreaker가 wait-duration 경과 후 HALF_OPEN 상태로 전환된다.")
        void should_transitionToHalfOpen_afterWaitDuration() throws InterruptedException {
            // given
            String userId = "userId";
            PgCommand.PaymentRequest request = paymentRequest();

            // when
            when(pgClient.requestPayment(eq(userId), any()))
                    .thenThrow(new RetryableBusinessException("fail"));

            for (int i = 0; i < 5; i++) {
                try {
                    pgService.requestPayment(userId, request);
                } catch (Exception ignored) {
                }
            }

            // then
            CircuitBreaker pgCircuit = circuitBreakerRegistry.circuitBreaker("pgCircuit");
            assertThat(pgCircuit.getState()).isEqualTo(CircuitBreaker.State.OPEN);
            long millis = toMillis(waitDurationInOpenState) + 1000;


            Thread.sleep(millis);

            reset(pgClient);
            when(pgClient.requestPayment(eq(userId), any()))
                    .thenReturn(successResponse());
            pgService.requestPayment(userId, request);


            pgCircuit = circuitBreakerRegistry.circuitBreaker("pgCircuit");
            assertThat(pgCircuit.getState()).isEqualTo(CircuitBreaker.State.HALF_OPEN);
        }

        @Test
        @DisplayName("HALF_OPEN 상태에서 성공 요청이 설정 수치 이상이면 CLOSED 상태로 전환된다.")
        void should_closeCircuitBreaker_when_halfOpenRequestsSucceed() throws InterruptedException {
            // given
            String userId = "userId";
            PgCommand.PaymentRequest request = paymentRequest();
            PgCommonResponse<PgInfo.PaymentResult> success = successResponse();

            // when
            when(pgClient.requestPayment(eq(userId), any()))
                    .thenThrow(new RetryableBusinessException("fail"));
            for (int i = 0; i < 3; i++) {
                try {
                    pgService.requestPayment(userId, request);
                } catch (Exception ignored) {}
            }

            long millis = toMillis(waitDurationInOpenState) + 1000;
            Thread.sleep(millis);

            reset(pgClient);
            when(pgClient.requestPayment(eq(userId), any()))
                    .thenReturn(success);

            for (int i = 0; i < permittedNumberOfCallsInHalfOpenState; i++) {
                pgService.requestPayment(userId, request);
            }

            // then
            CircuitBreaker pgCircuit = circuitBreakerRegistry.circuitBreaker("pgCircuit");
            assertThat(pgCircuit.getState()).isEqualTo(CircuitBreaker.State.CLOSED);
        }
    }

    @DisplayName("정상 응답")
    @Nested
    public class Success {

        @Test
        @DisplayName("PG서버가 정상응답이면 SUCCESS를 반환한다.")
        public void returnSuccess_whenPgServerResponseSuccess() throws Exception{
            // given
            String userId = "userId";
            PgCommand.PaymentRequest paymentRequest = paymentRequest();


            PgCommonResponse<PgInfo.PaymentResult> response = successResponse();

            given(pgClient.requestPayment(userId, paymentRequest)).willReturn(response);

            // when
            PgInfo.PaymentResult result = pgService.requestPayment(userId, paymentRequest);

            // then
            Assertions.assertThat(result).isNotNull();
            verify(pgClient, times(1)).requestPayment(userId, paymentRequest);
        }


    }

    @DisplayName("실패")
    @Nested
    public class Fail {

        @Test
        @DisplayName("PG서버 응답이 FAIL이면 PgServiceRetryException 발생")
        void businessFail() {
            when(pgClient.requestPayment(any(), any()))
                    .thenReturn(failResponse());

            Assertions.assertThatThrownBy(() ->
                            pgService.requestPayment("userId", paymentRequest())
                    ).isInstanceOf(PgServiceRetryException.class)
                    .hasMessageContaining("PG 서버가 불안정합니다. 잠시후 재시도해주세요.");

            verify(pgClient, times(3)).requestPayment(any(), any());
        }
    }

    public static long toMillis(String durationStr) {
        return Duration.parse(normalize(durationStr)).toMillis();
    }

    private static String normalize(String input) {
        input = input.trim().toLowerCase();

        if (input.endsWith("ms")) {
            double millis = Double.parseDouble(input.replace("ms", ""));
            double seconds = millis / 1000.0;
            return "PT" + seconds + "S";
        } else if (input.endsWith("s")) {
            return "PT" + input.replace("s", "") + "S";
        } else if (input.endsWith("m")) {
            return "PT" + input.replace("m", "") + "M";
        } else if (input.endsWith("h")) {
            return "PT" + input.replace("h", "") + "H";
        } else {
            throw new IllegalArgumentException("지원하지 않는 단위 형식: " + input);
        }
    }
}
