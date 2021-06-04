package com.n26.exceptions;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.*;

@ControllerAdvice
public class StatisticsExceptionHandler {

    @ResponseStatus(UNPROCESSABLE_ENTITY)
    @ExceptionHandler(ParsingException.class)
    public void handleParsingException() {
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(InvalidJSONException.class)
    public void handleInvalidJSONException() {
    }

    @ResponseStatus(NO_CONTENT)
    @ExceptionHandler(InvalidTimestampException.class)
    public void handleInvalidTimestampException() {
    }
}