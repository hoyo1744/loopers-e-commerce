package com.loopers.infrastructure.product;

import com.loopers.domain.product.ProductCommand;
import com.loopers.domain.product.ProductInfo;

import java.util.List;

public interface ProductQueryDslRepository {

    List<ProductInfo.ProductQuery> search(ProductCommand.Search command);

}
