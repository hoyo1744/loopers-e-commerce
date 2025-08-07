package com.loopers.application.like;

import com.loopers.domain.brand.BrandCommand;
import com.loopers.domain.brand.BrandInfo;
import com.loopers.domain.brand.BrandService;
import com.loopers.domain.like.LikeCommand;
import com.loopers.domain.like.LikeInfo;
import com.loopers.domain.like.LikeService;
import com.loopers.domain.product.ProductInfo;
import com.loopers.domain.product.ProductService;
import com.loopers.domain.stock.StockInfo;
import com.loopers.domain.stock.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.UnexpectedRollbackException;

import java.util.List;

@RequiredArgsConstructor
@Component
public class LikeFacade {

    private final LikeService likeService;

    private final ProductService productService;

    private final StockService stockService;

    private final BrandService brandService;

    public Boolean likeProduct(LikeCriteria.Like like) {
        try {
            return likeService.likeProduct(LikeCommand.Like.of(like.getUserId(), like.getProductId()));
        } catch (UnexpectedRollbackException ex) {
            return false;
        }
    }

    public Boolean unlikeProduct(LikeCriteria.Unlike unlike) {
        try {
            return likeService.unLikeProduct(LikeCommand.Unlike.of(unlike.getUserId(), unlike.getProductId()));
        } catch (UnexpectedRollbackException ex) {
            return false;
        }
    }

    public List<LikeResult.Product> getLikedProducts(LikeCriteria.User user) {
        List<LikeInfo.LikeProduct> likeProduct = likeService.getLikeProduct(user.getUserId());
        return likeProduct.stream()
                .map(like -> {
                    ProductInfo.ProductDetail productDetail = productService.getProductDetail(like.getProductId());

                    StockInfo.Stock stock = stockService.getStock(like.getProductId());

                    BrandInfo.Brand brand = brandService.getBrand(BrandCommand.Search.of(productDetail.getBrandId()));

                    Long likeCount = likeService.countLikes(like.getProductId());

                    return LikeResult.Product.of(
                            productDetail.getName(),
                            productDetail.getPrice(),
                            LikeResult.Brand.of(brand.getName()),
                            LikeResult.Like.of(true, likeCount),
                            LikeResult.Stock.of(stock.getQuantity())
                    );
                }).toList();
    }
}
