package com.n26.controllers;

import com.n26.entities.Transaction;
import com.n26.extractor.TransactionExtractor;
import com.n26.services.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

import static com.n26.constants.TransactionConstants.EVICTION_TIMESTAMP_LIMIT_IN_SECONDS;

@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TransactionController {
    private final TransactionService transactionService;

    @RequestMapping(value = "/transactions", method = RequestMethod.POST)
    public ResponseEntity<Void> addTransaction(@RequestBody String requestBody) {
        long currentTimeInSeconds = (Instant.now().toEpochMilli()) / 1000L;
        Transaction transaction = TransactionExtractor.extract(requestBody);
        transactionService.addTransaction(transaction, currentTimeInSeconds);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @RequestMapping(value = "/transactions", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteTransactions() {
        transactionService.deleteAllTransactions();
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
