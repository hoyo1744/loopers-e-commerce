package com.loopers.infrastructure.pg;

import com.loopers.domain.pg.PgHistory;
import com.loopers.domain.pg.PgHistoryRepository;
import com.loopers.domain.pg.PgStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PgHistoryRepositoryImpl implements PgHistoryRepository {
    private final PgHistoryJpaRepository pgHistoryJpaRepository;


    @Override
    public PgHistory save(PgHistory pgHistory) {
        return pgHistoryJpaRepository.save(pgHistory);
    }

    @Override
    public List<PgHistory> findListByStatus(PgStatus status) {
        return pgHistoryJpaRepository.findByStatus(status);
    }

    @Override
    public int updateStatus(String orderNumber, PgStatus status) {
        return pgHistoryJpaRepository.updateStatusByIds(status, orderNumber);
    }

    @Override
    public void touchByOrderNumbers(List<String> orderNumbers) {
        pgHistoryJpaRepository.touchByOrderNumbers(orderNumbers);
    }
}
