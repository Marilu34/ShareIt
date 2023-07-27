package ru.practicum.shareit.exception;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NotFoundException extends IllegalArgumentException {
    private static final Logger logger = LoggerFactory.getLogger(NotFoundException.class);

    public NotFoundException(String message) {
        super(message);
        logger.error(message);
    }
}
