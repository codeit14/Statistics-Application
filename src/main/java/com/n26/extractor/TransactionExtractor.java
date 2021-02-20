package com.n26.extractor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.n26.entities.Transaction;
import com.n26.exceptions.InvalidJSONException;
import com.n26.exceptions.ParsingException;
import com.n26.validator.TransactionValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Utility class responsible for Extraction of Transaction from given JSON string
 */
public class TransactionExtractor {
    private static final Logger logger = LoggerFactory.getLogger(TransactionValidator.class);

    private static final String TIMESTAMP_ATTRIB = "timestamp";
    private static final String AMOUNT_ATTRIB = "amount";

    private static final Set<String> TRANSACTION_ATTRIBUTES =
            new HashSet<>(Arrays.asList(TIMESTAMP_ATTRIB, AMOUNT_ATTRIB));

    public static Transaction extract(String inputTransaction) {
        if (Objects.isNull(inputTransaction))
            throw new InvalidJSONException();

        ObjectMapper objectMapper = new ObjectMapper();
        Transaction transaction;

        try {
            JsonNode jsonNode = objectMapper.readValue(inputTransaction, JsonNode.class);

            if (isAnyOfTheAttributesNull(jsonNode)) {
                throw new InvalidJSONException();
            }

            String date = jsonNode.get(TIMESTAMP_ATTRIB).textValue();
            Date parsedDate = Date.from(ZonedDateTime.parse(date).toInstant());
            long timestampInMillis = parsedDate.getTime();

            String amountString = jsonNode.get(AMOUNT_ATTRIB).textValue();
            transaction = new Transaction(new BigDecimal(amountString), timestampInMillis / 1000L);
        } catch (IOException | NumberFormatException | DateTimeParseException e) {
            logger.error(String.format("Issue while parsing JSON: %s", e.getMessage()));
            throw new ParsingException("Issue while paring JSON fields for transaction");
        }

        return transaction;
    }

    private static boolean isAnyOfTheAttributesNull(JsonNode jsonNode) {
        for (String attribute : TRANSACTION_ATTRIBUTES) {
            if (Objects.isNull(jsonNode.get(attribute))) return true;
        }

        return false;
    }
}
