package com.n26.controllers;

import com.n26.dtos.StatisticsDto;
import com.n26.services.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

import static com.n26.constants.TransactionConstants.EVICTION_TIMESTAMP_LIMIT_IN_SECONDS;

@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class StatisticsController {
    private final StatisticsService statisticsService;

    @RequestMapping(value = "/statistics", method = RequestMethod.GET)
    public ResponseEntity<StatisticsDto> getStatistics() {
        long currentTimeInSeconds = (Instant.now().toEpochMilli()) / 1000L;
        StatisticsDto statistics =  statisticsService.getStatistics(currentTimeInSeconds);
        return new ResponseEntity<>(statistics, HttpStatus.OK);
    }

}
