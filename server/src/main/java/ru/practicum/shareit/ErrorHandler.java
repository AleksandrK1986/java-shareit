package ru.practicum.shareit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.requests.ItemRequestController;
import ru.practicum.shareit.user.UserController;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.NoSuchElementException;

@Slf4j
@ControllerAdvice(assignableTypes = {ItemController.class,
        UserController.class,
        BookingController.class,
        ItemRequestController.class})
public class ErrorHandler {
    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleNoSuchElement(final NoSuchElementException e) {
        log.warn("Ошибка поиска объекта {}    Причина: {}", LocalDateTime.now(), e.getMessage());
        return new ResponseEntity<>(
                Map.of("error", e.getMessage()),
                HttpStatus.NOT_FOUND
        );
    }

    /*@ExceptionHandler
    public ResponseEntity<Map<String, String>> handleValidation(final ValidationException e) {
        log.warn("Ошибка проверки объекта {}    Причина: {}", LocalDateTime.now(), e.getMessage());
        return new ResponseEntity<>(
                Map.of("error", e.getMessage()),
                HttpStatus.BAD_REQUEST
        );
    }*/

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handlePSQLExceptionHelper(DataIntegrityViolationException e) {
        log.warn("Ошибка сохранения объекта: " + e.getMessage());
        return new ResponseEntity<>(
                Map.of("error", e.getMessage()),
                HttpStatus.CONFLICT
        );
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleNullPoint(final NullPointerException e) {
        log.warn("Ошибка проверки объекта {}    Причина: {}", LocalDateTime.now(), e.getMessage());
        return new ResponseEntity<>(
                Map.of("error", e.getMessage()),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleException(final RuntimeException e) {
        log.warn("Ошибка {}    Причина: {}", LocalDateTime.now(), e.getMessage());
        return new ResponseEntity<>(
                Map.of("error", e.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

}
