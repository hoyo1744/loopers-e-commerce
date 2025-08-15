package com.loopers.infrastructure.product;

import com.loopers.domain.product.ProductCommand;
import com.loopers.domain.product.ProductInfo;

import java.util.List;
import java.util.Optional;

public interface ProductCacheRepository {

    Optional<List<ProductInfo.ProductQuery>> getCachedProductList(ProductCommand.Search search);

    void putCachedProductList(ProductCommand.Search search, List<ProductInfo.ProductQuery> value);

}
