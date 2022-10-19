package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class BookingServiceImpl implements BookingService {

    private BookingRepository bookingRepository;
    private UserRepository userRepository;
    private ItemRepository itemRepository;

    @Autowired
    public BookingServiceImpl(BookingRepository bookingRepository,
                              UserRepository userRepository,
                              ItemRepository itemRepository) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    public Booking create(Booking data, long userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("Передан некорректный id пользователя");
        }
        if (!itemRepository.existsById(data.getItem().getId())) {
            throw new NoSuchElementException("Передан некорректный id вещи");
        }
        if (!itemRepository.getReferenceById(data.getItem().getId()).getAvailable()) {
            throw new ValidationException("Вещь не доступна для бронирования");
        }
        if (data.getStart().isBefore(LocalDateTime.now()) ||
                data.getStart().isAfter(data.getEnd()) ||
                data.getEnd().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Передано не корректное время бронирования");
        }
        if (itemRepository.getReferenceById(data.getItem().getId()).getOwner().getId() == userId) {
            throw new NoSuchElementException("Вледалец вещи не межет бронировать вещь");
        }
        User user = userRepository.getReferenceById(userId);
        data.setBooker(user);
        data.setItem(itemRepository.getReferenceById(data.getItem().getId()));
        data.setStatus(Status.WAITING);
        return bookingRepository.save(data);
    }

    public Booking approved(long bookingId, Boolean isApproved, long userId) {
        User owner = userRepository.getReferenceById(userId);
        Booking booking = bookingRepository.getReferenceById(bookingId);
        Item item = itemRepository.getReferenceById(booking.getItem().getId());
        if (booking.getStatus().equals(Status.APPROVED)) {
            throw new ValidationException("Бронирование уже подтвержденено");
        }
        if (item.getOwner() != owner) {
            throw new NoSuchElementException("Подтверждать бронирование может только владелец вещи");
        }
        if (isApproved) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }
        return bookingRepository.save(booking);
    }

    public Booking getById(long bookingId, long userId) {
        User user = userRepository.getReferenceById(userId);
        if (!bookingRepository.existsById(bookingId)) {
            throw new NoSuchElementException("Бронирование не найдено");
        }
        Booking booking = bookingRepository.getReferenceById(bookingId);
        if (booking.getBooker() != user && booking.getItem().getOwner() != user) {
            throw new NoSuchElementException("Получить бронирование может или владелец вещи, или автор бронирования");
        }
        return booking;
    }

    public List<Booking> getAllUserBookings(String state, long userId, Integer from, Integer size) {
        checkUser(userId);
        User user = userRepository.getReferenceById(userId);
        List<Booking> bookings = new ArrayList<>();
        try {
            switch (State.valueOf(state)) {
                case ALL:
                    if (from == null || size == null) {
                        bookings = bookingRepository.findBookingsByBookerAndStatusOrStatusOrderByStartDesc(
                                user,
                                Status.APPROVED,
                                Status.WAITING);
                    } else {
                        if (from < 0 || size <= 0) {
                            throw new ValidationException("Передан некорректный размер ожидаемого ответа: from "
                                    + from + ", size " + size);
                        }
                        Sort sortBy = Sort.by(Sort.Direction.DESC, "start");
                        Pageable page = PageRequest.of(from / size, from / size, sortBy);
                        do {
                            Page<Booking> bookingsPage = bookingRepository.findBookingsByBookerAndStatusOrStatusOrderByStartDesc(
                                    user,
                                    Status.APPROVED,
                                    Status.WAITING,
                                    page);
                            for (Booking b : bookingsPage.getContent()) {
                                bookings.add(b);
                            }
                            if (bookingsPage.hasNext()) {
                                page = bookingsPage.nextOrLastPageable();
                            } else {
                                page = null;
                            }
                        } while (page != null);
                    }
                    break;
                case CURRENT:
                    bookings = bookingRepository.findBookingsByBookerAndStartBeforeAndEndAfterOrderByStartDesc(
                            user,
                            LocalDateTime.now(),
                            LocalDateTime.now());
                    break;
                case PAST:
                    bookings = bookingRepository.findBookingsByBookerAndEndBeforeOrderByStartDesc(
                            user,
                            LocalDateTime.now());
                    break;
                case FUTURE:
                    bookings = bookingRepository.findBookingsByBookerAndStartAfterOrderByStartDesc(
                            user,
                            LocalDateTime.now());
                    break;
                case WAITING:
                    bookings = bookingRepository.findBookingsByBookerAndStatusOrderByStartDesc(user, Status.WAITING);
                    break;
                case REJECTED:
                    bookings = bookingRepository.findBookingsByBookerAndStatusOrderByStartDesc(user, Status.REJECTED);
                    break;
            }
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
        }
        return bookings;
    }

    public List<Booking> getAllUserItemsBookings(String state, long userId, Integer from, Integer size) {
        checkUser(userId);
        User user = userRepository.getReferenceById(userId);
        List<Booking> bookings = new ArrayList<>();
        try {
            switch (State.valueOf(state)) {
                case ALL:
                    if (from == null || size == null) {
                        bookings = bookingRepository.findBookingsByItem_OwnerOrderByStartDesc(user);
                    } else {
                        if (from < 0 || size <= 0) {
                            throw new ValidationException("Передан некорректный размер ожидаемого ответа: from "
                                    + from + ", size " + size);
                        }
                        Sort sortBy = Sort.by(Sort.Direction.DESC, "start");
                        Pageable page = PageRequest.of(from / size, from / size, sortBy);
                        do {
                            Page<Booking> bookingsPage = bookingRepository.findBookingsByItem_OwnerOrderByStartDesc(user, page);
                            for (Booking b : bookingsPage.getContent()) {
                                bookings.add(b);
                            }
                            if (bookingsPage.hasNext()) {
                                page = bookingsPage.nextOrLastPageable();
                            } else {
                                page = null;
                            }
                        } while (page != null);
                    }
                    break;
                case CURRENT:
                    bookings = bookingRepository.findBookingsByItem_OwnerAndStartBeforeAndEndAfterOrderByStartDesc(
                            user,
                            LocalDateTime.now(),
                            LocalDateTime.now());
                    break;
                case PAST:
                    bookings = bookingRepository.findBookingsByItem_OwnerAndEndBeforeOrderByStartDesc(
                            user,
                            LocalDateTime.now());
                    break;
                case FUTURE:
                    bookings = bookingRepository.findBookingsByItem_OwnerAndStartAfterOrderByStartDesc(
                            user,
                            LocalDateTime.now());
                    break;
                case WAITING:
                    bookings = bookingRepository.findBookingsByItem_OwnerAndStatusOrderByStartDesc(user, Status.WAITING);
                    break;
                case REJECTED:
                    bookings = bookingRepository.findBookingsByItem_OwnerAndStatusOrderByStartDesc(user, Status.REJECTED);
                    break;
            }
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
        }
        return bookings;
    }

    private void checkUser(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NoSuchElementException("Передан некорректный id пользователя");
        }
    }
}
