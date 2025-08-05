package com.loopers.application.user;

import com.loopers.domain.user.User;
import com.loopers.domain.user.UserInfo;
import com.loopers.domain.user.UserService;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserFacadeTest {

    @InjectMocks
    private UserFacade userFacade;

    @Mock
    private UserService userService;


    @DisplayName("UserFacade 유닛 테스트(회원 조회)")
    @Nested
    public class Get {

        @Test
        @DisplayName("존재하지 않은 회원 ID로 조회하는 경우, 404 Not Found 예외가 발생한다.")
        public void throwsException_whenUserNotFound() throws Exception{
            //given
            String userId = "invalidUser";
            when(userService.getUser(userId)).thenReturn(null);

            //when, then
            Assertions.assertThatThrownBy(
                    () -> userFacade.getUser(userId))
                    .isInstanceOf(CoreException.class)
                    .hasMessageContaining("존재하지 않는 회원입니다.")
                    .extracting("errorType")
                    .isEqualTo(ErrorType.NOT_FOUND);
        }

        @Test
        @DisplayName("유효한 회원 ID일 경우, 회원 정보를 반환한다.")
        void returnsUser_whenUserExists() {
            // given
            String userId = "valid-user";
            UserInfo.User user = UserInfo.User.of(
                    "hoyong", "hoyong", "hoyongeom@gmail.com", "010-1234-5678", "2025-04-20", "M"
            );

            when(userService.getUser(userId)).thenReturn(user);

            // when
            AppUserResult.User result = userFacade.getUser(userId);

            // then
            Assertions.assertThat(result.getId()).isEqualTo("hoyong");
            Assertions.assertThat(result.getName()).isEqualTo("hoyong");
            Assertions.assertThat(result.getEmail()).isEqualTo("hoyongeom@gmail.com");
            Assertions.assertThat(result.getPhoneNumber()).isEqualTo("010-1234-5678");
            Assertions.assertThat(result.getBirthDate()).isEqualTo("2025-04-20");
            Assertions.assertThat(result.getGender()).isEqualTo("M");
        }

    }


    @DisplayName("UserFacade 유닛 테스트(회원 가입)")
    @Nested
    public class SignUp {

        @Test
        @DisplayName("이미 등록된 회원일 경우, 예외가 발생한다")
        void signUpUser_shouldThrow_whenUserAlreadyExists() {
            // given
            UserCommand.SignUp signUpCommand = mock(UserCommand.SignUp.class);
            User domainUser = mock(User.class);

            when(signUpCommand.toDomainUser()).thenReturn(domainUser);
            when(userService.signUpUser(domainUser)).thenThrow(new CoreException(ErrorType.CONFLICT, "이미 등록된 회원입니다."));

            // when & then
            Assertions.assertThatThrownBy(() -> userFacade.signUpUser(signUpCommand))
                    .isInstanceOf(CoreException.class)
                    .hasMessageContaining("이미 등록된 회원입니다.")
                    .extracting("errorType")
                    .isEqualTo(ErrorType.CONFLICT);
        }

        @Test
        @DisplayName("회원 가입 후, 회원 정보를 반환 받는다.")
        public void returnsUser_whenSignUpSucceeds() throws Exception{
            //given
            String userId = "hoyongeom";

            User mockUser = mock(User.class);
            UserInfo.User newUser = UserInfo.User.of(
                    userId, "hoyongeom", "hoyongeom@gmail.com", "010-1234-5678", "2025-04-20", "M"
            );
            AppUserResult.User expectedUser = AppUserResult.User.of(
                    userId, "hoyongeom", "hoyongeom@gmail.com", "010-1234-5678", "2025-04-20", "M"
            );

            UserCommand.SignUp signUpCommand = mock(UserCommand.SignUp.class);

            when(signUpCommand.toDomainUser()).thenReturn(mockUser);
            when(userService.signUpUser(mockUser)).thenReturn(newUser);

            //when
            AppUserResult.User result = userFacade.signUpUser(signUpCommand);

            //then
            assertAll(
                    () -> Assertions.assertThat(result.getId()).isEqualTo(expectedUser.getId()),
                    () -> Assertions.assertThat(result.getName()).isEqualTo(expectedUser.getName()),
                    () -> Assertions.assertThat(result.getEmail()).isEqualTo(expectedUser.getEmail()),
                    () -> Assertions.assertThat(result.getPhoneNumber()).isEqualTo(expectedUser.getPhoneNumber()),
                    () -> Assertions.assertThat(result.getBirthDate()).isEqualTo(expectedUser.getBirthDate()),
                    () -> Assertions.assertThat(result.getGender()).isEqualTo(expectedUser.getGender())
            );
        }

    }




}
