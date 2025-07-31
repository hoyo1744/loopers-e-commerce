package com.loopers.domain.product;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ProductTest {

    @DisplayName("Product 도메인 테스트")
    @Nested
    public class Create {


        /**
         * - [O] 유효한 브랜드 ID, 상품명, 가격이 주어졌을 때 Product가 정상적으로 생성된다.
         * - [O] 브랜드 ID가 null 또는 0 이하이면 400 Bad Request 예외를 던진다.
         * - [O] 상품명이 null 또는 공백이면 400 Bad Request 예외를 던진다.
         * - [O] 가격이 null 또는 0 미만이면 400 Bad Request 예외를 던진다..
         */


        @Test
        @DisplayName("유효한 브랜드 ID, 상품명, 가격이 주어졌을 때 Product를 반환한다.")
        void createProduct_success() {
            // given
            Long brandId = 1L;
            String ProductName = "product";
            Long price = 1000L;

            // when
            Product product = Product.create(brandId, ProductName, price);

            // then
            assertThat(product.getBrandId()).isEqualTo(brandId);
            assertThat(product.getName()).isEqualTo(ProductName);
            assertThat(product.getPrice()).isEqualTo(price);
        }

        @ParameterizedTest
        @NullSource
        @ValueSource(longs = {0L, -1L})
        @DisplayName("브랜드 ID가 null 또는 0 이하이면 400 Bad Request 예외를 던진다.")
        void throwException_whenInvalidBrandId(Long brandId) {
            // given
            String productName = "product";
            Long price = 1000L;

            // when
            CoreException exception = assertThrows(CoreException.class, () -> {
                Product.create(brandId, productName, price);
            });

            // then
            assertThat(exception.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" "})
        @DisplayName("상품명이 null 또는 공백이면 400 Bad Request 예외를 던진다.")
        void throwException_whenNameIsNullOrBlank(String name) {
            // given
            Long brandId = 1L;
            Long price = 1000L;

            // when
            CoreException exception = assertThrows(CoreException.class, () -> {
                Product.create(brandId, name, price);
            });

            // then
            assertThat(exception.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }

        @ParameterizedTest
        @NullSource
        @ValueSource(longs = {-1L})
        @DisplayName("가격이 null 또는 음수이면 400 Bad Request 예외를 던진다.")
        void throwException_whenPriceIsInvalid(Long price) {
            // given
            Long brandId = 1L;
            String productName = "product";

            // when
            CoreException exception = assertThrows(CoreException.class, () -> {
                Product.create(brandId, productName, price);
            });

            // then
            assertThat(exception.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
            assertThat(exception.getMessage()).contains("상품 가격");
        }
    }

}
