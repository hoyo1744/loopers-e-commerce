package com.loopers.infrastructure.product;

import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductCommand;
import com.loopers.domain.product.ProductInfo;
import com.loopers.domain.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {

    private final ProductJpaRepository productJpaRepository;

    private final ProductQueryDslRepository productQueryDslRepository;

    private final ProductCacheRepository productCacheRepository;

    @Override
    public List<Product> findAll() {
        return productJpaRepository.findAll();
    }

    @Override
    public Product findById(Long id) {
        return productJpaRepository.findById(id).orElseThrow(
                () -> new NoSuchElementException("상품 ID  : " + id + "는 존재하지 않는 상품입니다.")
        );
    }

    @Override
    public Product save(Product product) {
        return productJpaRepository.save(product);
    }

    @Override
    public List<ProductInfo.ProductQuery> search(ProductCommand.Search command) {
        Optional<List<ProductInfo.ProductQuery>> cached = productCacheRepository.getCachedProductList(command);
        if(cached.isPresent()) {
            return cached.get();
        }
        List<ProductInfo.ProductQuery> result
                = productQueryDslRepository.search(command);
        productCacheRepository.putCachedProductList(command, result);

        return result;
    }

    @Override
    public void increaseLikeCount(Long id) {
        productJpaRepository.increaseLikeCount(id);
    }

    @Override
    public void decreaseLikeCount(Long id) {
        productJpaRepository.decreaseLikeCount(id);
    }
}
