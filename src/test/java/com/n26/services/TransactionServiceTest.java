package com.n26.services;

import com.n26.dao.StatisticsDao;
import com.n26.entities.Statistics;
import com.n26.entities.Transaction;
import com.n26.services.impl.TransactionServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Tests for Transaction Service
 */
@RunWith(MockitoJUnitRunner.class)
public class TransactionServiceTest {
    private static final long currentTimeInSeconds = System.currentTimeMillis() / 1000L;
    private static final Transaction SAMPLE_TRANSACTION =
            new Transaction(BigDecimal.valueOf(100), currentTimeInSeconds - 20L);

    @Mock
    private StatisticsDao statisticsDao;
    private TransactionService transactionService;

    @Before
    public void setup() {
        transactionService = new TransactionServiceImpl(statisticsDao);
    }

    /**
     * Tests to add transaction in case statistics already present for timestamp same as transaction's timestamp
     */
    @Test
    public void testAddTransactionIfTimestampAlreadyPresent() {
        Statistics expectedStats = Statistics.builder()
                .sum(BigDecimal.valueOf(200).setScale(2, RoundingMode.HALF_UP))
                .count(3L)
                .min(BigDecimal.valueOf(30).setScale(2, RoundingMode.HALF_UP))
                .max(BigDecimal.valueOf(100).setScale(2, RoundingMode.HALF_UP))
                .avg(BigDecimal.valueOf(66.67).setScale(2, RoundingMode.HALF_UP))
                .build();
        when(statisticsDao.contains(SAMPLE_TRANSACTION.getTimestamp())).thenReturn(true);
        when(statisticsDao.get(SAMPLE_TRANSACTION.getTimestamp())).thenReturn(Statistics.builder()
                .sum(BigDecimal.valueOf(100))
                .count(2L)
                .min(BigDecimal.valueOf(30))
                .max(BigDecimal.valueOf(70))
                .avg(BigDecimal.valueOf(50))
                .build());

        List<Long> timestamps = new ArrayList<>();
        timestamps.add(SAMPLE_TRANSACTION.getTimestamp());
        timestamps.add(SAMPLE_TRANSACTION.getTimestamp() - 65);

        when(statisticsDao.getAll()).thenReturn(timestamps);

        transactionService.addTransaction(SAMPLE_TRANSACTION, currentTimeInSeconds);
        ArgumentCaptor<Statistics> statsCaptor = ArgumentCaptor.forClass(Statistics.class);
        verify(statisticsDao, times(1)).add(eq(SAMPLE_TRANSACTION.getTimestamp()), statsCaptor.capture());
        Assertions.assertThat(statsCaptor.getValue()).isEqualTo(expectedStats);

        // Old transactions gets evicted
        verify(statisticsDao, times(1)).remove(SAMPLE_TRANSACTION.getTimestamp() - 65);
    }

    /**
     * Tests to add transaction in case it's a first transaction for the given timestamp
     */
    @Test
    public void testAddTransactionIfTimestampNotPresent() {
        Statistics expectedStats = Statistics.getStatisticsForATransaction(SAMPLE_TRANSACTION);
        when(statisticsDao.contains(SAMPLE_TRANSACTION.getTimestamp())).thenReturn(false);

        transactionService.addTransaction(SAMPLE_TRANSACTION, currentTimeInSeconds);
        ArgumentCaptor<Statistics> statsCaptor = ArgumentCaptor.forClass(Statistics.class);
        verify(statisticsDao, times(1)).add(eq(SAMPLE_TRANSACTION.getTimestamp()), statsCaptor.capture());
        Assertions.assertThat(statsCaptor.getValue()).isEqualTo(expectedStats);
    }

    /**
     * Tests to verify delete all transactions
     */
    @Test
    public void testDeleteAllTransactions() {
        transactionService.deleteAllTransactions();
        verify(statisticsDao, times(1)).clear();
    }
}
