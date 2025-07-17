package com.loopers.domain.user;

import lombok.Getter;

public class UserInfo {


    @Getter
    public static class User {
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

        public static User of(String id, String name, String email, String phoneNumber, String birthDate, String gender) {
            return new User(id, name, email, phoneNumber, birthDate, gender);
        }
    }
}
