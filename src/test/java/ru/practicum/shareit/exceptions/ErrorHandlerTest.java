package ru.practicum.shareit.exceptions;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.practicum.shareit.exception.ErrorHandler;
import ru.practicum.shareit.exception.ErrorResponse;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UnknownStateException;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ErrorHandlerTest {



    @Test
    void handleException() {
        Exception exception = Mockito.mock(Exception.class);
        ErrorHandler errorHandler = new ErrorHandler();

        ErrorResponse errorResponse = errorHandler.handleException(exception);

        assertEquals("Внутренняя ошибка сервера" + exception.getMessage(), errorResponse.getError());
        // Add additional assertions for the ErrorResponse object if needed
    }

    @Test
    void handleNotFoundExceptions() {
        NotFoundException exception = Mockito.mock(NotFoundException.class);
        ErrorHandler errorHandler = new ErrorHandler();

        ErrorResponse errorResponse = errorHandler.handleNotFoundExceptions(exception);

        assertEquals(exception.getMessage(), errorResponse.getError());
        // Add additional assertions for the ErrorResponse object if needed
    }

    @Test
    void handleUnknownStateException() {
        UnknownStateException exception = Mockito.mock(UnknownStateException.class);
        ErrorHandler errorHandler = new ErrorHandler();

        Map<String, String> response = errorHandler.handleUnknownStateException(exception);

        assertEquals("Unknown state: " + exception.getMessage(), response.get("error"));
        // Add additional assertions for the returned Map if needed
    }

}