    package com.loopers.infrastructure.product;

    import com.loopers.domain.product.Product;
    import org.springframework.data.jpa.repository.JpaRepository;
    import org.springframework.data.jpa.repository.Modifying;
    import org.springframework.data.jpa.repository.Query;
    import org.springframework.data.repository.query.Param;

    public interface ProductJpaRepository extends JpaRepository<Product, Long> {


        @Modifying
        @Query("update Product p set p.likeCount = p.likeCount + 1 where p.id = :id")
        void increaseLikeCount(@Param("id") Long id);

        @Modifying
        @Query("update Product p set p.likeCount = p.likeCount - 1 where p.id = :id")
        void decreaseLikeCount(@Param("id") Long id);
    }
