package com.loopers.domain.brand;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BrandTest {


    @DisplayName("Brand 도메인 생성 테스트")
    @Nested
    class Create {
        /**
         * - [O]  브랜드 생성시, 이름과 설명을 포함한 정보를 반환한다.
         * - [O]  브랜드 생성시, 이름이 null 또는 빈 문자열이면 400 Bad Request 에러를 반환한다.
         * - [O]  브랜드 생성시, 설명이 null 또는 빈 문자열이면 400 Bad Request 에러를 반환한다.
         */

        @Test
        @DisplayName("브랜드 생성시, 이름과 설명을 포함한 정보를 반환한다.")
        public void returnBrandWithNameAndDescription_whenCreateBrand() throws Exception{
            //given
            String brandName = "brand";
            String brandDescription = "description";

            //when
            Brand brand = Brand.create(brandName, brandDescription);

            //then
            Assertions.assertThat(brand.getName()).isEqualTo(brandName);
            Assertions.assertThat(brand.getDescription()).isEqualTo(brandDescription);
        }

        @ParameterizedTest
        @DisplayName("브랜드 생성시, 이름이 null 또는 빈 문자열이면 400 Bad Request 에러를 반환한다.")
        @NullAndEmptySource
        public void throw400BadRequest_whenBrandNameIsNullorEmpty(String brandName) throws Exception{
            //given
            String brandDescription = "description";

            //when
            CoreException exception = assertThrows(CoreException.class, () -> {
                Brand.create(brandName, brandDescription);
            });

            // then
            assertThat(exception.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }

        @ParameterizedTest
        @DisplayName("브랜드 생성시, 설명이 null 또는 빈 문자열이면 400 Bad Request 에러를 반환한다.")
        @NullAndEmptySource
        public void throw400BadRequest_whenBrandDescriptionIsNullorEmpty(String brandDescription) throws Exception{
            //given
            String brandName = "brand";

            //when
            CoreException exception = assertThrows(CoreException.class, () -> {
                Brand.create(brandName, brandDescription);
            });

            // then
            assertThat(exception.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }

    }

}
