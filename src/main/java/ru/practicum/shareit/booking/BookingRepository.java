package ru.practicum.shareit.booking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    Page<Booking> findBookingsByBookerAndStartBeforeAndEndAfterOrderByStartDesc(User user,
                                                                                LocalDateTime start,
                                                                                LocalDateTime end,
                                                                                Pageable page);

    Page<Booking> findBookingsByBookerAndEndBeforeOrderByStartDesc(User user,
                                                                   LocalDateTime localDateTime,
                                                                   Pageable page);

    Page<Booking> findBookingsByBookerAndStartAfterOrderByStartDesc(User user,
                                                                    LocalDateTime localDateTime,
                                                                    Pageable page);

    Page<Booking> findBookingsByBookerAndStatusOrderByStartDesc(User user, Status status, Pageable page);

    Page<Booking> findBookingsByBookerAndStatusOrStatusOrderByStartDesc(User user,
                                                                        Status statusFirst,
                                                                        Status statusSecond,
                                                                        Pageable page);

    Page<Booking> findBookingsByItem_OwnerOrderByStartDesc(User user, Pageable page);

    Page<Booking> findBookingsByItem_OwnerAndStartBeforeAndEndAfterOrderByStartDesc(User user,
                                                                                    LocalDateTime start,
                                                                                    LocalDateTime end,
                                                                                    Pageable page);

    Page<Booking> findBookingsByItem_OwnerAndEndBeforeOrderByStartDesc(User user,
                                                                       LocalDateTime localDateTime,
                                                                       Pageable page);

    Page<Booking> findBookingsByItem_OwnerAndStartAfterOrderByStartDesc(User user,
                                                                        LocalDateTime localDateTime,
                                                                        Pageable page);

    Page<Booking> findBookingsByItem_OwnerAndStatusOrderByStartDesc(User user, Status status, Pageable page);

    List<Booking> findBookingsByItemOrderByStart(Item item);

    Booking findBookingByBookerAndItemAndEndBefore(User user, Item item, LocalDateTime localDateTime);
}
