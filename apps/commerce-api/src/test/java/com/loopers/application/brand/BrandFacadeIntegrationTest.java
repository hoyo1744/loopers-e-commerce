package com.loopers.application.brand;

import com.loopers.domain.brand.Brand;
import com.loopers.domain.brand.BrandRepository;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class BrandFacadeIntegrationTest {

    @Autowired
    private BrandFacade brandFacade;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @DisplayName("브랜드 조회 파사드 통합 테스트")
    @Nested
    class Get {

        /**
         * - [O] 브랜드 조회시, 브랜드 이름과 설명을 포함한 정보를 반환한다.
         * - [O] 존재하지 않는 브랜드를 조회할 경우, 404 Not Found 에러를 반환한다.
         * - [0] 브랜드 ID가 누락된 경우, 400 Bad Request 에러를 반환한다.
         */

        @Test
        @DisplayName("브랜드 조회시, 브랜드 이름과 설명을 포함한 정보를 반환한다.")
        public void returnBrand_whenRequestBrand() throws Exception{
            //given
            Brand brand = Brand.create("brand", "description");
            Brand findBrand = brandRepository.save(brand);

            //when
            BrandResult.Brand result = brandFacade.getBrand(BrandCriteria.Search.of(findBrand.getId()));

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
                        brandFacade.getBrand(BrandCriteria.Search.of(nonExistentBrandId));
                    });

            //then
            assertThat(result.getErrorType()).isEqualTo(ErrorType.NOT_FOUND);
        }

        @Test
        @DisplayName("브랜드 ID가 누락된 경우, 400 Bad Request 에러를 반환한다.")
        public void throw400BadRequest_whenBrandIdIsNull() throws Exception{
            //given
            Long nonExistentBrandId = null;

            //when
            CoreException result = assertThrows(
                    CoreException.class, () -> {
                        brandFacade.getBrand(BrandCriteria.Search.of(nonExistentBrandId));
                    });

            //then
            assertThat(result.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }


    }
}
