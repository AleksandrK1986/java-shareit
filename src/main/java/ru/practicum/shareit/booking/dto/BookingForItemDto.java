package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;

@AllArgsConstructor
@Setter
@Getter
public class BookingForItemDto {
    private long id;
    private long bookerId;
    private LocalDateTime start;
    private LocalDateTime end;
    private Status status;
}
