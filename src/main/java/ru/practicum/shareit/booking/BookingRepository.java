package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findBookingsByBookerOrderByStartDesc(User user);

    List<Booking> findBookingsByBookerAndStartBeforeAndEndAfterOrderByStartDesc(User user, LocalDateTime start, LocalDateTime end);

    List<Booking> findBookingsByBookerAndEndBeforeOrderByStartDesc(User user, LocalDateTime localDateTime);

    List<Booking> findBookingsByBookerAndStartAfterOrderByStartDesc(User user, LocalDateTime localDateTime);

    List<Booking> findBookingsByBookerAndStatusOrderByStartDesc(User user, Status status);

    List<Booking> findBookingsByItem_OwnerOrderByStartDesc(User user);

    List<Booking> findBookingsByItem_OwnerAndStartBeforeAndEndAfterOrderByStartDesc(User user, LocalDateTime start, LocalDateTime end);

    List<Booking> findBookingsByItem_OwnerAndEndBeforeOrderByStartDesc(User user, LocalDateTime localDateTime);

    List<Booking> findBookingsByItem_OwnerAndStartAfterOrderByStartDesc(User user, LocalDateTime localDateTime);

    List<Booking> findBookingsByItem_OwnerAndStatusOrderByStartDesc(User user, Status status);

    List<Booking> findBookingsByItemOrderByStart(Item item);

    Booking findBookingByBookerAndItemAndEndBefore(User user, Item item, LocalDateTime localDateTime);
}
