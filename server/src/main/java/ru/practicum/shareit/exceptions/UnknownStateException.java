package ru.practicum.shareit.exceptions;

public class UnknownStateException extends RuntimeException {
    public UnknownStateException(String unknownState) {
        super(unknownState);

    }
}