package com.loopers.application.point;

import lombok.Getter;

public class AppPointResult {

    @Getter
    public static class Point {
        Long point;

        private Point(Long point) {
            this.point = point;
        }

        public static AppPointResult.Point of(Long point) {
            return new AppPointResult.Point(point);
        }
    }

    @Getter
    public static class ChargedPoint {
        Long point;

        private ChargedPoint(Long point) {
            this.point = point;
        }

        public static AppPointResult.ChargedPoint of(Long amount) {
            return new AppPointResult.ChargedPoint(amount);
        }
    }
}
