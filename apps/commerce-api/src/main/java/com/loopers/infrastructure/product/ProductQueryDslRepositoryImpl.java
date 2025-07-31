package com.loopers.infrastructure.product;

import com.loopers.domain.product.ProductCommand;
import com.loopers.domain.product.ProductInfo;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.loopers.domain.brand.QBrand.brand;
import static com.loopers.domain.like.QLike.like;
import static com.loopers.domain.product.QProduct.product;
import static com.loopers.domain.stock.QStock.stock;


@Repository
@RequiredArgsConstructor
public class ProductQueryDslRepositoryImpl implements ProductQueryDslRepository{

    private final JPAQueryFactory queryFactory;


    @Override
    public List<ProductInfo.ProductQuery> search(ProductCommand.Search command) {
        return queryFactory
                .select(Projections.constructor(ProductInfo.ProductQuery.class,
                        product.id,
                        product.name,
                        product.price,
                        product.createdAt,
                        product.brandId,
                        brand.name,
                        Expressions.constant(false),
                        like.count()
                ))
                .from(product)
                .join(brand).on(product.brandId.eq(brand.id))
                .leftJoin(like).on(like.productId.eq(product.id))
                .groupBy(product.id, product.name, product.price, product.createdAt, product.brandId, brand.name)
                .orderBy(getSortSpecifier(command.getSort()))
                .offset(command.getPage() * command.getSize())
                .limit(command.getSize())
                .fetch();
    }


    private OrderSpecifier<?> getSortSpecifier(String sort) {
        return switch (sort) {
            case "price_asc" -> product.price.asc();
            case "likes_desc" -> like.count().desc();
            case "latest" -> product.createdAt.desc();
            default -> product.createdAt.desc(); // 기본값
        };
    }
}
