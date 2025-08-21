package com.loopers.support.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorType {
    /** 범용 에러 */
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), "일시적인 오류가 발생했습니다."),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.getReasonPhrase(), "잘못된 요청입니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, HttpStatus.NOT_FOUND.getReasonPhrase(), "존재하지 않는 요청입니다."),
    CONFLICT(HttpStatus.CONFLICT, HttpStatus.CONFLICT.getReasonPhrase(), "이미 존재하는 리소스입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, HttpStatus.UNAUTHORIZED.getReasonPhrase(), "인증되지 않은 사용자입니다."),

    /** PG 에러 */
    PG_INTERNAL_ERROR(HttpStatus.SERVICE_UNAVAILABLE, "PG-2001", "PG 서버 내부 오류입니다."),


    /** Circuit 에러 */
    CC_INTERNAL_ERROR(HttpStatus.SERVICE_UNAVAILABLE, "CC-4001", "서비스가 일시적으로 불안정합니다."),
        ;


    private final HttpStatus status;
    private final String code;
    private final String message;
}
