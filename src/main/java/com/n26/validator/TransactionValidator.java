package com.n26.validator;

import com.n26.entities.Transaction;
import com.n26.exceptions.InvalidTimestampException;
import com.n26.exceptions.ParsingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;

/**
 * Validator for Transactions
 */
public class TransactionValidator {
    private static final Logger logger = LoggerFactory.getLogger(TransactionValidator.class);

    public static void validate(Transaction transaction, long evictionTimeInSeconds) {
        if (transaction.getTimestamp() <= evictionTimeInSeconds)
            throw new InvalidTimestampException();

        if (transaction.getTimestamp() > ((Instant.now().toEpochMilli()) / 1000L)) {
            logger.info(String.format("Timestamp is of future: %s", transaction.getTimestamp()));
            throw new ParsingException("Timestamp is of future");
        }

    }
}
