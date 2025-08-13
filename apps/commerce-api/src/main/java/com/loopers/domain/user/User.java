package com.loopers.domain.user;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@Getter
@Entity
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user")
public class User {

    @Id
    @Column(name = "user_id")
    private String id;

    private String password;

    private String name;

    private String email;

    private String phoneNumber;

    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    private Gender gender;


    @Builder
    private User(String id, String password, String name, String email, String phoneNumber, LocalDate birthDate, Gender gender) {
        this.id = id;
        this.password = password;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.birthDate = birthDate;
        this.gender = gender;
    }

    public static User create(String userId, String password, String userName, String email, String phoneNumber, String birthDate, Gender gender) {
        validateUserId(userId);
        validatePassword(password);
        validateUserName(userName);
        validateEmail(email);
        validatePhoneNumber(phoneNumber);
        validateGender(gender);
        LocalDate parsedBirthDate = validateBirthDate(birthDate);

        return User.builder()
                .id(userId)
                .password(password)
                .name(userName)
                .email(email)
                .phoneNumber(phoneNumber)
                .birthDate(parsedBirthDate)
                .gender(gender)
                .build();
    }

    public static void validateUserId(String userId) {
        requireNonBlank(userId, "회원 ID는 비어있을 수 없습니다.");

        if (!userId.matches("^[a-zA-Z0-9]{1,10}$")) {
            throw new CoreException(ErrorType.BAD_REQUEST, "회원 ID는 영문 및 숫자 10자 이내여야 합니다.");
        }
    }

    public static void validatePassword(String password) {
        requireNonBlank(password, "패스워드는 비어있을 수 없습니다.");
    }

    public static void validateUserName(String userName) {
        requireNonBlank(userName, "사용자 이름은 비어있을 수 없습니다.");
    }

    public static void validateEmail(String email) {
        if (!email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            throw new CoreException(ErrorType.BAD_REQUEST, "올바른 이메일 형식이 아닙니다.");
        }
    }

    public static void validatePhoneNumber(String phoneNumber) {
        if (!phoneNumber.matches("^01[016789]-\\d{3,4}-\\d{4}$")) {
            throw new CoreException(ErrorType.BAD_REQUEST, "휴대폰 번호는 01X-XXX(X)-XXXX 형식이어야 합니다.");
        }
    }

    public static LocalDate validateBirthDate(String birthDate) {
        try {
            return LocalDate.parse(birthDate);
        } catch (DateTimeParseException e) {
            throw new CoreException(ErrorType.BAD_REQUEST, "생년월일은 yyyy-MM-dd 형식이어야 합니다.");
        }
    }

    public static void validateGender(Gender gender) {
        if (gender == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "성별은 비어있을 수 없습니다.");
        }
    }

    private static void requireNonBlank(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new CoreException(ErrorType.BAD_REQUEST, message);
        }
    }
}
