package com.loopers.domain.stock;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;

@Service
@RequiredArgsConstructor
public class StockService {

    private final StockRepository stockRepository;


    @Transactional
    public void validateStock(StockCommand.OrderProducts orderProducts) {
            orderProducts.getOrderProducts().forEach(orderProduct -> {
                Stock stock = stockRepository.findByProductIdForUpdate(orderProduct.getProductId());
                stock.hasEnough(orderProduct.getQuantity());
            });
    }

    public StockInfo.Stock getStock(Long produdctId) {
        Stock stock = stockRepository.findByProductId(produdctId);
        return StockInfo.Stock.of(stock.getProductId(), stock.getQuantity());
    }

    @Transactional
    public void increaseStock(StockCommand.OrderProducts orderProducts) {
        orderProducts.getOrderProducts().stream()
                .sorted(Comparator.comparing(StockCommand.OrderProduct::getProductId))
                .forEach(orderProduct -> {
                    Stock stock = stockRepository.findByProductIdForUpdate(orderProduct.getProductId());
                    stock.incrementQuantity(orderProduct.getQuantity());
                });
    }

    @Transactional
    public void decreaseStock(StockCommand.OrderProducts orderProducts) {
        orderProducts.getOrderProducts().stream()
                .sorted(Comparator.comparing(StockCommand.OrderProduct::getProductId))
                .forEach(orderProduct -> {
                    Stock stock = stockRepository.findByProductIdForUpdate(orderProduct.getProductId());
                    stock.hasEnough(orderProduct.getQuantity());
                    stock.decrementQuantity(orderProduct.getQuantity());
                    stockRepository.save(stock);
                });
    }

}
