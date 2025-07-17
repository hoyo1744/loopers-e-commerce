package com.loopers.domain.user;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserTest {

    /**
     * - [O]  ID 가 `영문 및 숫자 10자 이내` 형식에 맞지 않으면, User 객체 생성에 실패한다.
     * - [O]  이메일이 `xx@yy.zz` 형식에 맞지 않으면, User 객체 생성에 실패한다.
     * - [O]  생년월일이 `yyyy-MM-dd` 형식에 맞지 않으면, User 객체 생성에 실패한다.
     */

    @DisplayName("User 도메인 생성 테스트")
    @Nested
    class Create {

        @Test
        @DisplayName("유저 ID가 비어있다면, BAD_REQUEST 예외가 발생한다.")
        public void failsToCreateUser_whenUserIdIsEmpty() throws Exception{
            //given
            String emptyUserId = "";
            String password = "password";
            String userName = "userName";
            String email = "email@loopers.com";
            String phoneNumber = "010-1234-5678";
            String birthDate = "1994-04-20";

            //when
            CoreException result = assertThrows(
                    CoreException.class, () -> {
                        User.of(
                                emptyUserId,
                                password,
                                userName,
                                email,
                                phoneNumber,
                                birthDate,
                                Gender.MALE
                        );
                    });

            //then
            assertThat(result.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }

        @Test
        @DisplayName("password가 비어있다면, BAD_REQUEST 예외가 발생한다.")
        public void failsToCreateUser_whenPasswordIsEmpty() throws Exception{
            //given
            String userId = "userId";
            String emptyPassword = "";
            String userName = "userName";
            String email = "email@loopers.com";
            String phoneNumber = "010-1234-5678";
            String birthDate = "1994-04-20";

            //when
            CoreException result = assertThrows(
                    CoreException.class, () -> {
                        User.of(
                                userId,
                                emptyPassword,
                                userName,
                                email,
                                phoneNumber,
                                birthDate,
                                Gender.MALE
                        );
                    });

            //then
            assertThat(result.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }

        @Test
        @DisplayName("유저 이름이 비어있다면, BAD_REQUEST 예외가 발생한다.")
        public void failsToCreateUser_whenUserNameIsEmpty() throws Exception{
            //given
            String userId = "userId";
            String password = "password";
            String emptyUserName = "";
            String email = "email@loopers.com";
            String phoneNumber = "010-1234-5678";
            String birthDate = "1994-04-20";

            //when
            CoreException result = assertThrows(
                    CoreException.class, () -> {
                        User.of(
                                userId,
                                password,
                                emptyUserName,
                                email,
                                phoneNumber,
                                birthDate,
                                Gender.MALE
                        );
                    });

            //then
            assertThat(result.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }


        @Test
        @DisplayName("유저 ID가 10자를 초과하면 BAD_REQUEST 예외가 발생한다.")
        public void failsToCreateUser_whenIdIsNotAlphanumericAndExceeds10Characters() throws Exception{
            //given
            String userIdExceeds10Characters = "12345678910";
            String password = "password";
            String userName = "userName";
            String email = "email@loopers.com";
            String phoneNumber = "010-1234-5678";
            String birthDate = "1994-04-20";

            //when
            CoreException result = assertThrows(
                    CoreException.class, () -> {
                        User.of(
                                userIdExceeds10Characters,
                                password,
                                userName,
                                email,
                                phoneNumber,
                                birthDate,
                                Gender.MALE
                        );
                    });

            //then
            assertThat(result.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
        }
    }
    
    @Test
    @DisplayName("이메일 형식이 맞지 않으면, BAD_REQUEST 예외가 발생한다.")
    public void failsToOfUser_whenEmailFormatIsInvalid() throws Exception{
        //given
        String userId = "userId";
        String password = "password";
        String userName = "userName";
        String invalidFormatEmail = "email";
        String phoneNumber = "010-1234-5678";
        String birthDate = "2025-04-20";

        //when
        CoreException result = assertThrows(
                CoreException.class, () -> {
                    User.of(
                            userId,
                            password,
                            userName,
                            invalidFormatEmail,
                            phoneNumber,
                            birthDate,
                            Gender.MALE
                    );
                });

        //then
        assertThat(result.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
    }
    
    @Test
    @DisplayName("생년월일이 yyyy-MM-dd 형식에 맞지 않는 경우, BAD_REQUEST 예외가 발생한다.")
    public void failsToOfUser_whenBirthDateFormatIsNot_yyyyMMdd() throws Exception{
        //given
        String userId = "userId";
        String password = "password";
        String userName = "userName";
        String email = "email";
        String phoneNumber = "010-1234-5678";
        String invalidFormatBirthDate = "940420";

        //when
        CoreException result = assertThrows(
                CoreException.class, () -> {
                    User.of(
                            userId,
                            password,
                            userName,
                            email,
                            phoneNumber,
                            invalidFormatBirthDate,
                            Gender.MALE
                    );
                });

        //then
        assertThat(result.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
    }

    @Test
    @DisplayName("성별이 비어있는 경우, BAD_REQUEST 예외가 발생한다.")
    public void failsToOfUser_whenGengerIsEmpty() throws Exception{
        //given
        String userId = "userId";
        String password = "password";
        String userName = "userName";
        String email = "email";
        String phoneNumber = "010-1234-5678";
        String birthDate = "1994-04-20";
        Gender invalidGender = null;


        //when
        CoreException result = assertThrows(
                CoreException.class, () -> {
                    User.of(
                            userId,
                            password,
                            userName,
                            email,
                            phoneNumber,
                            birthDate,
                            invalidGender
                    );
                });

        //then
        assertThat(result.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
    }

}
