package com.loopers.interfaces.api.point;

import com.loopers.application.point.PointResult;
import com.loopers.application.point.PointFacade;
import com.loopers.interfaces.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/points")
public class PointV1Controller implements PointV1ApiSpec {

    private final PointFacade pointFacade;


    @Override
    @GetMapping
    public ApiResponse<PointResponse.Point> getPoints(@RequestHeader(value = "X-USER-ID") String userId) {
        PointResult.Point result = pointFacade.getPoint(userId);
        return ApiResponse.success(PointResponse.Point.of(result.getPoint()));
    }

    @Override
    @PostMapping("/charge")
    public ApiResponse<PointResponse.ChargedPoint> charge(@RequestHeader("X-USER-ID") String userId, @RequestBody Long amount) {
        PointResult.ChargedPoint charge = pointFacade.charge(userId, amount);
        return ApiResponse.success(PointResponse.ChargedPoint.of(charge.getPoint()));
    }
}
