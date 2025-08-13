package com.loopers.application.point;

import com.loopers.domain.point.PointInfo;
import com.loopers.domain.point.PointService;
import com.loopers.domain.user.UserInfo;
import com.loopers.domain.user.UserService;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PointFacadeTest {

    @InjectMocks
    private PointFacade pointFacade;

    @Mock
    private PointService pointService;

    @Mock
    private UserService userService;

    @DisplayName("PointFacade 유닛 테스트(포인트 조회)")
    @Nested
    public class Get {

        @Test
        @DisplayName("유효한 회원 ID로 포인트 조회 시, 포인트가 반환 된다.")
        public void returnsPoint_whenUserIsValid() throws Exception{
            //given
            String userId = "hoyongeom";
            UserInfo.User mockUser = mock(UserInfo.User.class);
            PointInfo.Point mockPoint = PointInfo.Point.of(1000L, userId);

            when(userService.getUser(userId)).thenReturn(mockUser);
            when(pointService.getPoint(userId)).thenReturn(mockPoint);

            //when
            PointResult.Point result = pointFacade.getPoint(userId);


            //then
            assertThat(result.getPoint()).isEqualTo(1000L);
        }
        
        @Test
        @DisplayName("회원 ID가 제공되지 않은 경우, 404 Not Found 예외를 발생시킨다.")
        public void throwsException_whenUserIdIsNullOrEmpty() throws Exception{
            //given
            String nullId = null;
            String emptyId = "";


            // when, then
            assertThatThrownBy(() -> pointFacade.getPoint(nullId))
                    .isInstanceOf(CoreException.class)
                    .hasMessageContaining("회원 ID는 필수입니다.")
                    .extracting("errorType")
                    .isEqualTo(ErrorType.NOT_FOUND);

            assertThatThrownBy(() -> pointFacade.getPoint(emptyId))
                    .isInstanceOf(CoreException.class)
                    .hasMessageContaining("회원 ID는 필수입니다.")
                    .extracting("errorType")
                    .isEqualTo(ErrorType.NOT_FOUND);
        }

        @Test
        @DisplayName("포인트를 조회하고자하는 회원 ID가 존재하지 않는 경우, 404 Not Found 예외를 발생시킨다.")
        public void throwsException_whenUserNotFound() throws Exception{
            // given
            String userId = "non-existent-user";
            when(userService.getUser(userId)).thenReturn(null);

            // when, then
            assertThatThrownBy(() -> pointFacade.getPoint(userId))
                    .isInstanceOf(CoreException.class)
                    .hasMessageContaining("존재하지 않는 회원입니다.")
                    .extracting("errorType")
                    .isEqualTo(ErrorType.NOT_FOUND);
        }
    }


    @DisplayName("PointFacade 유닛 테스트(포인트 충전)")
    @Nested
    public class Charge {
        @Test
        @DisplayName("유효한 회원이 포인트를 충전하면 충전된 포인트 금액을 반환한다.")
        void returnsChargedPoint_whenUserIsValid() {
            // given
            String userId = "hoyong";
            Long amount = 500L;

            UserInfo.User mockUser = mock(UserInfo.User.class);
            PointInfo.Point chargedPoint = PointInfo.Point.of(1500L, userId);

            when(userService.getUser(userId)).thenReturn(mockUser);
            when(pointService.chargePoint(userId, amount)).thenReturn(chargedPoint);

            // when
            PointResult.ChargedPoint result = pointFacade.charge(userId, amount);

            // then
            assertThat(result.getPoint()).isEqualTo(1500L);
        }

        @Test
        @DisplayName("회원 ID가 null이면 예외가 발생한다.")
        void throwsException_whenUserIdIsNull() {
            // given
            String userId = null;
            Long amount = 1000L;

            // when, then
            assertThatThrownBy(() -> pointFacade.charge(userId, amount))
                    .isInstanceOf(CoreException.class)
                    .hasMessageContaining("회원 ID는 필수입니다.")
                    .extracting("errorType")
                    .isEqualTo(ErrorType.NOT_FOUND);
        }

        @Test
        @DisplayName("존재하지 않는 회원일 경우 예외가 발생한다.")
        void throwsException_whenUserNotFound() {
            // given
            String userId = "non-existent-user";
            Long amount = 1000L;

            when(userService.getUser(userId))
                    .thenThrow(new CoreException(ErrorType.NOT_FOUND, "존재하지 않는 회원입니다."));

            // when & then
            assertThatThrownBy(() -> pointFacade.charge(userId, amount))
                    .isInstanceOf(CoreException.class)
                    .hasMessageContaining("존재하지 않는 회원입니다.")
                    .extracting("errorType")
                    .isEqualTo(ErrorType.NOT_FOUND);

            verify(userService).getUser(userId);
            verifyNoInteractions(pointService);
        }

    }

}
