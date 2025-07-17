package com.loopers.application.user;

import com.loopers.domain.user.UserInfo;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class AppUserResult {

    @Getter
    public static class User {
        private static UserInfo.User user;
        private String id;
        private String name;
        private String email;
        private String phoneNumber;
        private String birthDate;
        private String gender;

        private User(String id, String name, String email, String phoneNumber, String birthDate, String gender) {
            this.id = id;
            this.name = name;
            this.email = email;
            this.phoneNumber = phoneNumber;
            this.birthDate = birthDate;
            this.gender = gender;
        }

        public static AppUserResult.User of(String id, String name, String email, String phoneNumber, String birthDate, String gender) {
            return new AppUserResult.User(id, name, email, phoneNumber, birthDate, gender);
        }

        public static AppUserResult.User from(UserInfo.User user) {
            User.user = user;
            return AppUserResult.User.of(
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
