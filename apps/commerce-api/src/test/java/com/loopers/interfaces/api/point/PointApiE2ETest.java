package com.loopers.interfaces.api.point;

import com.loopers.application.point.PointFacade;
import com.loopers.application.user.AppUserCommand;
import com.loopers.application.user.UserFacade;
import com.loopers.interfaces.api.ApiResponse;
import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PointApiE2ETest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @Autowired
    private PointFacade pointFacade;

    @Autowired
    private UserFacade userFacade;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }


    /**
     * - [O]  포인트 조회에 성공할 경우, 보유 포인트를 응답으로 반환한다.
     * - [O]  `X-USER-ID` 헤더가 없을 경우, `400 Bad Request` 응답을 반환한다.
     */
    @DisplayName("GET /api/v1/points")
    @Nested
    public class Get {

        @Test
        @DisplayName("회원 ID 식별자로 보유 포인트 조회시, 보유 포인트를 응답한다.")
        public void returnsUserPoints_whenQueriedWithValidUserId() throws Exception{
            //given
            String id = "hoyongeom";
            String password = "1q2w3e4r!@";
            String userName = "hoyong.eom";
            String email = "hoyong.eom@gmail.com";
            String phoneNumber = "010-1234-5678";
            String birthDate = "1994-04-20";
            String gender = "M";

            userFacade.signUpUser(AppUserCommand.SignUp.of(
                    id, password, userName, email, phoneNumber, birthDate, gender
            ));

            Long chargeAmount = 1000L;
            pointFacade.charge(id, chargeAmount);

            String requestUrl = "/api/v1/points";

            HttpHeaders headers = new HttpHeaders();
            headers.set("X-USER-ID", id);

            //when
            ParameterizedTypeReference<ApiResponse<PointResponse.Point>> responseType = new ParameterizedTypeReference<>() {
            };
            ResponseEntity<ApiResponse<PointResponse.Point>> response = restTemplate.exchange(requestUrl, HttpMethod.GET, new HttpEntity<>(headers), responseType);

            //then
            Assertions.assertAll(
                    () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK),
                    () -> assertThat(response.getBody().data().getAmount()).isEqualTo(chargeAmount)
            );
        }

        @Test
        @DisplayName("회원 ID 인증 정보가 전달되지 않은 경우, 400 Bad Request 응답을 반환한다.")
        public void returnsBadRequest_whenUserIdIsMissing() throws Exception{
            //given
            String requestUrl = "/api/v1/points";
            HttpHeaders headers = new HttpHeaders();

            //when
            ParameterizedTypeReference<ApiResponse<PointResponse.Point>> responseType = new ParameterizedTypeReference<>() {
            };
            ResponseEntity<ApiResponse<PointResponse.Point>> response = restTemplate.exchange(requestUrl, HttpMethod.GET, new HttpEntity<>(headers), responseType);

            //then
            Assertions.assertAll(
                    () -> Assertions.assertTrue(response.getStatusCode().is4xxClientError()),
                    () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST)
            );
        }

    }


    /***
     * - [O]  존재하는 유저가 1000원을 충전할 경우, 충전된 보유 총량을 응답으로 반환한다.
     * - [O]  존재하지 않는 유저로 요청할 경우, `404 Not Found` 응답을 반환한다.
     */

    @DisplayName("POST /api/v1/points/charge")
    @Nested
    public class Charge {

        @Test
        @DisplayName("존재하는 유저가 1000원을 충전할 경우, 충전 된 보유 포인트로 응답을 반환한다.")
        public void returnsChargedPoints_whenValidUserCharges1000() throws Exception{
            //given
            String id = "hoyongeom";
            String password = "1q2w3e4r!@";
            String userName = "hoyong.eom";
            String email = "hoyong.eom@gmail.com";
            String phoneNumber = "010-1234-5678";
            String birthDate = "1994-04-20";
            String gender = "M";

            userFacade.signUpUser(AppUserCommand.SignUp.of(
                    id, password, userName, email, phoneNumber, birthDate, gender
            ));

            Long chargeAmount = 1000L;
            String requestUrl = "/api/v1/points/charge";
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-USER-ID", id);

            //when
            ParameterizedTypeReference<ApiResponse<PointResponse.ChargedPoint>> responseType = new ParameterizedTypeReference<>() {
            };
            ResponseEntity<ApiResponse<PointResponse.ChargedPoint>> response = restTemplate.exchange(requestUrl, HttpMethod.POST, new HttpEntity<>(chargeAmount, headers), responseType);


            //then
            Assertions.assertAll(
                    () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK),
                    () -> assertThat(response.getBody().data().getAmount()).isEqualTo(chargeAmount)
            );
        }

        @Test
        @DisplayName("유효하지 않은 회원이 충전 요청을 할  경우, 404 Not Found` 응답을 반환한다")
        public void returnsNotFound_whenInvalidUserAttemptsToCharge() throws Exception{
            //given
            String id = "unknownId";

            Long chargeAmount = 1000L;
            String requestUrl = "/api/v1/points/charge";
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-USER-ID", id);

            //when
            ParameterizedTypeReference<ApiResponse<PointResponse.ChargedPoint>> responseType = new ParameterizedTypeReference<>() {
            };
            ResponseEntity<ApiResponse<PointResponse.ChargedPoint>> response = restTemplate.exchange(requestUrl, HttpMethod.POST, new HttpEntity<>(chargeAmount, headers), responseType);

            //then
            Assertions.assertAll(
                    () -> Assertions.assertTrue(response.getStatusCode().is4xxClientError()),
                    () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND)
            );
        }

    }

}
