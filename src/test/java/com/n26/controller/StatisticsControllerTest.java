package com.n26.controller;

import com.n26.dtos.StatisticsDto;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StatisticsControllerTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    public void testGetStatistics() {
        long currentTimeInSeconds = Instant.now().toEpochMilli() / 1000L;
        testRestTemplate.delete("/transactions");

        StatisticsDto statisticsDto = getStatistics();

        int nanoOfSecond = 0;
        ZoneOffset offset = ZoneOffset.UTC;
        LocalDateTime dateTimeInIS08601Format = LocalDateTime.ofEpochSecond(currentTimeInSeconds, nanoOfSecond, offset);
        String timestamp = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'").format(dateTimeInIS08601Format);

        String requestBody1 = String.format("{\"amount\":\"200.0\", \"timestamp\":\"%s\"}", timestamp);
        String requestBody2 = String.format("{\"amount\":\"202.0\", \"timestamp\":\"%s\"}", timestamp);
        String requestBody3 = String.format("{\"amount\":\"204.0\", \"timestamp\":\"%s\"}", timestamp);

        testRestTemplate.postForEntity("/transactions", requestBody1, Void.class);
        testRestTemplate.postForEntity("/transactions", requestBody2, Void.class);
        testRestTemplate.postForEntity("/transactions", requestBody3, Void.class);

        ResponseEntity<StatisticsDto> response = testRestTemplate.getForEntity("/statistics", StatisticsDto.class);
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(statisticsDto).isEqualTo(response.getBody());
    }

    private StatisticsDto getStatistics() {
        StatisticsDto statisticsDto = new StatisticsDto();
        statisticsDto.setCount(3);
        statisticsDto.setMin(BigDecimal.valueOf(200).setScale(2, RoundingMode.HALF_UP).toString());
        statisticsDto.setMax(BigDecimal.valueOf(204).setScale(2, RoundingMode.HALF_UP).toString());
        statisticsDto.setAvg(BigDecimal.valueOf(202).setScale(2, RoundingMode.HALF_UP).toString());
        statisticsDto.setSum(BigDecimal.valueOf(606).setScale(2, RoundingMode.HALF_UP).toString());
        return statisticsDto;
    }
}
