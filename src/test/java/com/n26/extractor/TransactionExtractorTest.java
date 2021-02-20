package com.n26.extractor;

import com.n26.entities.Transaction;
import com.n26.exceptions.InvalidJSONException;
import com.n26.exceptions.ParsingException;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.math.BigDecimal;

/**
 * Tests for Transaction extractor
 */
public class TransactionExtractorTest {

    /**
     * Tests for case when JSON string is NULL
     */
    @Test(expected = InvalidJSONException.class)
    public void testExtractTransactionOutOfJSONStringInCaseJSONIsNull() {
        TransactionExtractor.extract(null);
    }

    /**
     * Tests for case when timestamp is NULL
     */
    @Test(expected = InvalidJSONException.class)
    public void testExtractTransactionOutOfJSONStringInCaseTimestampIsNull() {
        String requestBody = "{\"amount\":\"1000\"}";
        TransactionExtractor.extract(requestBody);
    }

    /**
     * Tests for case when amount is invalid
     */
    @Test(expected = ParsingException.class)
    public void testExtractTransactionOutOfJSONStringInCaseAmountIsInvalid() {
        String requestBody = "{\"amount\":\"random\", \"timestamp\":\"2018-07-17T09:59:51.312Z\"}";
        TransactionExtractor.extract(requestBody);
    }

    /**
     * Tests for case when timestamp is invalid
     */
    @Test(expected = ParsingException.class)
    public void testExtractTransactionOutOfJSONStringInCaseDateIsInvalid() {
        String requestBody = "{\"amount\":\"1000\", \"timestamp\":\"random\"}";
        TransactionExtractor.extract(requestBody);
    }

    /**
     * Tests for case when JSON string is valid
     */
    @Test
    public void testExtractTransactionOutOfJSONStringInCaseJSONIsValid() {
        String requestBody = "{\"amount\":\"1000.0\", \"timestamp\":\"2018-07-17T09:59:51.312Z\"}";
        Transaction transaction = TransactionExtractor.extract(requestBody);

        Assertions.assertThat(transaction).isNotNull();
        Assertions.assertThat(transaction.getAmount()).isEqualTo(BigDecimal.valueOf(1000.0));
    }
}
