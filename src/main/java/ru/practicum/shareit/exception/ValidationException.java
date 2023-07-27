package ru.practicum.shareit.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ValidationException extends Exception {
    private static final Logger logger = LoggerFactory.getLogger(ValidationException.class);

    public ValidationException(String message) {
        super(message);
        logger.error(message);
    }
}