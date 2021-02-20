package com.n26.validator;

import com.n26.entities.Transaction;
import com.n26.exceptions.InvalidTimestampException;
import com.n26.exceptions.ParsingException;
import org.junit.Test;

import java.math.BigDecimal;

public class TransactionValidatorTest {
    private static final long currentTimeInSeconds = System.currentTimeMillis() / 1000L;
    private static final Transaction SAMPLE_TRANSACTION_1 = new Transaction(BigDecimal.TEN, 1000L);
    private static final Transaction SAMPLE_TRANSACTION_2 = new Transaction(BigDecimal.TEN, currentTimeInSeconds + 1000L);

    @Test(expected = InvalidTimestampException.class)
    public void testValidateTransactionInCaseBeforeThresholdTime() {
        TransactionValidator.validate(SAMPLE_TRANSACTION_1, 2000L);
    }

    @Test(expected = ParsingException.class)
    public void testValidateTransactionInCaseOfFutureTransaction() {
        TransactionValidator.validate(SAMPLE_TRANSACTION_2, 500L);
    }
}
