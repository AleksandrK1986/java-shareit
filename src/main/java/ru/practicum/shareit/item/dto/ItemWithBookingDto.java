package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.requests.ItemRequest;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@AllArgsConstructor
@Setter
@Getter
public class ItemWithBookingDto {
    private long id;
    private String name;
    private String description;
    private Boolean available;
    private UserDto owner;
    private ItemRequest request;
    private BookingForItemDto lastBooking;
    private BookingForItemDto nextBooking;
    private List<CommentDto> comments;
}
