package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;

/**
 * // TODO .
 */
@AllArgsConstructor
@Setter
@Getter
public class BookingDto {
    private long id;
    private LocalDateTime end;
    private long item;
    private long booker;
    private Status status;
}
