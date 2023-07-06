package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ItemNotFoundException extends IllegalArgumentException {
    public ItemNotFoundException(String message) {

        log.error(message);
    }
}
