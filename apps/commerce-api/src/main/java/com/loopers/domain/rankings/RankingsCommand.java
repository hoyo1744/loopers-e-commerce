package com.loopers.domain.rankings;

import lombok.*;

import java.time.LocalDate;

public class RankingsCommand {


    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class PageInfo {
        private LocalDate date;
        private Long page;
        private Long size;
        private RankType type;
        private RankPeriod period;

        public static PageInfo of(LocalDate date, Long page, Long size, RankType type, RankPeriod period) {
            return PageInfo.builder()
                    .date(date)
                    .page(page)
                    .size(size)
                    .type(type)
                    .period(period)
                    .build();
        }
    }
}
