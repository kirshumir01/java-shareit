package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice("ru.practicum.shareit")
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST) // 400
    public ErrorResponse handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        log.error("Ошибка валидации аргументов метода. Данные введены некорректно.");
        return new ErrorResponse("Ошибка валидации аргументов метода", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST) // 400
    public ErrorResponse handleMissingRequestHeaderException(final MissingRequestHeaderException e) {
        log.error("Ошибка валидации заголовка запроса. Данные введены некорректно.");
        return new ErrorResponse("Ошибка валидации заголовка запроса", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND) // 404
    public ErrorResponse handleNotFoundException(final NotFoundException e) {
        log.error("Ошибка поиска объекта. Объект не найден.");
        return new ErrorResponse("Ошибка поиска объекта", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT) // 409
    public ErrorResponse handleConflictException(final ConflictException e) {
        log.error("Возникла ошибка. Обнаружены совпадения.");
        return new ErrorResponse("Обнаружены совпадения", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN) // 403
    public ErrorResponse handleNotOwnerException(final NotOwnerException e) {
        log.error("Возникла ошибка. Пользователь не является владельцем вещи.");
        return new ErrorResponse("Пользователь не является владельцем вещи", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST) // 400
    public ErrorResponse handleBadRequest(final BadRequestException e) {
        log.error("Возникла ошибка. Ошибка формирования запроса.");
        return new ErrorResponse("Ошибка формирования запроса", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) // 500
    public ErrorResponse handleThrowable(final Throwable e) {
        log.error("Возникла внутренняя ошибка:", e);
        return new ErrorResponse("Внутренняя ошибка", e.getMessage());
    }
}