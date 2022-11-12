package ru.practicum.shareit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.user.UserController;

import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@ControllerAdvice(assignableTypes = {ItemController.class,
        UserController.class,
        BookingController.class})
public class ErrorHandler {

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(final IllegalArgumentException e) {
        log.warn("Ошибка {}    Причина: {}", LocalDateTime.now(), e.getMessage());
        return new ResponseEntity<>(
                Map.of("error", e.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}