package com.loopers.domain.pg;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PgCommonResponse<T> {
    private Meta meta;
    private T data;


    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Meta {
        private String result;
        private String errorCode;
        private String message;

    }
}
