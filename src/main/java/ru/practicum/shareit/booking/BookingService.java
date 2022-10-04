package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingService {

    Booking create(Booking data, long userId);

    Booking approved(long bookingId, Boolean isApproved, long userId);

    Booking getById(long bookingId, long userId);

    List<Booking> getAllUserBookings(String state, long userId);

    List<Booking> getAllUserItemsBookings(String state, long userId);

}
