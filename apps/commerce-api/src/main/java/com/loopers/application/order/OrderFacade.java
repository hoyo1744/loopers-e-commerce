package com.loopers.application.order;

import com.loopers.domain.brand.BrandCommand;
import com.loopers.domain.brand.BrandService;
import com.loopers.domain.coupon.Coupon;
import com.loopers.domain.coupon.CouponService;
import com.loopers.domain.order.Order;
import com.loopers.domain.order.OrderCommand;
import com.loopers.domain.order.OrderInfo;
import com.loopers.domain.order.OrderService;
import com.loopers.domain.ordercalculator.OrderCalculator;
import com.loopers.domain.payment.PaymentEventCommand;
import com.loopers.domain.payment.PaymentEventService;
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

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OrderFacade {

    private final OrderService orderService;

    private final StockService stockService;

    private final ProductService productService;

    private final BrandService brandService;

    private final UserCouponService userCouponService;

    private final CouponService couponService;

    private final OrderCalculator orderCalculator;

    private final UserService userService;

    private final PaymentEventService paymentEventService;
    

    @Transactional
    public void order(OrderCriteria.Order request) {

        if (request == null || request.getOrderProducts() == null || request.getOrderProducts().isEmpty()) {
            throw new CoreException(ErrorType.BAD_REQUEST, "주문할 상품이 없습니다.");
        }

        userService.getUser(request.getUserId());

        stockService.validateStock(request.toStockCommand());

        ProductInfo.OrderProducts orderProducts = productService.getProducts(request.toProductCommand());

        Order order = orderService.createOrder(OrderCommand.Order.of(request.getUserId(),
                OrderCommand.OrderProducts.of(
                        orderProducts.getOrderProducts().stream()
                                .map(op -> OrderCommand.OrderProduct.of(op.getProductId(), op.getQuantity(), op.getPrice()))
                                .collect(Collectors.toList()))
        ));

        Long userCouponId = null;
        Long couponId = request.getCouponId();
        if(couponId != null) {
            UserCoupon uc = userCouponService.getAvailableUserCoupon(request.toUserCouponCommand());
            userCouponId = uc.getId();
            Coupon c = couponService.getCoupon(uc.getCouponId());
            orderCalculator.applyDiscount(order, c, uc);
        }

        // 결제 요청 이벤트 발행
        paymentEventService.publishRequested(
                PaymentEventCommand.Requested.of(request.getUserId(), couponId,
                PaymentEventCommand.Payment.of(order.getOrderNumber(), request.getCardType(), request.getCardNo(), order.calculateFinalPrice(), request.getPaymentType())));

        // 결제 완료 이벤트 발행
        paymentEventService.publishCompleted(PaymentEventCommand.Completed.of(userCouponId, order.getOrderNumber()));
    }

    @Transactional
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
                                order.getOrderProducts().getOrderProductDtos().stream()
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
                orderDetail.getOrderProducts().getOrderProductDtos().stream()
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
