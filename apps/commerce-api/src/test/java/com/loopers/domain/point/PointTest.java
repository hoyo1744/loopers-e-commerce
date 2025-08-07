package com.loopers.domain.point;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PointTest {


    /**
     * - [O]  0 이하의 정수로 포인트를 충전 시 실패한다.
     *
     */


    @Test
    @DisplayName("0 이하의 정수로 포인트를 충전 시 실패한다.")
    public void failsToChargePoints_whenAmountIsZeroOrNegative() throws Exception{
        //given
        Point point = Point.create("hoyong.eom", 0L);

        //when
        IllegalArgumentException result = assertThrows(
                IllegalArgumentException.class, () -> {
                    point.charge(-1L);
                });

        //then
        Assertions.assertThat(result.getMessage()).isEqualTo("포인트 충전 금액은 0 보다 커야 합니다.");
    }

}
