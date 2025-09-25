package com.loopers.domain.rankings;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RankPeriod {

    DAILY("DAILY"),
    WEEKLY("WEEKLY"),
    MONTHLY("MONTHLY")
    ;

    public static RankPeriod from(String input) {
        for (RankPeriod period : RankPeriod.values()) {
            if (period.period.equalsIgnoreCase(input)) {
                return period;
            }
        }
        return DAILY;
    }


    private final String period;

}
