package ru.practicum.shareit.gateway.exeptions;

public class ValidationException extends RuntimeException {

    public ValidationException(String message) {
        super(message);
    }
}