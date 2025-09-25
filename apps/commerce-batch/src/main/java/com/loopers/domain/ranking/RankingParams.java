package com.loopers.domain.ranking;

import java.time.LocalDate;

public record RankingParams(LocalDate start, LocalDate end, double wLike, double wSales, double wPv) {
}
