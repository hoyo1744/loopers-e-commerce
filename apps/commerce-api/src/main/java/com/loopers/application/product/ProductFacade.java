package com.loopers.application.product;

import com.loopers.domain.brand.BrandCommand;
import com.loopers.domain.brand.BrandInfo;
import com.loopers.domain.brand.BrandService;
import com.loopers.domain.like.LikeCommand;
import com.loopers.domain.like.LikeService;
import com.loopers.domain.product.ProductCommand;
import com.loopers.domain.product.ProductInfo;
import com.loopers.domain.product.ProductService;
import com.loopers.domain.stock.StockInfo;
import com.loopers.domain.stock.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductFacade {

    private final ProductService productService;

    private final BrandService brandService;

    private final StockService stockService;

    private final LikeService likeService;

    public List<ProductResult.Product> getProducts(ProductCriteria.ProductRequest request) {
        List<ProductInfo.ProductQuery> products = productService.getProducts(ProductCommand.Search.of(
                request.getUserId(),
                request.getBrandId(),
                request.getSort(),
                request.getPage(),
                request.getSize()
        ));

        return products.stream()
                .map(product -> {
                    Boolean liked = likeService.isLiked(LikeCommand.Check.of(request.getUserId(), product.getProductId()));
                    return ProductResult.Product.of(
                            product.getProductName(),
                            product.getPrice(),
                            product.getCreatedAt(),
                            ProductResult.Brand.of(product.getBrandName()),
                            ProductResult.Like.of(liked, product.getLikes())
                    );
                }).toList();

    }

    public ProductResult.ProductDetail getProductDetail(ProductCriteria.ProductDetailRequest request) {
        ProductInfo.ProductDetail findProductDetail
                = productService.getProductDetail(request.getProductId());

        BrandInfo.Brand findBrand
                = brandService.getBrand(BrandCommand.Search.of(findProductDetail.getBrandId()));

        StockInfo.Stock stock = stockService.getStock(request.getProductId());

        Long productLikeCount = likeService.countLikes(request.getProductId());

        Boolean liked = likeService.isLiked(LikeCommand.Check.of(request.getUserId(), request.getProductId()));

        return ProductResult.ProductDetail.of(
                findProductDetail.getName(),
                findProductDetail.getPrice(),
                ProductResult.Brand.of(findBrand.getName()),
                ProductResult.Like.of(liked, productLikeCount),
                ProductResult.Stock.of(stock.getQuantity())
        );
    }

}
