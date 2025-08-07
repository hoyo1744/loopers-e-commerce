package com.loopers.infrastructure.point;

import com.loopers.domain.point.Point;
import com.loopers.domain.point.PointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PointRepositoryImpl implements PointRepository {

    private final PointJpaRepository pointJpaRepository;

    @Override
    public Optional<Point> findByUserId(String userId) {
        return pointJpaRepository.findByUserId(userId);
    }

    @Override
    public Optional<Point> findByUserIdForUpdate(String userId) {
        return pointJpaRepository.findByUserIdForUpdate(userId);
    }

    @Override
    public Point saveAndFlush(Point point) {
        return pointJpaRepository.saveAndFlush(point);
    }

    @Override
    public Point save(Point point) {
        return pointJpaRepository.save(point);
    }
}
