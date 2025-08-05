package com.loopers.interfaces.api.user;

import com.loopers.domain.user.Gender;
import com.loopers.domain.user.User;
import com.loopers.domain.user.UserRepository;
import com.loopers.domain.user.UserService;
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
class UserApiE2ETest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    /**
     * - [O]  내 정보 조회에 성공할 경우, 해당하는 유저 정보를 응답으로 반환한다.
     * - [O]  존재하지 않는 ID 로 조회할 경우, `404 Not Found` 응답을 반환한다.
     */

    @DisplayName("GET /api/v1/users/me")
    @Nested
    public class Get {

        @Test
        @DisplayName("회원 ID로 정보 조회시, 해당하는 유저 정보를 응답한다.")
        public void returnsUserInfo_whenUserIdIsValid() throws Exception{
            //given
            String id = "hoyongeom";
            String password = "1q2w3e4r!@";
            String userName = "hoyong.eom";
            String email = "hoyong.eom@gmail.com";
            String phoneNumber = "010-1234-5678";
            String birthDate = "2025-04-20";
            String gender = "M";
            User user = User.create(id, password, userName, email, phoneNumber, birthDate, Gender.from(gender));

            userService.signUpUser(user);

            String requestUrl = "/api/v1/users/me";

            HttpHeaders headers = new HttpHeaders();
            headers.set("X-USER-ID", id);

            //when
            ParameterizedTypeReference<ApiResponse<UserResponse.User>> responseType = new ParameterizedTypeReference<>() {
            };
            ResponseEntity<ApiResponse<UserResponse.User>> response = restTemplate.exchange(requestUrl, HttpMethod.GET, new HttpEntity<>(headers), responseType);

            //then
            Assertions.assertAll(
                    () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK),
                    () -> assertThat(response.getBody().data().getId()).isEqualTo(user.getId()),
                    () -> assertThat(response.getBody().data().getName()).isEqualTo(user.getName()),
                    () -> assertThat(response.getBody().data().getPhoneNumber()).isEqualTo(user.getPhoneNumber()),
                    () -> assertThat(response.getBody().data().getEmail()).isEqualTo(user.getEmail()),
                    () -> assertThat(response.getBody().data().getBirthDate()).isEqualTo(user.getBirthDate().toString()),
                    () -> assertThat(response.getBody().data().getGender()).isEqualTo(user.getGender().getValue())
            );
        }


        @Test
        @DisplayName("존재하지 않는 회원 ID로 조회할 경우, `404 Not Found` 응답을 반환한다.")
        public void returns404NotFound_whenUserIdDoesNotExist() throws Exception{
            //given
            String id = "unknownId";
            String requestUrl = "/api/v1/users/me";

            HttpHeaders headers = new HttpHeaders();
            headers.set("X-USER-ID", id);

            //when
            ParameterizedTypeReference<ApiResponse<UserResponse.User>> responseType = new ParameterizedTypeReference<>() {
            };
            ResponseEntity<ApiResponse<UserResponse.User>> response = restTemplate.exchange(requestUrl, HttpMethod.GET, new HttpEntity<>(headers), responseType);

            //then
            Assertions.assertAll(
                    () -> Assertions.assertTrue(response.getStatusCode().is4xxClientError()),
                    () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND)
            );

        }
    }


    /***
     * - [O]  회원 가입이 성공할 경우, 생성된 유저 정보를 응답으로 반환한다.
     * - [O]  회원 가입 시에 성별이 없을 경우, `400 Bad Request` 응답을 반환한다.
     */
    @DisplayName("POST /api/v1/users")
    @Nested
    public class SignUp {
        @Test
        @DisplayName("회원 가입 성공 시, 생성된 유저 정보를 응답으로 반환한다.")
        public void returnsCreatedUserInfo_whenSignUpSucceeds() throws Exception {
            //given
            String userId = "hoyongeom";
            String password = "1q2w3e4r!@";
            String userName = "hoyong.eom";
            String email = "hoyong.eom@gmail.com";
            String phoneNumber = "010-1234-5678";
            String birthDate = "2025-04-20";
            String gender = "M";
            UserRequest.SignUp signUp = UserRequest.SignUp.of(userId, password, userName, email, phoneNumber, birthDate, gender);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            String requestUrl = "/api/v1/users";

            //when
            ParameterizedTypeReference<ApiResponse<UserResponse.User>> responseType = new ParameterizedTypeReference<>() {
            };
            ResponseEntity<ApiResponse<UserResponse.User>> response = restTemplate.exchange(requestUrl, HttpMethod.POST, new HttpEntity<>(signUp, headers), responseType);

            //then
            Assertions.assertAll(
                    () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK),
                    () -> assertThat(response.getBody().data().getId()).isEqualTo(userId),
                    () -> assertThat(response.getBody().data().getName()).isEqualTo(userName),
                    () -> assertThat(response.getBody().data().getPhoneNumber()).isEqualTo(phoneNumber),
                    () -> assertThat(response.getBody().data().getEmail()).isEqualTo(email),
                    () -> assertThat(response.getBody().data().getBirthDate()).isEqualTo(birthDate),
                    () -> assertThat(response.getBody().data().getGender()).isEqualTo(gender)
            );
        }

        @Test
        @DisplayName("회원 가입 시에 성별이 없을 경우, `400 Bad Request` 응답을 반환한다.")
        public void failsToCreateUser_whenGenderIsMissing() throws Exception{
            //given
            String userId = "hoyongeom";
            String password = "1q2w3e4r!@";
            String userName = "hoyong.eom";
            String email = "hoyong.eom@gmail.com";
            String phoneNumber = "010-1234-5678";
            String birthDate = "2025-04-20";
            UserRequest.SignUp signUp = UserRequest.SignUp.of(userId, password, userName, email, phoneNumber, birthDate, null);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            String requestUrl = "/api/v1/users";

            //when
            ParameterizedTypeReference<ApiResponse<UserResponse.User>> responseType = new ParameterizedTypeReference<>() {
            };
            ResponseEntity<ApiResponse<UserResponse.User>> response = restTemplate.exchange(requestUrl, HttpMethod.POST, new HttpEntity<>(signUp, headers), responseType);

            //then
            Assertions.assertAll(
                    () -> Assertions.assertTrue(response.getStatusCode().is4xxClientError()),
                    () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST)
            );
        }

        @Test
        @DisplayName("올바르지 않은 이메일 형식으로 회원가입 요청시, 400 BadRequest 응답을 반환한다.")
        public void returnsBadRequest_whenEmailFormatIsInvalid() throws Exception{
            String userId = "hoyongeom";
            String password = "1q2w3e4r!@";
            String userName = "hoyong.eom";
            String email = "hoyong.eom_gmail.com";
            String phoneNumber = "010-1234-5678";
            String birthDate = "2025-04-20";
            UserRequest.SignUp signUp = UserRequest.SignUp.of(userId, password, userName, email, phoneNumber, birthDate, "M");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            String requestUrl = "/api/v1/users";

            //when
            ParameterizedTypeReference<ApiResponse<UserResponse.User>> responseType = new ParameterizedTypeReference<>() {
            };
            ResponseEntity<ApiResponse<UserResponse.User>> response = restTemplate.exchange(requestUrl, HttpMethod.POST, new HttpEntity<>(signUp, headers), responseType);

            //then
            Assertions.assertAll(
                    () -> Assertions.assertTrue(response.getStatusCode().is4xxClientError()),
                    () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST)
            );
        }

        @Test
        @DisplayName("회원 가입 시, 회원 ID가 전달되지 않는다면 400 BadRequest 응답을 반환한다.")
        public void returnsBadRequest_whenIdIsBlank() throws Exception{
            String userId = "   ";
            String password = "1q2w3e4r!@";
            String userName = "hoyong.eom";
            String email = "hoyong.eom_gmail.com";
            String phoneNumber = "010-1234-5678";
            String birthDate = "2025-04-20";
            UserRequest.SignUp signUp = UserRequest.SignUp.of(userId, password, userName, email, phoneNumber, birthDate, "M");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            String requestUrl = "/api/v1/users";

            //when
            ParameterizedTypeReference<ApiResponse<UserResponse.User>> responseType = new ParameterizedTypeReference<>() {
            };
            ResponseEntity<ApiResponse<UserResponse.User>> response = restTemplate.exchange(requestUrl, HttpMethod.POST, new HttpEntity<>(signUp, headers), responseType);

            //then
            Assertions.assertAll(
                    () -> Assertions.assertTrue(response.getStatusCode().is4xxClientError()),
                    () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST)
            );
        }

        @Test
        @DisplayName("회원 가입 시, PASSWORD가 전달되지 않는다면 400 BadRequest 응답을 반환한다.")
        public void returnsBadRequest_whenPasswordIsBlank() throws Exception{
            String userId = "hoyongeom";
            String password = " ";
            String userName = "hoyong.eom";
            String email = "hoyong.eom_gmail.com";
            String phoneNumber = "010-1234-5678";
            String birthDate = "2025-04-20";
            UserRequest.SignUp signUp = UserRequest.SignUp.of(userId, password, userName, email, phoneNumber, birthDate, "M");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            String requestUrl = "/api/v1/users";

            //when
            ParameterizedTypeReference<ApiResponse<UserResponse.User>> responseType = new ParameterizedTypeReference<>() {
            };
            ResponseEntity<ApiResponse<UserResponse.User>> response = restTemplate.exchange(requestUrl, HttpMethod.POST, new HttpEntity<>(signUp, headers), responseType);

            //then
            Assertions.assertAll(
                    () -> Assertions.assertTrue(response.getStatusCode().is4xxClientError()),
                    () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST)
            );
        }

        @Test
        @DisplayName("회원 가입 시, 이름 이/가 전달되지 않는다면 400 BadRequest 응답을 반환한다.")
        public void returnsBadRequest_whenNameIsBlank() throws Exception{
            String userId = "hoyongeom";
            String password = "1q2w3e4r!@";
            String userName = "   ";
            String email = "hoyong.eom_gmail.com";
            String phoneNumber = "010-1234-5678";
            String birthDate = "2025-04-20";
            UserRequest.SignUp signUp = UserRequest.SignUp.of(userId, password, userName, email, phoneNumber, birthDate, "M");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            String requestUrl = "/api/v1/users";

            //when
            ParameterizedTypeReference<ApiResponse<UserResponse.User>> responseType = new ParameterizedTypeReference<>() {
            };
            ResponseEntity<ApiResponse<UserResponse.User>> response = restTemplate.exchange(requestUrl, HttpMethod.POST, new HttpEntity<>(signUp, headers), responseType);

            //then
            Assertions.assertAll(
                    () -> Assertions.assertTrue(response.getStatusCode().is4xxClientError()),
                    () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST)
            );
        }

        @Test
        @DisplayName("회원 가입 시 휴대폰 번호 형식이 잘못된 경우 400 BadRequest 응답을 반환한다.")
        public void returnsBadRequest_whenPhoneNumberIsInvalid() throws Exception{
            String userId = "hoyongeom";
            String password = "1q2w3e4r!@";
            String userName = "hoyongeom";
            String email = "hoyong.eom_gmail.com";
            String phoneNumber = "0101-1234-5678";
            String birthDate = "2025-04-20";
            UserRequest.SignUp signUp = UserRequest.SignUp.of(userId, password, userName, email, phoneNumber, birthDate, "M");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            String requestUrl = "/api/v1/users";

            //when
            ParameterizedTypeReference<ApiResponse<UserResponse.User>> responseType = new ParameterizedTypeReference<>() {
            };
            ResponseEntity<ApiResponse<UserResponse.User>> response = restTemplate.exchange(requestUrl, HttpMethod.POST, new HttpEntity<>(signUp, headers), responseType);

            //then
            Assertions.assertAll(
                    () -> Assertions.assertTrue(response.getStatusCode().is4xxClientError()),
                    () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST)
            );
        }


    }
}

