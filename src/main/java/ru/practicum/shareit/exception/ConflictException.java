package ru.practicum.shareit.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConflictException extends Exception {
    private static final Logger logger = LoggerFactory.getLogger(ConflictException.class);

    public ConflictException(String message) {
        super(message);
        logger.error(message);
    }
}