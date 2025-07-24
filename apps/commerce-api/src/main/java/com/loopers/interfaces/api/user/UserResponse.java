package com.loopers.interfaces.api.user;

import com.loopers.application.user.AppUserResult;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserResponse {

    @Getter
    public static class User {
        private final String id;

        private final String name;

        private final String email;

        private final String phoneNumber;

        private final String birthDate;

        private final String gender;

        private User(String id, String name, String email,
                     String phoneNumber, String birthDate, String gender) {
            this.id = id;
            this.name = name;
            this.email = email;
            this.phoneNumber = phoneNumber;
            this.birthDate = birthDate;
            this.gender = gender;
        }

        public static UserResponse.User of(String userId, String userName, String email, String phoneNumber, String birthDate, String gender) {
            return new UserResponse.User(userId, userName, email, phoneNumber, birthDate, gender);
        }


        public static UserResponse.User from(AppUserResult.User user) {
            return UserResponse.User.of(
                    user.getId(),
                    user.getName(),
                    user.getEmail(),
                    user.getPhoneNumber(),
                    user.getBirthDate(),
                    user.getGender()
            );
        }


    }

}
