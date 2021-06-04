package com.n26.services.impl;

import com.n26.dao.StatisticsDao;
import com.n26.entities.Statistics;
import com.n26.entities.Transaction;
import com.n26.services.TransactionService;
import com.n26.validator.TransactionValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static com.n26.constants.TransactionConstants.EVICTION_TIMESTAMP_LIMIT_IN_SECONDS;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TransactionServiceImpl implements TransactionService {
    private final StatisticsDao statisticsDao;

    @Override
    public void addTransaction(Transaction transaction, long currentTimeInSeconds) {
        long evictionTimeInSeconds = currentTimeInSeconds - EVICTION_TIMESTAMP_LIMIT_IN_SECONDS;
        TransactionValidator.validate(transaction, evictionTimeInSeconds);

        synchronized (this) {
            Statistics statistics = calculateStatistics(transaction);
            statisticsDao.add(transaction.getTimestamp(), statistics);
            evictEntriesBasedOnThreshold(evictionTimeInSeconds);
        }
    }

    @Override
    public void deleteAllTransactions() {
        statisticsDao.clear();
    }

    private Statistics calculateStatistics(Transaction transaction) {
        Statistics stats = Statistics.getStatisticsForATransaction(transaction);
        if (statisticsDao.contains(transaction.getTimestamp())) {
            Statistics statistics = statisticsDao.get(transaction.getTimestamp());

            stats.setCount(statistics.getCount() + 1L);
            stats.setSum(statistics.getSum().add(transaction.getAmount()).setScale(2, RoundingMode.HALF_UP));
            stats.setMax(statistics.getMax().max(transaction.getAmount()).setScale(2, RoundingMode.HALF_UP));
            stats.setMin(statistics.getMin().min(transaction.getAmount()).setScale(2, RoundingMode.HALF_UP));
            stats.setAvg(stats.getSum().divide(BigDecimal.valueOf(stats.getCount()), 2, RoundingMode.HALF_UP));
        }

        return stats;
    }

    private void evictEntriesBasedOnThreshold(long evictionThresholdInSeconds) {
        statisticsDao.getAll().forEach(timestamp -> {
            if (timestamp < evictionThresholdInSeconds)
                statisticsDao.remove(timestamp);
        });
    }
}
