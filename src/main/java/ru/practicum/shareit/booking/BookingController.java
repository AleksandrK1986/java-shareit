package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import java.util.ArrayList;
import java.util.List;

import static ru.practicum.shareit.booking.dto.BookingMapper.toBooking;
import static ru.practicum.shareit.booking.dto.BookingMapper.toBookingDto;


/**
 * // TODO .
 */
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {

    private BookingService service;

    @Autowired
    public BookingController(BookingService service) {
        this.service = service;
    }

    @PostMapping
    public BookingDto create(@Valid @RequestBody BookingDto bookingDto,
                             @RequestHeader(value = "X-Sharer-User-Id") long userId) {
        return toBookingDto(service.create(toBooking(bookingDto), userId));
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approved(@PathVariable long bookingId,
                               @RequestHeader(value = "X-Sharer-User-Id") long userId,
                               @RequestParam Boolean approved) {
        return toBookingDto(service.approved(bookingId, approved, userId));
    }

    @GetMapping("/{bookingId}")
    public BookingDto findById(@PathVariable long bookingId,
                               @RequestHeader(value = "X-Sharer-User-Id") long userId) {
        return toBookingDto(service.getById(bookingId, userId));
    }

    @GetMapping
    public List<BookingDto> getAllUserBookings(@RequestHeader(value = "X-Sharer-User-Id") long userId,
                                               @RequestParam(name = "state", defaultValue = "ALL") String state,
                                               @RequestParam(name = "from", required = false) @PositiveOrZero Integer from,
                                               @RequestParam(name = "size", required = false) @Positive Integer size) {
        List<BookingDto> bookingsDto = new ArrayList();
        List<Booking> bookings = service.getAllUserBookings(state, userId, from, size);
        if (bookings != null) {
            for (Booking b : bookings) {
                bookingsDto.add(toBookingDto(b));
            }
        }
        return bookingsDto;
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllUserItemsBookings(@RequestHeader(value = "X-Sharer-User-Id") long userId,
                                                    @RequestParam(name = "state", defaultValue = "ALL") String state,
                                                    @RequestParam(name = "from", required = false) @PositiveOrZero Integer from,
                                                    @RequestParam(name = "size", required = false) @Positive Integer size) {
        List<BookingDto> bookingsDto = new ArrayList();
        List<Booking> bookings = service.getAllUserItemsBookings(state, userId, from, size);
        if (bookings != null) {
            for (Booking b : bookings) {
                bookingsDto.add(toBookingDto(b));
            }
        }
        return bookingsDto;
    }

}
