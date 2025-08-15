package com.loopers.domain.product;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public List<ProductInfo.ProductQuery> getProducts(ProductCommand.Search command) {
        return productRepository.search(command);
    }

    public ProductInfo.ProductDetail getProductDetail(Long productId) {
        Product product = productRepository.findById(productId);
        return ProductInfo.ProductDetail.of(product.getName(), product.getPrice(), product.getBrandId());
    }

    public ProductInfo.OrderProducts getOrderProducts(ProductCommand.OrderProducts orderProducts) {
        List<ProductInfo.OrderProduct> orderProductList = orderProducts.getOrderProducts().stream()
                .map(op -> {
                    Product product = productRepository.findById(op.getProductId());
                    return ProductInfo.OrderProduct.of(product.getId(), product.getPrice(), op.getQuantity(), product.getName());
                }).toList();

        return ProductInfo.OrderProducts.of(orderProductList);
    }

    public void increaseLikeCount(ProductCommand.Product product) {
        productRepository.increaseLikeCount(product.getProductId());
    }

    public void decreaseLikeCount(ProductCommand.Product product) {
        productRepository.decreaseLikeCount(product.getProductId());
    }

}
