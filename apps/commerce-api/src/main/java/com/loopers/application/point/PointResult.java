package com.loopers.application.point;

import lombok.Getter;

public class PointResult {

    @Getter
    public static class Point {
        Long point;

        private Point(Long point) {
            this.point = point;
        }

        public static PointResult.Point of(Long point) {
            return new PointResult.Point(point);
        }
    }

    @Getter
    public static class ChargedPoint {
        Long point;

        private ChargedPoint(Long point) {
            this.point = point;
        }

        public static PointResult.ChargedPoint of(Long amount) {
            return new PointResult.ChargedPoint(amount);
        }
    }
}
