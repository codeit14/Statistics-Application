package com.n26.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Data
@Builder
@AllArgsConstructor
public class Statistics {
    private BigDecimal sum;
    private BigDecimal avg;
    private BigDecimal max;
    private BigDecimal min;
    private long count;

    public static Statistics getDefaultInstance() {
        return Statistics.builder()
                .sum(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP))
                .avg(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP))
                .max(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP))
                .min(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP))
                .count(0L)
                .build();
    }

    public static Statistics getStatisticsForATransaction(Transaction transaction) {
        return Statistics.builder()
                .sum(transaction.getAmount())
                .avg(transaction.getAmount())
                .max(transaction.getAmount())
                .min(transaction.getAmount())
                .count(1L)
                .build();
    }

    public static Statistics merge(Statistics s1, Statistics s2) {
        Statistics statistics = getDefaultInstance();
        statistics.setSum(s1.getSum().add(s2.getSum().setScale(2, RoundingMode.HALF_UP)));

        if (s1.getMin().compareTo(s2.getMin()) >= 0 || s1.getMin().compareTo(BigDecimal.ZERO) == 0) {
            statistics.setMin(s2.getMin().setScale(2, RoundingMode.HALF_UP));
        } else {
            statistics.setMin(s1.getMin().setScale(2, RoundingMode.HALF_UP));
        }

        statistics.setMax(s1.getMax().max(s2.getMax().setScale(2, RoundingMode.HALF_UP)));
        statistics.setCount(s1.getCount() + s2.getCount());
        if (statistics.getCount() != 0L)
            statistics.setAvg(statistics.getSum().divide(
                    BigDecimal.valueOf(statistics.getCount()), 2, RoundingMode.HALF_UP));

        return statistics;
    }
}
