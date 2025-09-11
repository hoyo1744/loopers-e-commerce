package com.loopers.domain.product;

import com.loopers.common.kafka.event.EventMessage;
import com.loopers.common.kafka.event.EventType;
import com.loopers.common.kafka.event.pageview.PageViewOutEvent;
import com.loopers.common.kafka.topic.Topics;
import com.loopers.domain.sender.MessageSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    private final MessageSender messageSender;

    public List<ProductInfo.ProductQuery> getProducts(ProductCommand.Search command) {
        return productRepository.search(command);
    }

    public ProductInfo.ProductDetail getProductDetail(Long productId) {
        Product product = productRepository.findById(productId);
        return ProductInfo.ProductDetail.of(product.getName(), product.getPrice(), product.getBrandId());
    }

    public ProductInfo.ProductDetail getProduct(Long productId) {
        messageSender.send(Topics.PAGE_VIEW, productId.toString(),
                EventMessage.<PageViewOutEvent.Viewed>builder()
                        .eventType(EventType.PAGE_VIEWED)
                        .version("v1")
                        .payload(PageViewOutEvent.Viewed.of(
                                productId,
                                LocalDateTime.now()))
                        .build()
        );

        Product product = productRepository.findById(productId);
        return ProductInfo.ProductDetail.of(product.getName(), product.getPrice(), product.getBrandId());
    }

    public ProductInfo.OrderProducts getProducts(ProductCommand.OrderProducts orderProducts) {
        List<ProductInfo.OrderProduct> orderProductList = orderProducts.getOrderProducts().stream()
                .map(op -> {
                    Product product = productRepository.findById(op.getProductId());
                    return ProductInfo.OrderProduct.of(product.getId(), product.getPrice(), op.getQuantity(), product.getName());
                }).toList();

        return ProductInfo.OrderProducts.of(orderProductList);
    }

    @Transactional
    public void increaseLikeCount(ProductCommand.Product product) {
        productRepository.increaseLikeCount(product.getProductId());
    }

    @Transactional
    public void decreaseLikeCount(ProductCommand.Product product) {
        productRepository.decreaseLikeCount(product.getProductId());
    }

}
