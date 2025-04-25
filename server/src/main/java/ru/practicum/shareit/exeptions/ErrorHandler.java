package ru.practicum.shareit.exeptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

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
}