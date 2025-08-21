package com.loopers.domain.pg;

import com.loopers.domain.payment.CardType;
import com.loopers.infrastructure.pg.PgFeignClient;
import com.loopers.support.error.PgServiceRetryException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@ContextConfiguration(initializers = PgServiceTimeoutTest.MockServerInitializer.class)
class PgServiceTimeoutTest {

    static MockWebServer mockWebServer;

    static class MockServerInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext context) {
            TestPropertyValues.of(
                    "pg.simulator.url=http://localhost:8089"
            ).applyTo(context.getEnvironment());
        }
    }

    @Autowired
    PgService pgService;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start(8089);
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Nested
    @DisplayName("FeignClient ReadTimeout 테스트")
    public class ReadTimeOut {
        @Test
        @DisplayName("ReadTimeout 발생 시 PgServiceRetryException이 발생한다")
        void should_throwException_when_ReadTimeoutOccurs() {
            // given
            mockWebServer.enqueue(new MockResponse()
                    .setBodyDelay(5, TimeUnit.SECONDS)
                    .setBody("{\"meta\":{\"result\":\"SUCCESS\"},\"data\":{}}")
                    .addHeader("Content-Type", "application/json")
            );

            PgCommand.PaymentRequest request = PgCommand.PaymentRequest.of(
                    "orderId",
                    CardType.SAMSUNG,
                    "1234-5678-9012-3456",
                    "1000",
                    "http://localhost:8080/callback"
            );

            // when & then
            Assertions.assertThatThrownBy(() -> pgService.requestPayment("user1", request))
                    .isInstanceOf(PgServiceRetryException.class)
                    .hasMessageContaining("PG 서버가 불안정합니다");
        }
    }


    @Autowired
    PgFeignClient pgFeignClient;

    @Nested
    @DisplayName("Retry가 없을 경우 FeignClient의 예외 테스트")
    public class NoRetry {
        @Test
        @DisplayName("Feign ReadTimeout은 feign.FeignException 처리된다")
        void should_throwRetryableException_onReadTimeout() {
            mockWebServer.enqueue(new MockResponse()
                    .setBodyDelay(5, TimeUnit.SECONDS)
                    .setBody("{\"meta\":{\"result\":\"SUCCESS\"},\"data\":{}}")
                    .addHeader("Content-Type", "application/json")
            );

            PgCommand.PaymentRequest request = PgCommand.PaymentRequest.of(
                    "orderId",
                    CardType.SAMSUNG,
                    "1234-5678-9012-3456",
                    "1000",
                    "http://localhost:8080/callback"
            );

            Assertions.assertThatThrownBy(() -> pgFeignClient.requestPayment("user1", request))
                    .isInstanceOf(feign.FeignException.class)
                    .hasMessageContaining("Read timed out");
        }
    }
}
