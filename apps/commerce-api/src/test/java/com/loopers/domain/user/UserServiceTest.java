package com.loopers.domain.user;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Nested
    @DisplayName("회원 조회 유닛 테스트")
    class Get {

        @Test
        @DisplayName("유효하지 않은 회원을 조회할 경우 404 Not Found 예외가 발생한다.")
        void throw404NotFoundException_whenUserIsInvalid() {
            // given
            String id = "hoyong.eom";
            when(userRepository.findById(id)).thenReturn(Optional.empty());

            // when
            CoreException exception = assertThrows(CoreException.class, () -> userService.getUser(id));

            // then
            assertThat(exception.getErrorType()).isEqualTo(ErrorType.NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("회원 가입 유닛 테스트")
    class SignUp {

        @Test
        @DisplayName("회원 가입시 User 저장이 수행된다.")
        void saveUser_whenUserSignup() {
            // given
            User signUpUser = User.create(
                    "hoyongeom",
                    "1q2w3e4r!@",
                    "hoyong.eom",
                    "hoyong.eom@gmail.com",
                    "010-1234-5678",
                    "2025-04-20",
                    Gender.MALE
            );

            when(userRepository.save(signUpUser)).thenReturn(signUpUser);

            // when
            UserInfo.User result = userService.signUpUser(signUpUser);

            // then
            assertAll(
                    () -> assertThat(result.getId()).isEqualTo(signUpUser.getId()),
                    () -> assertThat(result.getName()).isEqualTo(signUpUser.getName()),
                    () -> assertThat(result.getEmail()).isEqualTo(signUpUser.getEmail()),
                    () -> assertThat(result.getPhoneNumber()).isEqualTo(signUpUser.getPhoneNumber()),
                    () -> assertThat(result.getGender()).isEqualTo(signUpUser.getGender().getValue())
            );
            verify(userRepository).save(signUpUser);
        }
    }
}
