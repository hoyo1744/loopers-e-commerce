package com.loopers.interfaces.api.point;

import lombok.Getter;

public class PointResponse {

    @Getter
    public static class Point {
        private Long amount;

        private Point(Long amount) {
            this.amount = amount;
        }

        public static Point of(Long amount) {
            return new Point(amount);
        }
    }

    @Getter
    public static class ChargedPoint {
        private Long amount;

        private ChargedPoint(Long amount) {
            this.amount = amount;
        }

        public static ChargedPoint of(Long amount) {
            return new ChargedPoint(amount);
        }
    }
}
