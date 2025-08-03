package com.loopers.domain.point;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
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
        Point save = pointRepository.save(Point.create(userId, amount));
        return PointInfo.Point.of(save.getAmount(), save.getUserId());
    }

    public void deductPoint(PointCommand.Use command) {
        Point point = pointRepository.findByUserId(command.getUserId())
                .orElseThrow(() -> new CoreException(ErrorType.BAD_REQUEST, "사용자 포인트가 존재하지 않습니다. userId: " + command.getUserId()));

        point.deduct(command.getPoint());
    }
}
