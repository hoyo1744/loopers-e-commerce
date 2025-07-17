package com.loopers.domain.user;

import com.loopers.utils.DatabaseCleanUp;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@SpringBootTest
class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @SpyBean
    private UserRepository userRepository;

    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }


    @DisplayName("회원 조회 서비스 통합 테스트")
    @Nested
    public class Get {

        @Test
        @DisplayName("유효하지 않은 회원을 조회할 경우 null이 반환된다.")
        public void returnNull_whenUserIsInvalid() throws Exception{
            //given
            String id = "hoyong.eom";

            //when
            UserInfo.User user = userService.getUser(id);

            //then
            Assertions.assertThat(user).isNull();
        }
    }
    
    @DisplayName("회원 가입 서비스 통합 테스트")
    @Nested
    public class SignUp {
        
        @Test
        @DisplayName("회원 가입시 User 저장이 수행된다. ( spy 검증 )")
        public void saveUser_whenUserSignup() throws Exception{
            //given
            User signUpUser = User.of(
                    "hoyongeom",
                    "1q2w3e4r!@",
                    "hoyong.eom",
                    "hoyong.eom@gmail.com",
                    "010-1234-5678",
                    "2025-04-20",
                    Gender.MALE);

            doReturn(signUpUser).when(userRepository).save(signUpUser);

            
            //when
            UserInfo.User result = userService.signUpUser(signUpUser);


            //then
            assertAll(
                    () -> Assertions.assertThat(result.getId()).isEqualTo(signUpUser.getId()),
                    () -> Assertions.assertThat(result.getName()).isEqualTo(signUpUser.getName()),
                    () -> Assertions.assertThat(result.getEmail()).isEqualTo(signUpUser.getEmail()),
                    () -> Assertions.assertThat(result.getPhoneNumber()).isEqualTo(signUpUser.getPhoneNumber()),
                    () -> Assertions.assertThat(result.getName()).isEqualTo(signUpUser.getName()),
                    () -> Assertions.assertThat(result.getGender()).isEqualTo(signUpUser.getGender().getValue())
            );

            verify(userRepository).save(signUpUser);
        }
        
        
    }
}
