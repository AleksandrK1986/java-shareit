package ru.practicum.shareit.booking.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

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
    private Item item;
    private User booker;
    private Status status;
}
