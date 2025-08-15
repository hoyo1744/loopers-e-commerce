package com.loopers.interfaces.api.like;

import com.loopers.application.like.LikeCriteria;
import com.loopers.application.like.LikeFacade;
import com.loopers.application.like.LikeResult;
import com.loopers.interfaces.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/like")
public class LikeV1Controller implements LikeV1ApiSpec{

    private final LikeFacade likeFacade;

    @PostMapping("/products/{productId}")
    public ApiResponse<String> likeProduct(
            @RequestHeader(value = "X-USER-ID", required = true) String userId,
            @PathVariable Long productId) {
        likeFacade.likeProduct(LikeCriteria.Like.of(userId, productId));
        return ApiResponse.success("Product liked successfully.");
    }

    @DeleteMapping("/products/{productId}")
    public ApiResponse<String> unlikeProduct(
            @RequestHeader(value = "X-USER-ID", required = true) String userId,
            @PathVariable Long productId) {
        likeFacade.unlikeProduct(LikeCriteria.Unlike.of(userId, productId));
        return ApiResponse.success("Product unliked successfully.");
    }

    @GetMapping("/products")
    public ApiResponse<LikeResponse.Products> getLikedProducts(@RequestHeader(value = "X-USER-ID", required = true) String userId) {

        List<LikeResult.Product> likedProducts = likeFacade.getLikedProducts(LikeCriteria.User.of(userId));

        return ApiResponse.success(LikeResponse.Products.of(likedProducts.stream().map(lp ->
                LikeResponse.Product.of(
                        lp.getName(),
                        lp.getPrice(),
                        LikeResponse.Brand.of(lp.getBrand().getName()),
                        LikeResponse.Like.of(lp.getLike().getLiked(), lp.getLike().getCount()),
                        LikeResponse.Stock.of(lp.getStock().getQuantity())
                )
        ).toList()));
    }
}
