package com.loopers.domain.point;

import java.util.Optional;

public interface PointRepository {
    Optional<Point> findByUserId(String userId);

    Optional<Point> findByUserIdForUpdate(String userId);

    Point saveAndFlush(Point point);

    Point save(Point point);
}
