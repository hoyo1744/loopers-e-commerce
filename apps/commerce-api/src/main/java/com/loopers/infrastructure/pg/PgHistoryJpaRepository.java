package com.loopers.infrastructure.pg;

import com.loopers.domain.pg.PgHistory;
import com.loopers.domain.pg.PgStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PgHistoryJpaRepository extends JpaRepository<PgHistory, Long> {

    List<PgHistory> findByStatus(PgStatus status);


    @Modifying
    @Query("UPDATE PgHistory ph SET ph.status = :status WHERE ph.orderNumber = :orderNumber")
    int updateStatusByIds(@Param("status") PgStatus status, @Param("orderNumber") String orderNumber);

    @Modifying
    @Query("UPDATE PgHistory p SET p.updatedAt = CURRENT_TIMESTAMP WHERE p.orderNumber IN :orderNumbers")
    void touchByOrderNumbers(@Param("orderNumbers") List<String> orderNumbers);

}
