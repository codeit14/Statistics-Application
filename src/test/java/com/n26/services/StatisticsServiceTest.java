package com.n26.services;

import com.n26.dao.StatisticsDao;
import com.n26.dtos.StatisticsDto;
import com.n26.entities.Statistics;
import com.n26.services.impl.StatisticsServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class StatisticsServiceTest {
    @Mock
    private StatisticsDao statisticsDao;
    private StatisticsService statisticsService;

    @Before
    public void setup() {
        statisticsService = new StatisticsServiceImpl(statisticsDao);
    }

    /**
     * Test to calculate statistics
     * 3 Sample points: Only 2 should be considered in calculations
     */
    @Test
    public void testGetStatistics() {
        List<Long> timestamps = Arrays.asList(1000L, 2000L, 2100L);
        when(statisticsDao.getAll()).thenReturn(timestamps);
        timestamps.forEach(timestamp -> when(statisticsDao.get(timestamp))
                .thenReturn(Statistics.builder()
                        .count(2)
                        .sum(BigDecimal.valueOf(200))
                        .avg(BigDecimal.valueOf(100))
                        .max(BigDecimal.valueOf(150))
                        .min(BigDecimal.valueOf(50))
                        .build()));

        StatisticsDto expectedStatsDto = StatisticsDto.builder()
                .count(4L)
                .sum("400.00")
                .avg("100.00")
                .max("150.00")
                .min("50.00")
                .build();

        StatisticsDto actualStatsDto = statisticsService.getStatistics(1500L);
        Assertions.assertThat(actualStatsDto).isNotNull();
        Assertions.assertThat(actualStatsDto).isEqualTo(expectedStatsDto);
    }
}
