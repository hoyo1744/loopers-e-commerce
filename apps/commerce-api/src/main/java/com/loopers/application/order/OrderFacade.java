package com.loopers.application.order;

import com.loopers.domain.brand.BrandCommand;
import com.loopers.domain.brand.BrandService;
import com.loopers.domain.coupon.Coupon;
import com.loopers.domain.coupon.CouponService;
import com.loopers.domain.order.*;
import com.loopers.domain.ordercalculator.OrderCalculator;
import com.loopers.domain.payment.PaymentCommand;
import com.loopers.domain.payment.PaymentService;
import com.loopers.domain.point.PointCommand;
import com.loopers.domain.point.PointService;
import com.loopers.domain.product.ProductInfo;
import com.loopers.domain.product.ProductService;
import com.loopers.domain.stock.StockService;
import com.loopers.domain.user.UserService;
import com.loopers.domain.usercoupon.UserCoupon;
import com.loopers.domain.usercoupon.UserCouponService;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OrderFacade {

    private final OrderService orderService;

    private final StockService stockService;

    private final ProductService productService;

    private final PointService pointService;

    private final PaymentService paymentService;

    private final BrandService brandService;

    private final UserCouponService userCouponService;

    private final CouponService couponService;

    private final OrderCalculator orderCalculator;

    private final UserService userService;


    @Transactional
    public void order(OrderCriteria.Order request) {

        if (request == null || request.getOrderProducts() == null || request.getOrderProducts().isEmpty()) {
            throw new CoreException(ErrorType.BAD_REQUEST, "주문할 상품이 없습니다.");
        }

        userService.getUser(request.getUserId());

        stockService.validateStock(request.toStockCommand());

        ProductInfo.OrderProducts orderProducts = productService.getOrderProducts(request.toProductCommand());

        Optional<Long> optionalCouponId = Optional.ofNullable(request.getCouponId());
        Optional<UserCoupon> userCoupon = optionalCouponId.map(id -> userCouponService.getAvailableUserCoupon(request.toUserCouponCommand()));

        Optional<Coupon> coupon = userCoupon.map(uc -> couponService.getCoupon(uc.getCouponId()));

        Order order = orderService.createOrder(OrderCommand.Order.of(request.getUserId(),
                OrderCommand.OrderProducts.of(
                        orderProducts.getOrderProducts().stream()
                                .map(op -> OrderCommand.OrderProduct.of(op.getProductId(), op.getQuantity(), op.getPrice()))
                                .collect(Collectors.toList()))
        ));

        userCoupon.ifPresent(uc -> {
            coupon.ifPresent(c -> {
                orderCalculator.applyDiscount(order, c, uc);
                userCouponService.useCoupon(uc.getId());
            });
        });

        paymentService.pay(PaymentCommand.Pay.of(order.calculateFinalPrice(), order.getId()));

        stockService.decreaseStock(request.toStockCommand());

        pointService.deductPoint(PointCommand.Use.of(request.getUserId(), order.calculateFinalPrice()));

        orderService.pay(OrderCommand.OrderStatus.of(order.getId(), OrderStatus.COMPLETE.getValue()));
    }

    @Transactional(readOnly = true)
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
