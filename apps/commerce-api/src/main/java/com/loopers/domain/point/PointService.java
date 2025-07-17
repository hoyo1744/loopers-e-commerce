package com.loopers.domain.point;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PointService {
    private final PointRepository pointRepository;

    public PointInfo.Point getPoint(String userId) {
        Point point = pointRepository.findByUserId(userId).orElse(null);
        if (point == null) {
            return null;
        }
        return PointInfo.Point.of(point.getAmount(), point.getUserId());
    }

    public PointInfo.Point chargePoint(String userId, Long amount) {
        Point save = pointRepository.save(Point.of(userId, amount));
        return PointInfo.Point.of(save.getAmount(), save.getUserId());
    }
}
