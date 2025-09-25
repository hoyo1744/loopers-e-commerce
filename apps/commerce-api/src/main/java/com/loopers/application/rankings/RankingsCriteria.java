package com.loopers.application.rankings;

import com.loopers.domain.rankings.RankPeriod;
import com.loopers.domain.rankings.RankType;
import com.loopers.domain.rankings.RankingsCommand;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

public class RankingsCriteria {

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class PageInfo {
        private String userId;
        private LocalDate date;
        private Long page;
        private Long size;
        private String type;
        private String period;

        public static PageInfo of(String userId, LocalDate date, Long page, Long size, String type, String period) {
            return PageInfo.builder()
                    .userId(userId)
                    .date(date)
                    .page(page)
                    .size(size)
                    .type(type)
                    .period(period)
                    .build();
        }

        public RankingsCommand.PageInfo toPageInfo() {
            return RankingsCommand.PageInfo.of(date, page, size, RankType.from(type), RankPeriod.from(period));
        }
    }
}
