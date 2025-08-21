package com.loopers.scheduler;

import com.loopers.domain.order.OrderService;
import com.loopers.domain.payment.CardType;
import com.loopers.domain.payment.PaymentService;
import com.loopers.domain.pg.*;
import com.loopers.support.error.PgServiceUnavailableException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class PgRetrySchedulerTest {

    @InjectMocks
    private PgRetryScheduler scheduler;

    @Mock
    private PgHistoryService historyService;

    @Mock
    private OrderService orderService;

    @Mock
    private PgService pgService;

    @Mock
    private PaymentService paymentService;


    @DisplayName("스케쥴링 단위 테스트")
    @Nested
    public class Schedule {

        @Test
        @DisplayName("스케쥴러에서 모든 실패 결제를 재처리한다.")
        public void retryAll_whenExistPaymentIsFailed() throws Exception{
            // given
            PgHistoryInfo.Failed failed1 = PgHistoryInfo.Failed.of("user1", "order1", "1234-5678-9814-1451", 1000L, CardType.SAMSUNG);
            PgHistoryInfo.Failed failed2 = PgHistoryInfo.Failed.of("user2", "order2",  "1234-5678-9814-1451", 2000L, CardType.HYUNDAI);

            given(historyService.getPaymentFailedList()).willReturn(List.of(failed1, failed2));

            // when
            scheduler.retryFailedPgRequests();

            // then
            verify(pgService, times(1)).requestPayment(eq("user1"), any());
            verify(pgService, times(1)).requestPayment(eq("user2"), any());
            verify(paymentService, times(2)).pay(any());
            verify(orderService, times(2)).complete(any());
            verify(historyService, times(2)).complete(any());
            verify(historyService, never()).touch(any());
        }

        @Test
        @DisplayName("일부 재처리 실패 케이스가 존재할 경우, touch를 호출해 업데이트 시간을 변경한다.")
        void callTouch_whenExistRetryFailed() {
            // given
            PgHistoryInfo.Failed failed1 = PgHistoryInfo.Failed.of("user1", "order1", "1234-5678-9814-1451", 1000L, CardType.SAMSUNG);
            PgHistoryInfo.Failed failed2 = PgHistoryInfo.Failed.of("user2", "order2", "1234-5678-9814-1451", 2000L, CardType.HYUNDAI);

            given(historyService.getPaymentFailedList()).willReturn(List.of(failed1, failed2));

            PgInfo.PaymentResult mockResult = mock(PgInfo.PaymentResult.class);
            given(pgService.requestPayment(eq("user1"), any(PgCommand.PaymentRequest.class)))
                    .willReturn(mockResult);

            willThrow(new PgServiceUnavailableException("retry failed"))
                    .given(pgService)
                    .requestPayment(eq("user2"), any(PgCommand.PaymentRequest.class));

            // when
            scheduler.retryFailedPgRequests();

            // then
            verify(pgService, times(1)).requestPayment(eq("user1"), any());
            verify(pgService, times(1)).requestPayment(eq("user2"), any());

            verify(historyService, times(1)).complete("order1");
            verify(historyService, times(1)).touch(List.of("order2"));
        }

    }





}
