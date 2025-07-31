package com.loopers.interfaces.api.product;

import com.loopers.application.product.ProductCriteria;
import com.loopers.application.product.ProductFacade;
import com.loopers.application.product.ProductResult;
import com.loopers.interfaces.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductV1Controller implements ProductV1ApiSpec {

    private final ProductFacade productFacade;

    @GetMapping("/{productId}")
    public ApiResponse<ProductResponse.ProductDetail> getProductDetail(@RequestHeader(value = "X-USER-ID", required = false) String userId, @PathVariable Long productId) {
        ProductResult.ProductDetail productDetail = productFacade.getProductDetail(ProductCriteria.ProductDetailRequest.of(userId, productId));
        return ApiResponse.success(
                ProductResponse.ProductDetail.of(
                        productDetail.getName(),
                        productDetail.getPrice(),
                        ProductResponse.Brand.of(productDetail.getBrand().getName()),
                        ProductResponse.Like.of(productDetail.getLike().getLiked(), productDetail.getLike().getCount()),
                        ProductResponse.Stock.of(productDetail.getStock().getQuantity())
                )
        );
    }


    @GetMapping
    public ApiResponse<ProductResponse.Products> getProducts(@RequestHeader(value = "X-USER-ID", required = false) String id,
                                                            @RequestParam(value = "brandId", required = true) Long brandId,
                                                             @RequestParam(value = "sort", required = true) String sort,
                                                             @RequestParam(value = "page", required = true) Long page,
                                                             @RequestParam(value = "size", required = true) Long size
                                                             ) {

        List<ProductResult.Product> products = productFacade.getProducts(ProductCriteria.ProductRequest.of(
                id,
                brandId,
                sort,
                page,
                size
        ));
        return ApiResponse.success(
                ProductResponse.Products.of(products.stream().map(product ->
                        ProductResponse.Product.of(
                                product.getName(),
                                product.getPrice(),
                                product.getCreatedAt(),
                                ProductResponse.Brand.of(product.getBrand().getName()),
                                ProductResponse.Like.of(product.getLike().getLiked(), product.getLike().getCount())
                        )
                ).toList())
        );
    }

}
