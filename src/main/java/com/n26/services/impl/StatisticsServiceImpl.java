package com.n26.services.impl;

import com.n26.dao.StatisticsDao;
import com.n26.dtos.StatisticsDto;
import com.n26.entities.Statistics;
import com.n26.services.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.n26.constants.TransactionConstants.EVICTION_TIMESTAMP_LIMIT_IN_SECONDS;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class StatisticsServiceImpl implements StatisticsService {
    private final StatisticsDao statisticsDao;

    @Override
    public StatisticsDto getStatistics(long currentTimeInSeconds) {
        long evictionTimeInSeconds = currentTimeInSeconds - EVICTION_TIMESTAMP_LIMIT_IN_SECONDS;
        Statistics resultant =  statisticsDao.getAll().stream()
                .filter(timestamp -> timestamp > evictionTimeInSeconds)
                .map(statisticsDao::get).reduce(Statistics.getDefaultInstance(), Statistics::merge);

        return StatisticsDto.getStatisticsDto(resultant);
    }
}
