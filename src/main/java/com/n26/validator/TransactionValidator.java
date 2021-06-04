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
        if (transaction.getTimestamp() <= evictionTimeInSeconds) {
            logger.error(String.format("Invalid timestamp, timestamp is before eviction threshold %s",
                    transaction.getTimestamp()));

            throw new InvalidTimestampException();
        }

        if (transaction.getTimestamp() > ((Instant.now().toEpochMilli()) / 1000L)) {
            String message = String.format("Timestamp is of future : %s", transaction.getTimestamp());
            logger.error(message);
            throw new ParsingException(message);
        }

    }
}
