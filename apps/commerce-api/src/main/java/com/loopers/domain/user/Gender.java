package com.loopers.domain.user;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Gender {
    MALE("M"),
    FEMALE("F");

    private final String value;

    public static Gender from(String input){
        for (Gender gender : values()) {
            if (gender.name().equalsIgnoreCase(input)) {
                return gender;
            }
            if (gender.value.equalsIgnoreCase(input)) {
                return gender;
            }
        }
        throw new CoreException(ErrorType.BAD_REQUEST, "성별은 남성/여성만 가능합니다.");
    }
}
