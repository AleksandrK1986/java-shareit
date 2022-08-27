package ru.practicum.shareit.booking.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * // TODO .
 */
@AllArgsConstructor
@Setter
@Getter
public class Booking {
    private long id;
    private LocalDateTime end;
    private long item;
    private long booker;
    private Status status;
}
