package com.loopers.application.point;

import com.loopers.domain.point.PointInfo;
import com.loopers.domain.point.PointService;
import com.loopers.domain.user.UserInfo;
import com.loopers.domain.user.UserService;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
@Transactional(readOnly = true)
public class PointFacade {

    private final PointService pointService;

    private final UserService userService;

    public PointResult.Point getPoint(String userId) {
        if (userId == null || userId.isEmpty()) {
            throw new CoreException(ErrorType.NOT_FOUND, "회원 ID는 필수입니다.");
        }

        UserInfo.User user = userService.getUser(userId);
        if (user == null) {
            throw new CoreException(ErrorType.NOT_FOUND, "존재하지 않는 회원입니다.");
        }

        PointInfo.Point findPoint = pointService.getPoint(userId);

        return PointResult.Point.of(findPoint.getAmount());
    }

    @Transactional
    public PointResult.ChargedPoint charge(String userId, Long amount) {
        if (userId == null) {
            throw new CoreException(ErrorType.NOT_FOUND, "회원 ID는 필수입니다.");
        }

        userService.getUser(userId);

        PointInfo.Point point = pointService.chargePoint(userId, amount);
        return PointResult.ChargedPoint.of(point.getAmount());
    }

}
