package ru.practicum.shareit.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ValidationException extends IllegalArgumentException {
    private static final Logger logger = LoggerFactory.getLogger(ValidationException.class);

    public ValidationException(String message) {
        super(message);
        logger.error(message);
    }
}