package com.loopers.application.point;

import com.loopers.domain.point.PointInfo;
import com.loopers.domain.point.PointService;
import com.loopers.domain.user.UserInfo;
import com.loopers.domain.user.UserService;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PointFacade {

    private final PointService pointService;

    private final UserService userService;

    public AppPointResult.Point getPoint(String userId) {
        if (userId == null || userId.isEmpty()) {
            throw new CoreException(ErrorType.NOT_FOUND, "회원 ID는 필수입니다.");
        }

        UserInfo.User user = userService.getUser(userId);
        if (user == null) {
            throw new CoreException(ErrorType.NOT_FOUND, "존재하지 않는 회원입니다.");
        }

        PointInfo.Point findPoint = pointService.getPoint(userId);

        return AppPointResult.Point.of(findPoint.getAmount());
    }

    public AppPointResult.ChargedPoint charge(String userId, Long amount) {
        if (userId == null) {
            throw new CoreException(ErrorType.NOT_FOUND, "회원 ID는 필수입니다.");
        }

        UserInfo.User user = userService.getUser(userId);
        if (user == null) {
            throw new CoreException(ErrorType.NOT_FOUND, "존재하지 않는 회원입니다.");
        }

        PointInfo.Point point = pointService.chargePoint(userId, amount);
        return AppPointResult.ChargedPoint.of(point.getAmount());
    }

}
