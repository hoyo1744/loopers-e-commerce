package com.loopers.domain.point;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

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

    @Transactional
    public PointInfo.Point chargePoint(String userId, Long amount) {
        Point point = pointRepository.findByUserIdForUpdate(userId).orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "사용자 포인트가 존재하지 않습니다. userId: " + userId));
        point.charge(amount);
        return PointInfo.Point.of(point.getAmount(), userId);
    }

    @Transactional
    public void deductPoint(PointCommand.Use command) {
        Point point = pointRepository.findByUserIdForUpdate(command.getUserId())
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "사용자 포인트가 존재하지 않습니다. userId: " + command.getUserId()));
        point.deduct(command.getPoint());
    }
}
