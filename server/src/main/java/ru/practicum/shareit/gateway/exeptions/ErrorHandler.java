package ru.practicum.shareit.gateway.exeptions;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler({ValidationException.class, MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(final Exception e) {
        log.error("Выброшено исключение, ошибка валидации: {}", e.getMessage());
        return new ErrorResponse("Ошибка валидации: ", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNoFoundElementException(final NotFoundException e) {
        log.error("Выброшено исключение, объект не найден: " + e.getMessage());
        return new ErrorResponse("Объект не найден: ", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleHttpMessageNotReadableException(final HttpMessageNotReadableException e) {
        log.error("Ошибка чтения тела запроса: {}", e.getMessage());
        return new ErrorResponse("Ошибка чтения JSON: ", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleIncorrectResultSizeDataAccessException(final IncorrectResultSizeDataAccessException e) {
        log.error("Выброшено исключение, объект не найден: " + e.getMessage());
        return new ErrorResponse("Объект не найден: ", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleConditionsNotMetException(final Exception e) {
        log.error("Выброшено исключение, другая ошибка: " + e);
        return new ErrorResponse("Другая ошибка : ", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleIllegalStateException(final IllegalStateException e) {
        log.error("Выброшено исключение, конфликт: {}", e.getMessage());
        return new ErrorResponse("Другая ошибка: ", e.getMessage());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleEntityNotFoundException(final EntityNotFoundException e) {
        log.error("Выброшено исключение, объект не найден: " + e.getMessage());
        return new ErrorResponse("Другая ошибка: ", e.getMessage());
    }
}