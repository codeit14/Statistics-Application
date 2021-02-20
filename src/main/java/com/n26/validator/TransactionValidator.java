package com.n26.validator;

import com.n26.entities.Transaction;
import com.n26.exceptions.InvalidTimestampException;
import com.n26.exceptions.ParsingException;

import java.time.Instant;

/**
 * Validator for Transactions
 */
public class TransactionValidator {

    public static void validate(Transaction transaction, long evictionTimeInSeconds) {
        if (transaction.getTimestamp() <= evictionTimeInSeconds)
            throw new InvalidTimestampException();

        if (transaction.getTimestamp() > ((Instant.now().toEpochMilli()) / 1000L))
            throw new ParsingException("Timestamp is of future");
    }
}
