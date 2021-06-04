package com.n26.dao;

import com.n26.entities.Statistics;
import com.n26.entities.Transaction;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;

/**
 * Tests for Statistics DAO
 */
public class StatisticsDaoTest {
    private static final Transaction SAMPLE_TRANSACTION_1 = new Transaction(BigDecimal.TEN, 1000L);
    private static final Transaction SAMPLE_TRANSACTION_2 = new Transaction(BigDecimal.ONE, 2000L);

    /**
     * Test insertion of a single statistic and its fetch
     */
    @Test
    public void testInsertionAndFetchSingleKey() {
        StatisticsDao statsDao = new StatisticsDao();
        statsDao.add(SAMPLE_TRANSACTION_1.getTimestamp(), Statistics.getStatisticsForATransaction(SAMPLE_TRANSACTION_1));

        Assertions.assertThat(statsDao.get(SAMPLE_TRANSACTION_1.getTimestamp()))
                .isEqualTo(Statistics.getStatisticsForATransaction(SAMPLE_TRANSACTION_1));
    }

    /**
     * Test insertion of multiple statistics
     */
    @Test
    public void testGetAllKeys() {
        StatisticsDao statsDao = new StatisticsDao();
        statsDao.add(SAMPLE_TRANSACTION_1.getTimestamp(), Statistics.getStatisticsForATransaction(SAMPLE_TRANSACTION_1));
        statsDao.add(SAMPLE_TRANSACTION_2.getTimestamp(), Statistics.getStatisticsForATransaction(SAMPLE_TRANSACTION_2));

        List<Long> allKeys = statsDao.getAll();
        Assertions.assertThat(allKeys).hasSize(2);
        Assertions.assertThat(allKeys)
                .containsExactlyInAnyOrder(SAMPLE_TRANSACTION_1.getTimestamp(), SAMPLE_TRANSACTION_2.getTimestamp());
    }

    /**
     * Tests presence of a given timestamp
     */
    @Test
    public void testContainsKey() {
        StatisticsDao statsDao = new StatisticsDao();
        statsDao.add(SAMPLE_TRANSACTION_1.getTimestamp(), Statistics.getStatisticsForATransaction(SAMPLE_TRANSACTION_1));

        Assertions.assertThat(statsDao.contains(SAMPLE_TRANSACTION_1.getTimestamp())).isTrue();
        Assertions.assertThat(statsDao.contains(SAMPLE_TRANSACTION_2.getTimestamp())).isFalse();
    }

    /**
     * Tests cleanup of data
     */
    @Test
    public void testClearAllData() {
        StatisticsDao statsDao = new StatisticsDao();
        statsDao.add(SAMPLE_TRANSACTION_1.getTimestamp(), Statistics.getStatisticsForATransaction(SAMPLE_TRANSACTION_1));
        statsDao.add(SAMPLE_TRANSACTION_2.getTimestamp(), Statistics.getStatisticsForATransaction(SAMPLE_TRANSACTION_2));

        statsDao.clear();
        Assertions.assertThat(statsDao.getAll()).hasSize(0);
    }

    /**
     * Tests removal of key
     */
    @Test
    public void testRemoveKey() {
        StatisticsDao statsDao = new StatisticsDao();
        statsDao.add(SAMPLE_TRANSACTION_1.getTimestamp(), Statistics.getStatisticsForATransaction(SAMPLE_TRANSACTION_1));
        statsDao.add(SAMPLE_TRANSACTION_2.getTimestamp(), Statistics.getStatisticsForATransaction(SAMPLE_TRANSACTION_2));

        statsDao.remove(SAMPLE_TRANSACTION_1.getTimestamp());
        Assertions.assertThat(statsDao.contains(SAMPLE_TRANSACTION_1.getTimestamp())).isFalse();
        Assertions.assertThat(statsDao.contains(SAMPLE_TRANSACTION_2.getTimestamp())).isTrue();
    }
}
