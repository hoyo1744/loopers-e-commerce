package com.loopers.domain.point;

import lombok.Getter;

public class PointInfo {

    @Getter
    public static class Point {
        private Long amount;
        private String userId;

        private Point(Long amount, String userId) {
            this.amount = amount;
            this.userId = userId;
        }

        public static Point of(Long amount, String userId) {
            return new Point(amount, userId);
        }
    }
}
