package com.loopers.domain.point;

import lombok.Builder;
import lombok.Getter;

public class PointCommand {

    @Getter
    @Builder
    public static class Use {
        private String userId;
        private Long point;

        private Use(String userId, Long point) {
            this.userId = userId;
            this.point = point;
        }

        public static Use of(String userId, Long point) {
            return Use.builder()
                    .userId(userId)
                    .point(point)
                    .build();
        }
    }
}
