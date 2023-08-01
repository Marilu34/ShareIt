package ru.practicum.shareit.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserAlreadyExistsException extends IllegalArgumentException {
    private static final Logger logger = LoggerFactory.getLogger(UserAlreadyExistsException.class);

    public UserAlreadyExistsException(String message) {
        super(message);
        logger.error(message);
    }
}