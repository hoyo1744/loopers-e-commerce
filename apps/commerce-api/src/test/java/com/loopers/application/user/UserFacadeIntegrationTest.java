package com.loopers.application.user;

import com.loopers.domain.user.UserRepository;
import com.loopers.domain.user.UserService;
import com.loopers.support.error.CoreException;
import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class UserFacadeIntegrationTest {

    @Autowired
    private UserFacade userFacade;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    /**
     * - [O]  해당 ID 의 회원이 존재할 경우, 회원 정보가 반환된다.
     * - [O]  해당 ID 의 회원이 존재하지 않을 경우, null 이 반환된다.
     */
    @DisplayName("회원 정보 조회 통합 테스트")
    @Nested
    public class Get {

        @Test
        @DisplayName("유효한 회원 정보 조회시, 회원 정보를 반환한다.")
        public void returnsUserInfo_whenUserIsValid() throws Exception{
            //given
            String id = "hoyongeom";
            String password = "1q2w3e4r!@";
            String userName = "hoyong.eom";
            String email = "hoyong.eom@gmail.com";
            String phoneNumber = "010-1234-5678";
            String birthDate = "1994-04-20";
            String gender = "M";

            AppUserResult.User signUpResult = userFacade.signUpUser(AppUserCommand.SignUp.of(id, password, userName, email, phoneNumber, birthDate, gender));

            //when
            AppUserResult.User result = userFacade.getUser(id);

            //then
            Assertions.assertAll(
                    () -> assertThat(result).isNotNull(),
                    () -> assertThat(result.getId()).isEqualTo(signUpResult.getId()),
                    () -> assertThat(result.getName()).isEqualTo(signUpResult.getName()),
                    () -> assertThat(result.getPhoneNumber()).isEqualTo(signUpResult.getPhoneNumber()),
                    () -> assertThat(result.getBirthDate()).isEqualTo(signUpResult.getBirthDate()),
                    () -> assertThat(result.getGender()).isEqualTo(signUpResult.getGender())
            );
        }
    }

    /**
     * - [O]  이미 가입된 ID 로 회원가입 시도 시, 실패한다.
     */
    @DisplayName("회원 가입 통합 테스트")
    @Nested
    public class SignUp {

        @Test
        @DisplayName("회원 가입시 User가 정상적으로 저장된다.")
        public void failsToCreateUser_whenUserIdIsEmpty() throws Exception{
            //given
            String id = "hoyongeom";
            String password = "1q2w3e4r!@";
            String userName = "hoyong.eom";
            String email = "hoyong.eom@gmail.com";
            String phoneNumber = "010-1234-5678";
            String birthDate = "1994-04-20";
            String gender = "M";
            AppUserResult.User signUpResult
                    = userFacade.signUpUser(AppUserCommand.SignUp.of(id, password, userName, email, phoneNumber, birthDate, gender));

            //when
            AppUserResult.User result = userFacade.getUser(id);

            //then
            Assertions.assertAll(
                    () -> assertThat(result).isNotNull(),
                    () -> assertThat(result.getId()).isEqualTo(signUpResult.getId()),
                    () -> assertThat(result.getName()).isEqualTo(signUpResult.getName()),
                    () -> assertThat(result.getPhoneNumber()).isEqualTo(signUpResult.getPhoneNumber()),
                    () -> assertThat(result.getBirthDate()).isEqualTo(signUpResult.getBirthDate()),
                    () -> assertThat(result.getGender()).isEqualTo(signUpResult.getGender())
            );
        }

        @Test
        @DisplayName("이미 가입된 ID로 회원 가입 시도시, 실패한다.")
        public void failsToCreateUser_whenUserIdAlreadyExists() throws Exception{
            //given
            String id = "hoyongeom";
            String password = "1q2w3e4r!@";
            String userName = "hoyong.eom";
            String email = "hoyong.eom@gmail.com";
            String phoneNumber = "010-1234-5678";
            String birthDate = "1994-04-20";
            String gender = "M";
            userFacade.signUpUser(AppUserCommand.SignUp.of(id, password, userName, email, phoneNumber, birthDate, gender));

            //when, then
            assertThatThrownBy( () -> userFacade.signUpUser(AppUserCommand.SignUp.of(id, password, userName, email, phoneNumber, birthDate, gender)))
                    .isInstanceOf(CoreException.class)
                    .hasMessage("이미 등록된 회원입니다.");
        }
    }


}
