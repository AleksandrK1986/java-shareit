package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

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
    private ItemDto item;
    private UserDto booker;
    private Status status;
}
