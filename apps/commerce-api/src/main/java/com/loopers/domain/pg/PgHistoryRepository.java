package com.loopers.domain.pg;

import java.util.List;

public interface PgHistoryRepository {

    PgHistory save(PgHistory pgHistory);

    List<PgHistory> findListByStatus(PgStatus status);

    int updateStatus(String orderNumber, PgStatus status);

    void touchByOrderNumbers(List<String> orderNumbers);
}
