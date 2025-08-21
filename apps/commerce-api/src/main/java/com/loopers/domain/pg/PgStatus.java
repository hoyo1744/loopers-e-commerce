package com.loopers.domain.pg;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PgStatus {

    FAILED("FAILED"),
    PENDING("PENDING"),
    SUCCESS("SUCCESS"),
    ;

    private final String status;
}
