package com.loopers.domain.pg;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PgHistoryService {

    private final PgHistoryRepository pgHistoryRepository;

    public List<PgHistoryInfo.Failed> getPaymentFailedList() {
        List<PgHistory> list = pgHistoryRepository.findListByStatus(PgStatus.FAILED);
        return list.stream().map(PgHistoryInfo.Failed::from).toList();
    }

    public void complete(String orderNumber) {
        pgHistoryRepository.updateStatus(orderNumber, PgStatus.SUCCESS);
    }

    public void touch(List<String> orderNumbers) {
        pgHistoryRepository.touchByOrderNumbers(orderNumbers);
    }

}
