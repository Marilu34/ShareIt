package ru.practicum.shareit.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BookingException extends IllegalArgumentException {
    private static final Logger logger = LoggerFactory.getLogger(ItemNotFoundException.class);

    public BookingException(String message) {
        super(message);
        logger.error(message);
    }
}