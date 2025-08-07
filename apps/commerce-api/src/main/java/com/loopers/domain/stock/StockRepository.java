package com.loopers.domain.stock;

public interface StockRepository {

    Stock findByProductId(Long productId);

    Stock save(Stock stock);

    Stock findByProductIdForUpdate(Long productId);
}
