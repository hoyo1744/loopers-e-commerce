package com.loopers.interfaces.api.brand;

import com.loopers.application.brand.BrandFacade;
import com.loopers.domain.brand.Brand;
import com.loopers.domain.brand.BrandRepository;
import com.loopers.interfaces.api.ApiResponse;
import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BrandV1E2ETest {

    @Autowired
    private TestRestTemplate restTemplate;

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

    @BeforeEach
    void setUp() {
        brandRepository.save(Brand.create("나이키", "나이키입니다."));
        brandRepository.save(Brand.create("아디다스", "아디다스입니다."));
        brandRepository.save(Brand.create("퓨마", "퓨마입니다."));
        brandRepository.save(Brand.create("리복", "리복입니다."));
    }

    @DisplayName("GET /api/v1/brands/{brandId}")
    @Nested
    public class Get {

        @Test
        @DisplayName("존재하지 않는 브랜드ID 식별자로 브랜드 정보 조회시, 404 Not Found 에러가 발생한다.")
        public void throw404NotFound_whenProvideNotExistBrandId() throws Exception{
            //given
            Long notExistBrandId = 1000L;
            String requestUrl = "/api/v1/brands/" + String.valueOf(notExistBrandId);

            //when
            ParameterizedTypeReference<ApiResponse<BrandResponse.Brand>> responseType = new ParameterizedTypeReference<>() {
            };
            ResponseEntity<ApiResponse<BrandResponse.Brand>> response = restTemplate.exchange(requestUrl, HttpMethod.GET, HttpEntity.EMPTY, responseType);

            //then
            Assertions.assertAll(
                    () -> Assertions.assertTrue(response.getStatusCode().is4xxClientError()),
                    () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND)
            );
        }

        @Test
        @DisplayName("브랜드ID 식별자로 브랜드 정보 조회시, 브랜드 정보를 반환한다.")
        public void returnBrandInfo_whenProvideBrandId() throws Exception{
            //given
            Long nikeBrandId = 1L;
            String requestUrl = "/api/v1/brands/" + String.valueOf(nikeBrandId);

            //when
            ParameterizedTypeReference<ApiResponse<BrandResponse.Brand>> responseType = new ParameterizedTypeReference<>() {
            };
            ResponseEntity<ApiResponse<BrandResponse.Brand>> response = restTemplate.exchange(requestUrl, HttpMethod.GET, HttpEntity.EMPTY, responseType);


            //then
            Assertions.assertAll(

                    () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK),
                    () -> assertThat(response.getBody().data().equals(
                            BrandResponse.Brand.of("나이키", "나이키입니다.")
                    )));
        }
    }
}
