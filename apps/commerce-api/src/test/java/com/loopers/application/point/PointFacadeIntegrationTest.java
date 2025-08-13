package com.loopers.application.point;

import com.loopers.domain.point.Point;
import com.loopers.domain.point.PointRepository;
import com.loopers.domain.user.Gender;
import com.loopers.domain.user.User;
import com.loopers.domain.user.UserService;
import com.loopers.support.error.CoreException;
import com.loopers.utils.DatabaseCleanUp;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest

class PointFacadeIntegrationTest {

    @Autowired
    private PointFacade pointFacade;

    @Autowired
    private PointRepository pointRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    /**
     * - [O]  해당 ID 의 회원이 존재할 경우, 보유 포인트가 반환된다.
     */

    @DisplayName("포인트 조회 파사드 통합 테스트")
    @Nested
    public class Get {

        @Test
        @DisplayName("유효한 회원 ID의 보유 포인트 조회시, 보유 포인트를 반환한다.")
        public void returnsUserPoints_whenUserIdIsValid() throws Exception{
            //given
            String id = "hoyongeom";
            String password = "1q2w3e4r!@";
            String userName = "hoyong.eom";
            String email = "hoyong.eom@gmail.com";
            String phoneNumber = "010-1234-5678";
            String birthDate = "1994-04-20";
            String gender = "M";

            pointRepository.save(Point.create(id, 0L));

            userService.signUpUser(User.create(
                    id, password, userName, email, phoneNumber, birthDate, Gender.from(gender)
            ));

            pointFacade.charge(id, 100L);

            //when
            PointResult.Point result = pointFacade.getPoint(id);

            //then
            Assertions.assertThat(result.getPoint()).isEqualTo(100L);
        }
    }

    /**
     * - [O]  존재하지 않는 유저 ID 로 충전을 시도한 경우, 실패한다.
     */

    @DisplayName("포인트 충전 파사드 통합 테스트")
    @Nested
    public class Charge {
        
        @Test
        @DisplayName("존재하지 않는 유저 ID 로 충전을 시도한 경우, 실패한다.")
        public void failsToChargePoints_whenUserIdDoesNotExist() throws Exception{
            //given
            String id = "hoyongeom";
            Long chargeAmount = 100L;

            //when & then
            assertThatThrownBy( () -> pointFacade.charge(id, chargeAmount))
                    .isInstanceOf(CoreException.class)
                    .hasMessage("존재하지 않는 사용자입니다.");

        }
        
    }
}
