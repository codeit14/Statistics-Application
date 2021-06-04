package com.n26.controller;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TransactionControllerTest {
    private static final String IS0_8601_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'";

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    public void testAddTransactionHappyFlow() {
        long currentTimeInSeconds = Instant.now().toEpochMilli() / 1000L;
        int nanoOfSecond = 0;
        ZoneOffset offset = ZoneOffset.UTC;
        LocalDateTime dateTime = LocalDateTime.ofEpochSecond(currentTimeInSeconds, nanoOfSecond, offset);
        String timestamp = DateTimeFormatter.ofPattern(IS0_8601_PATTERN).format(dateTime);

        String requestBody = String.format("{\"amount\":\"200.0\", \"timestamp\":\"%s\"}", timestamp);

        ResponseEntity<Void> response = testRestTemplate.postForEntity("/transactions", requestBody, Void.class);
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    public void testAddTransactionWhenUnprocessableEntity() {
        long currentTimeInSeconds = Instant.now().toEpochMilli() / 1000L;
        int nanoOfSecond = 0;
        ZoneOffset offset = ZoneOffset.UTC;
        LocalDateTime dateTime = LocalDateTime.ofEpochSecond(currentTimeInSeconds, nanoOfSecond, offset);
        String timestamp = DateTimeFormatter.ofPattern(IS0_8601_PATTERN).format(dateTime);

        String requestBody = String.format("{\"amount\":\"abc\", \"timestamp\":\"%s\"}", timestamp);

        ResponseEntity<Void> response = testRestTemplate.postForEntity("/transactions", requestBody, Void.class);
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @Test
    public void testAddTransactionWhenNoContent() {
        long currentTimeInSeconds = Instant.now().toEpochMilli() / 1000L;
        int nanoOfSecond = 0;
        ZoneOffset offset = ZoneOffset.UTC;
        LocalDateTime dateTime = LocalDateTime.ofEpochSecond(currentTimeInSeconds - 3000L, nanoOfSecond, offset);
        String timestamp = DateTimeFormatter.ofPattern(IS0_8601_PATTERN).format(dateTime);

        String requestBody = String.format("{\"amount\":\"2000.0\", \"timestamp\":\"%s\"}", timestamp);

        ResponseEntity<Void> response = testRestTemplate.postForEntity("/transactions", requestBody, Void.class);
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }


    @Test
    public void testAddTransactionWhenBadRequest() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        ResponseEntity<Void> response = testRestTemplate.exchange("/transactions", HttpMethod.POST,
                new HttpEntity<>("random something", headers), Void.class);

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void testDeleteTransactions() {
        ResponseEntity<Void> response = testRestTemplate.exchange("/transactions", HttpMethod.DELETE, null,
                Void.class);

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }
}
