package com.loopers.domain.rankings;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RankType {

    LIKE_RANK("like"),
    SALES_RANK("sales"),
    PV_RANK("pv"),
    ALL_RANK("all")
    ;

    public static RankType from(String input) {
        for (RankType type : RankType.values()) {
            if (type.type.equalsIgnoreCase(input)) {
                return type;
            }
        }
        return ALL_RANK;
    }

    private final String type;
}
