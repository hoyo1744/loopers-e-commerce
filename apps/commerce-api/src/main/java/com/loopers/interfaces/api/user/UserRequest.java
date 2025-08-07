package com.loopers.interfaces.api.user;

import com.loopers.application.user.UserCommand;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class UserRequest {

    @Getter
    @AllArgsConstructor
    public static class SignUp {
        @NotBlank(message = "ID는 비어있을 수 없습니다.")
        private String id;

        @NotBlank(message = "PASSWORD는 비어있을 수 없습니다.")
        private String password;

        @NotBlank(message = "NAME은 비어있을 수 없습니다.")
        private String name;

        @Email
        private String email;

        @Pattern(regexp = "^01[016789]-\\d{3,4}-\\d{4}$", message = "전화번호는 010-XXXX-XXXX 형식이어야 하며, 011~019 번호도 허용됩니다.")
        @NotEmpty(message = "연락처는 비어있을 수 없습니다.")
        private String phoneNumber;

        @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}")
        private String birthDate;

        @Pattern(regexp = "M|F")
        @NotEmpty(message = "성별은 비어있을 수 없습니다.")
        private String gender;

        public static SignUp of(String userId, String password, String userName, String email, String phoneNumber, String birthDate, String gender) {
            return new SignUp(userId, password, userName, email, phoneNumber, birthDate, gender);
        }

        public UserCommand.SignUp toAppUserCommand() {
            return UserCommand.SignUp.of(
                    id, password, name, email, phoneNumber, birthDate, gender
            );
        }
    }
}

