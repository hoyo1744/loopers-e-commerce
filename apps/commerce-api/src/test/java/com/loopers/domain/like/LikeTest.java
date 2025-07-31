package com.loopers.domain.like;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertThrows;


class LikeTest {

    @DisplayName("Like 도메인 생성 테스트")
    @Nested
    class Create {

        /**
         * - [O]  좋아요 등록시, 유저와 상품 ID를 포함한 정보를 반환한다.
         * - [O]  좋아요 등록시, 유저 ID가 존재하지 않는다면 401 Unauthorized 에러를 반환한다.
         * - [O]  좋아요 등록시, 상품 ID가 존재하지 않는다면 404 Not Found 에러를 반환한다.
         */



        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("좋아요 등록시, 유저 ID가 null 또는 빈 문자열이면 401 Unauthorized 에러를 반환한다.")
        public void throw401UnauthorizedException_whenUserIdIsNullOrEmpty(String userId) throws Exception{
            //given & when
            CoreException result = assertThrows(
                    CoreException.class, () -> {
                        Like create = Like.create(userId, 1L);
                    });

            //then
            Assertions.assertThat(result.getErrorType()).isEqualTo(ErrorType.UNAUTHORIZED);
        }

        @ParameterizedTest
        @NullSource
        @ValueSource(longs = {0, -1})
        @DisplayName("좋아요 등록시, 상품 ID가 null 또는 0 이하의 값이면 404 Not Found 에러를 반환한다.")
        public void throw404NotFoundException_whenProductIdIsNullOrLessThanOrEqualToZero(Long productId) throws Exception{
            //given & when
            CoreException result = assertThrows(
                    CoreException.class, () -> {
                        Like create = Like.create("hoyong_eom", productId);
                    });

            //then
            Assertions.assertThat(result.getErrorType()).isEqualTo(ErrorType.NOT_FOUND);
        }

        @Test
        @DisplayName("좋아요 등록시, 유저와 상품 ID를 포함한 정보를 반환한다.")
        public void returnLike_whenUserLikeProduct() throws Exception{
            //given
            String userId = "hoyong_eom";
            Long productId = 1L;

            //when
            Like like = Like.create(userId, productId);

            //then
            Assertions.assertThat(like.getUserId()).isEqualTo(userId);
            Assertions.assertThat(like.getProductId()).isEqualTo(productId);
        }

    }

}
