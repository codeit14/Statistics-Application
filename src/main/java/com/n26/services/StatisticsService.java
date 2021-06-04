package com.n26.services;

import com.n26.dtos.StatisticsDto;

/**
 * Service responsible for calculation of statistics
 */
public interface StatisticsService {
    StatisticsDto getStatistics(long currentTimeInSeconds);
}
