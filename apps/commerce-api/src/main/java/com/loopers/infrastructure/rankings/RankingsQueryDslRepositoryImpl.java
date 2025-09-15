package com.loopers.infrastructure.rankings;

import com.loopers.domain.brand.QBrand;
import com.loopers.domain.product.QProduct;
import com.loopers.domain.rankings.RankingsInfo;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.loopers.domain.brand.QBrand.*;
import static com.loopers.domain.product.QProduct.*;

@Repository
@RequiredArgsConstructor
public class RankingsQueryDslRepositoryImpl implements RankingsQueryDslRepository{

    private final JPAQueryFactory queryFactory;

    @Override
    public List<RankingsInfo.Product> search(List<Long> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            return List.of();
        }

        return queryFactory
                .select(Projections.constructor(
                        RankingsInfo.Product.class,
                        product.id,
                        product.name,
                        product.price,
                        brand.name,
                        product.likeCount
                ))
                .from(product)
                .join(brand).on(product.brandId.eq(brand.id))
                .where(product.id.in(productIds))
                .fetch();
    }
}
