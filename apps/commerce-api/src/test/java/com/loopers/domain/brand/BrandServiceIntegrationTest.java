package com.loopers.domain.brand;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import com.loopers.utils.DatabaseCleanUp;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class BrandServiceIntegrationTest {

    @Autowired
    BrandService brandService;

    @Autowired
    BrandRepository brandRepository;

    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @DisplayName("브랜드 조회 서비스 통합 테스트")
    @Nested
    class Get {

        /**
         * - [O] 브랜드 조회시, 브랜드 이름과 설명을 포함한 정보를 반환한다.
         * - [O] 존재하지 않는 브랜드를 조회할 경우, 404 Not Found 에러를 반환한다.
         */

        @Test
        @DisplayName("브랜드 조회시, 브랜드 이름과 설명을 포함한 정보를 반환한다.")
        public void returnBrand_whenRequestBrand() throws Exception{
            //given
            Brand brand = Brand.create("brand", "description");
            Brand findBrand = brandRepository.save(brand);

            //when
            BrandInfo.Brand result = brandService.getBrand(BrandCommand.Search.of(findBrand.getId()));

            //then
            assertThat(result.getName()).isEqualTo(findBrand.getName());
            assertThat(result.getDescription()).isEqualTo(findBrand.getDescription());
        }

        @Test
        @DisplayName("존재하지 않는 브랜드를 조회할 경우, 404 Not Found 에러를 반환한다.")
        public void throw404NotFound_whenNotExistBrand() throws Exception{
            //given
            Long nonExistentBrandId = 999L;

            //when
            CoreException result = assertThrows(
                    CoreException.class, () -> {
                        brandService.getBrand(BrandCommand.Search.of(nonExistentBrandId));
                    });

            //then
            assertThat(result.getErrorType()).isEqualTo(ErrorType.NOT_FOUND);

        }

    }



}
