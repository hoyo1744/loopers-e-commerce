package com.loopers.domain.product;

import java.util.List;

public interface ProductRepository {

    List<Product> findAll();

    Product findById(Long id);

    Product save(Product product);

    List<ProductInfo.ProductQuery> search(ProductCommand.Search command);

    void increaseLikeCount(Long id);

    void decreaseLikeCount(Long id);
}
