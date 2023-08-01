package ru.practicum.shareit.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserNotFoundException extends IllegalArgumentException {
    private static final Logger logger = LoggerFactory.getLogger(UserNotFoundException.class);

    public UserNotFoundException(String message) {
        super(message);
        logger.error(message);
    }
}