package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;

import static ru.practicum.shareit.item.dto.ItemMapper.toItemDto;
import static ru.practicum.shareit.user.dto.UserMapper.toUserDto;

public class BookingMapper {

    public static BookingDto toBookingDto(Booking booking) {
        UserDto userDto = toUserDto(booking.getBooker());
        ItemDto itemDto = toItemDto(booking.getItem());
        return new BookingDto(
                booking.getId(),
                booking.getItem().getId(),
                itemDto,
                userDto,
                booking.getStart(),
                booking.getEnd(),
                booking.getStatus()
        );
    }

    public static BookingForItemDto toBookingForItemDto(Booking booking) {
        if (booking == null) {
            return null;
        } else {
            return new BookingForItemDto(
                    booking.getId(),
                    booking.getBooker().getId(),
                    booking.getStart(),
                    booking.getEnd(),
                    booking.getStatus()
            );
        }
    }

    public static Booking toBooking(BookingDto bookingDto) {
        Booking booking = new Booking();
        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        Item item = new Item();
        item.setId(bookingDto.getItemId());
        booking.setItem(item);
        return booking;
    }

}
