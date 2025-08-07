package com.loopers.application.user;

import com.loopers.domain.user.Gender;
import com.loopers.domain.user.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserCommand {

    @Getter
    public static class SignUp {
        private String id;
        private String password;
        private String name;
        private String email;
        private String phoneNumber;
        private String birthDate;
        private String gender;


        private SignUp(String id, String password, String name, String email, String phoneNumber, String birthDate, String gender) {
            this.id = id;
            this.password = password;
            this.name = name;
            this.email = email;
            this.phoneNumber = phoneNumber;
            this.birthDate = birthDate;
            this.gender = gender;
        }

        public static SignUp of(String id, String password, String name, String email, String phoneNumber, String birthDate, String gender) {
            return new SignUp(id, password, name, email, phoneNumber, birthDate, gender);
        }

        public User toDomainUser() {
            return User.create(
                    id,
                    password,
                    name,
                    email,
                    phoneNumber,
                    birthDate,
                    Gender.from(gender)
            );
        }
    }

}
