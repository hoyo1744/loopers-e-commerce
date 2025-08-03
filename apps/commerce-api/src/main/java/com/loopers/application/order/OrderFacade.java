package com.loopers.application.order;

import com.loopers.domain.brand.BrandCommand;
import com.loopers.domain.brand.BrandService;
import com.loopers.domain.order.OrderCommand;
import com.loopers.domain.order.OrderInfo;
import com.loopers.domain.order.OrderService;
import com.loopers.domain.order.OrderStatus;
import com.loopers.domain.payment.PaymentCommand;
import com.loopers.domain.payment.PaymentService;
import com.loopers.domain.point.PointCommand;
import com.loopers.domain.point.PointService;
import com.loopers.domain.product.ProductCommand;
import com.loopers.domain.product.ProductInfo;
import com.loopers.domain.product.ProductService;
import com.loopers.domain.stock.StockCommand;
import com.loopers.domain.stock.StockService;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderFacade {

    private final OrderService orderService;

    private final StockService stockService;

    private final ProductService productService;

    private final PointService pointService;

    private final PaymentService paymentService;

    private final BrandService brandService;


    @Transactional
    public void order(OrderCriteria.Order order) {
        if (order == null || order.getOrderProducts() == null || order.getOrderProducts().isEmpty()) {
            throw new CoreException(ErrorType.BAD_REQUEST, "주문할 상품이 없습니다.");
        }

        if (order.getUserId() == null || order.getUserId().isEmpty()) {
            throw new CoreException(ErrorType.UNAUTHORIZED, "로그인 후 이용 가능합니다.");
        }

        // 재고 확인
        order.orderProducts.stream()
                .forEach(orderProduct -> {
                    if (orderProduct.getProductId() == null || orderProduct.getQuantity() == null) {
                        throw new CoreException(ErrorType.BAD_REQUEST, "상품 ID와 수량은 필수입니다.");
                    }
                    if (!stockService.isStockAvailable(orderProduct.getProductId(), orderProduct.getQuantity())) {
                        throw new CoreException(ErrorType.BAD_REQUEST, "재고가 부족합니다. 상품 ID: " + orderProduct.getProductId());
                    }
                });

        // 상품 정보 확인
        List<ProductCommand.OrderProduct> list = order.orderProducts.stream().map(
                        orderProduct -> ProductCommand.OrderProduct.of(orderProduct.getProductId(), orderProduct.getQuantity()))
                .toList();

        ProductInfo.OrderProducts orderProducts = productService.getOrderProducts(ProductCommand.OrderProducts.of(list));


        // Order 및 OrderProduct 생성
        OrderInfo.Order orderInfo = orderService.createOrder(OrderCommand.Order.of(order.getUserId(),
                OrderCommand.OrderProducts.of(
                        orderProducts.getOrderProducts().stream()
                                .map(op -> OrderCommand.OrderProduct.of(op.getProductId(), op.getQuantity(), op.getPrice()))
                                .collect(Collectors.toList()))
        ));


        // -> 주문 완료 후
        // 외부 결제 요청
        paymentService.pay(PaymentCommand.Pay.of(orderInfo.getTotalPrice(), orderInfo.getOrderId()));

        // 재고 감소
        stockService.decreaseStock(StockCommand.OrderProducts.of(
                order.orderProducts.stream()
                        .map(op -> StockCommand.OrderProduct.of(op.getProductId(), op.getQuantity()))
                        .collect(Collectors.toList())
        ));

        // 포인트 감소
        pointService.deductPoint(PointCommand.Use.of(order.getUserId(), orderInfo.getTotalPrice()));

        // 주문 상태 업데이트
        orderService.updateOrderStatus(OrderCommand.OrderStatus.of(orderInfo.getOrderId(), OrderStatus.COMPLETE.getValue()));
    }

    public OrderResult.Orders getOrders(String userId) {

        if (userId == null || userId.isEmpty()) {
            throw new CoreException(ErrorType.UNAUTHORIZED, "로그인 후 이용 가능합니다.");
        }

        OrderInfo.Orders orders = orderService.getOrders(userId);

        return OrderResult.Orders.of(
                orders.getOrders().stream()
                        .map(order -> OrderResult.Order.of(
                                order.getOrderId(),
                                order.getOrderStatus(),
                                order.getTotalPrice(),
                                order.getOrderProducts().getOrderProducts().stream()
                                        .map(op -> {
                                            ProductInfo.ProductDetail productDetail = productService.getProductDetail(op.getProductId());
                                            return OrderResult.Product.of(
                                                    productDetail.getName(),
                                                op.getPrice(),
                                                brandService.getBrand(BrandCommand.Search.of(productDetail.getBrandId())).getName());
                                                }
                                        ).toList()
                        )).toList()
        );
    }

    public OrderResult.Order getOrder(OrderCriteria.OrderDetail command) {
        if (command.getUserId() == null || command.getUserId().isEmpty()) {
            throw new CoreException(ErrorType.UNAUTHORIZED, "로그인 후 이용 가능합니다.");
        }

        OrderInfo.OrderDetail orderDetail = orderService.getOrderDetail(OrderCommand.OrderDetail.of(command.getUserId(), command.getOrderId()));

        return OrderResult.Order.of(
                orderDetail.getOrderId(),
                orderDetail.getOrderStatus(),
                orderDetail.getTotalPrice(),
                orderDetail.getOrderProducts().getOrderProducts().stream()
                        .map(op -> {
                            ProductInfo.ProductDetail productDetail = productService.getProductDetail(op.getProductId());
                            return OrderResult.Product.of(
                                    productDetail.getName(),
                                            op.getPrice(),
                                            brandService.getBrand(BrandCommand.Search.of(productDetail.getBrandId())).getName()
                                    );
                                }
                        )
                        .toList()
        );
    }

}
