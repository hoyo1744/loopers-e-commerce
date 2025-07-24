package com.loopers.domain.point;

import com.loopers.utils.DatabaseCleanUp;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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
}
