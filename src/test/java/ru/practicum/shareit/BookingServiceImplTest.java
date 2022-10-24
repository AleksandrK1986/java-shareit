package ru.practicum.shareit;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.BookingServiceImpl;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import javax.validation.ValidationException;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceImplTest {

    @Mock
    BookingRepository bookingRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    ItemRepository itemRepository;

    private final EntityManager em;

    private final ItemService itemService;

    private final UserService userService;

    private final BookingService bookingService;

    User userForList;
    User userForList2;
    Item itemForList;
    Booking bookingForList;
    Page<Booking> bookings;

    @BeforeEach
    void createTestListBookings() {
        userForList = new User(1, "name", "email@mail.ru");
        userForList2 = new User(2, "2name", "2email@mail.ru");
        itemForList = new Item(
                1,
                "name",
                "description",
                true,
                userForList,
                null, null, null, null, null);
        bookingForList = new Booking(
                1,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                itemForList,
                userForList2,
                Status.WAITING);
        bookings = new Page<Booking>() {
            @Override
            public int getTotalPages() {
                return 0;
            }

            @Override
            public long getTotalElements() {
                return 0;
            }

            @Override
            public <U> Page<U> map(Function<? super Booking, ? extends U> converter) {
                return null;
            }

            @Override
            public int getNumber() {
                return 0;
            }

            @Override
            public int getSize() {
                return 0;
            }

            @Override
            public int getNumberOfElements() {
                return 0;
            }

            @Override
            public List<Booking> getContent() {
                List<Booking> bookingList = new ArrayList<>();
                bookingList.add(bookingForList);
                return bookingList;
            }

            @Override
            public boolean hasContent() {
                return false;
            }

            @Override
            public Sort getSort() {
                return null;
            }

            @Override
            public boolean isFirst() {
                return false;
            }

            @Override
            public boolean isLast() {
                return false;
            }

            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public boolean hasPrevious() {
                return false;
            }

            @Override
            public Pageable nextPageable() {
                return null;
            }

            @Override
            public Pageable previousPageable() {
                return null;
            }

            @Override
            public Iterator<Booking> iterator() {
                return null;
            }
        };
    }

    @Test
    void testCreateBookingWrongUser() {
        BookingService bookingService = new BookingServiceImpl(
                bookingRepository,
                userRepository,
                itemRepository);
        Booking booking = new Booking(
                1,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(1),
                null,
                null,
                null
        );
        Mockito
                .when(userRepository.existsById(Mockito.anyLong()))
                .thenReturn(false);
        Throwable thrown = assertThrows(RuntimeException.class, () -> {
            bookingService.create(booking, 1);
        });
        assertNotNull(thrown.getMessage());
    }

    @Test
    void testCreateBookingWrongItem() {
        BookingService bookingService = new BookingServiceImpl(
                bookingRepository,
                userRepository,
                itemRepository);
        Item item = new Item(
                1,
                "name",
                "description",
                true,
                null, null, null, null, null, null);
        Booking booking = new Booking(
                1,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(1),
                item,
                null,
                null
        );
        Mockito
                .when(userRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);
        Mockito
                .when(itemRepository.existsById(Mockito.anyLong()))
                .thenReturn(false);
        Throwable thrown = assertThrows(NoSuchElementException.class, () -> {
            bookingService.create(booking, 1);
        });
        assertNotNull(thrown.getMessage());
    }

    @Test
    void testCreateBookingWrongItemAvailable() {
        BookingService bookingService = new BookingServiceImpl(
                bookingRepository,
                userRepository,
                itemRepository);
        Item item = new Item(
                1,
                "name",
                "description",
                false,
                null, null, null, null, null, null);
        Booking booking = new Booking(
                1,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(1),
                item,
                null,
                null
        );
        Mockito
                .when(userRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);
        Mockito
                .when(itemRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);
        Mockito
                .when(itemRepository.getReferenceById(Mockito.anyLong()))
                .thenReturn(item);
        Throwable thrown = assertThrows(ValidationException.class, () -> {
            bookingService.create(booking, 1);
        });
        assertNotNull(thrown.getMessage());
    }

    @Test
    void testCreateBookingWrongStart() {
        BookingService bookingService = new BookingServiceImpl(
                bookingRepository,
                userRepository,
                itemRepository);
        Item item = new Item(
                1,
                "name",
                "description",
                true,
                null, null, null, null, null, null);
        Booking booking = new Booking(
                1,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(1),
                item,
                null,
                null
        );
        Mockito
                .when(userRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);
        Mockito
                .when(itemRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);
        Mockito
                .when(itemRepository.getReferenceById(Mockito.anyLong()))
                .thenReturn(item);
        Throwable thrown = assertThrows(ValidationException.class, () -> {
            bookingService.create(booking, 1);
        });
        Assertions.assertEquals("Передано не корректное время бронирования", thrown.getMessage());
    }

    @Test
    void testCreateBookingWithUserIsOwner() {
        BookingService bookingService = new BookingServiceImpl(
                bookingRepository,
                userRepository,
                itemRepository);
        User user = new User(1, "name", "email@mail.ru");
        Item item = new Item(
                1,
                "name",
                "description",
                true,
                user,
                null, null, null, null, null);
        Booking booking = new Booking(
                1,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                item,
                null,
                null
        );
        Mockito
                .when(userRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);
        Mockito
                .when(itemRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);
        Mockito
                .when(itemRepository.getReferenceById(Mockito.anyLong()))
                .thenReturn(item);
        Throwable thrown = assertThrows(NoSuchElementException.class, () -> {
            bookingService.create(booking, 1);
        });
        Assertions.assertEquals("Вледалец вещи не межет бронировать вещь", thrown.getMessage());
    }

    @Test
    void testCreateBooking() {
        BookingService bookingService = new BookingServiceImpl(
                bookingRepository,
                userRepository,
                itemRepository);
        User user = new User(1, "name", "email@mail.ru");
        User user2 = new User(2, "2name", "2email@mail.ru");
        Item item = new Item(
                1,
                "name",
                "description",
                true,
                user,
                null, null, null, null, null);
        Booking booking = new Booking(
                1,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                item,
                null,
                null
        );
        Mockito
                .when(userRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);
        Mockito
                .when(itemRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);
        Mockito
                .when(itemRepository.getReferenceById(Mockito.anyLong()))
                .thenReturn(item);
        Mockito
                .when(userRepository.getReferenceById(Mockito.anyLong()))
                .thenReturn(user2);
        Mockito
                .when(bookingRepository.save(Mockito.any()))
                .thenReturn(booking);
        Assertions.assertEquals(1, bookingService.create(booking, 2).getId());
    }

    @Test
    void testApproveWrongStatus() {
        BookingService bookingService = new BookingServiceImpl(
                bookingRepository,
                userRepository,
                itemRepository);
        User user = new User(1, "name", "email@mail.ru");
        User user2 = new User(2, "2name", "2email@mail.ru");
        Item item = new Item(
                1,
                "name",
                "description",
                true,
                user,
                null, null, null, null, null);
        Booking booking = new Booking(
                1,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                item,
                null,
                Status.APPROVED
        );
        Mockito
                .when(itemRepository.getReferenceById(Mockito.anyLong()))
                .thenReturn(item);
        Mockito
                .when(userRepository.getReferenceById(Mockito.anyLong()))
                .thenReturn(user);
        Mockito
                .when(bookingRepository.getReferenceById(Mockito.anyLong()))
                .thenReturn(booking);
        Throwable thrown = assertThrows(ValidationException.class, () -> {
            bookingService.approved(1, true, 1);
        });
        Assertions.assertEquals("Бронирование уже подтвержденено", thrown.getMessage());
    }

    @Test
    void testApproveUserNotOwner() {
        BookingService bookingService = new BookingServiceImpl(
                bookingRepository,
                userRepository,
                itemRepository);
        User user = new User(1, "name", "email@mail.ru");
        User user2 = new User(2, "2name", "2email@mail.ru");
        Item item = new Item(
                1,
                "name",
                "description",
                true,
                user2,
                null, null, null, null, null);
        Booking booking = new Booking(
                1,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                item,
                null,
                Status.WAITING
        );
        Mockito
                .when(itemRepository.getReferenceById(Mockito.anyLong()))
                .thenReturn(item);
        Mockito
                .when(userRepository.getReferenceById(Mockito.anyLong()))
                .thenReturn(user);
        Mockito
                .when(bookingRepository.getReferenceById(Mockito.anyLong()))
                .thenReturn(booking);
        Throwable thrown = assertThrows(NoSuchElementException.class, () -> {
            bookingService.approved(1, true, 1);
        });
        Assertions.assertEquals("Подтверждать бронирование может только владелец вещи", thrown.getMessage());
    }

    @Test
    void testApprove() {
        BookingService bookingService = new BookingServiceImpl(
                bookingRepository,
                userRepository,
                itemRepository);
        User user = new User(1, "name", "email@mail.ru");
        User user2 = new User(2, "2name", "2email@mail.ru");
        Item item = new Item(
                1,
                "name",
                "description",
                true,
                user,
                null, null, null, null, null);
        Booking booking = new Booking(
                1,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                item,
                null,
                Status.WAITING
        );
        Mockito
                .when(itemRepository.getReferenceById(Mockito.anyLong()))
                .thenReturn(item);
        Mockito
                .when(userRepository.getReferenceById(Mockito.anyLong()))
                .thenReturn(user);
        Mockito
                .when(bookingRepository.getReferenceById(Mockito.anyLong()))
                .thenReturn(booking);
        Mockito
                .when(bookingRepository.save(Mockito.any()))
                .thenReturn(booking);
        Assertions.assertEquals(Status.APPROVED, bookingService.approved(1, true, 1).getStatus());
    }

    @Test
    void testApproveRejected() {
        BookingService bookingService = new BookingServiceImpl(
                bookingRepository,
                userRepository,
                itemRepository);
        User user = new User(1, "name", "email@mail.ru");
        User user2 = new User(2, "2name", "2email@mail.ru");
        Item item = new Item(
                1,
                "name",
                "description",
                true,
                user,
                null, null, null, null, null);
        Booking booking = new Booking(
                1,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                item,
                null,
                Status.WAITING
        );
        Mockito
                .when(itemRepository.getReferenceById(Mockito.anyLong()))
                .thenReturn(item);
        Mockito
                .when(userRepository.getReferenceById(Mockito.anyLong()))
                .thenReturn(user);
        Mockito
                .when(bookingRepository.getReferenceById(Mockito.anyLong()))
                .thenReturn(booking);
        Mockito
                .when(bookingRepository.save(Mockito.any()))
                .thenReturn(booking);
        Assertions.assertEquals(Status.REJECTED, bookingService.approved(1, false, 1).getStatus());
    }

    @Test
    void testGetByIdBookingNotFoundBooking() {
        BookingService bookingService = new BookingServiceImpl(
                bookingRepository,
                userRepository,
                itemRepository);
        User user = new User(1, "name", "email@mail.ru");
        Mockito
                .when(userRepository.getReferenceById(Mockito.anyLong()))
                .thenReturn(user);
        Mockito
                .when(bookingRepository.existsById(Mockito.anyLong()))
                .thenReturn(false);
        Throwable thrown = assertThrows(NoSuchElementException.class, () -> {
            bookingService.getById(1, 1);
        });
        Assertions.assertEquals("Бронирование не найдено", thrown.getMessage());
    }

    @Test
    void testGetByIdBookingWrongOwner() {
        BookingService bookingService = new BookingServiceImpl(
                bookingRepository,
                userRepository,
                itemRepository);
        User user = new User(1, "name", "email@mail.ru");
        User user2 = new User(2, "2name", "2email@mail.ru");
        User user3 = new User(3, "2name", "2email@mail.ru");
        Item item = new Item(
                1,
                "name",
                "description",
                true,
                user,
                null, null, null, null, null);
        Booking booking = new Booking(
                1,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                item,
                user2,
                Status.WAITING
        );
        Mockito
                .when(bookingRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);
        Mockito
                .when(userRepository.getReferenceById(Mockito.anyLong()))
                .thenReturn(user3);
        Mockito
                .when(bookingRepository.getReferenceById(Mockito.anyLong()))
                .thenReturn(booking);
        Throwable thrown = assertThrows(NoSuchElementException.class, () -> {
            bookingService.getById(1, 3);
        });
        Assertions.assertEquals("Получить бронирование может или владелец вещи, или автор бронирования", thrown.getMessage());
    }

    @Test
    void testGetByIdBooking() {
        BookingService bookingService = new BookingServiceImpl(
                bookingRepository,
                userRepository,
                itemRepository);
        User user = new User(1, "name", "email@mail.ru");
        User user2 = new User(2, "2name", "2email@mail.ru");
        Item item = new Item(
                1,
                "name",
                "description",
                true,
                user,
                null, null, null, null, null);
        Booking booking = new Booking(
                1,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                item,
                user2,
                Status.WAITING
        );
        Mockito
                .when(bookingRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);
        Mockito
                .when(userRepository.getReferenceById(Mockito.anyLong()))
                .thenReturn(user);
        Mockito
                .when(bookingRepository.getReferenceById(Mockito.anyLong()))
                .thenReturn(booking);
        Assertions.assertEquals(1, bookingService.getById(1, 1).getId());
    }

    @Test
    void testGetAllUserBookingsWrongUser() {
        BookingService bookingService = new BookingServiceImpl(
                bookingRepository,
                userRepository,
                itemRepository);
        Mockito
                .when(userRepository.existsById(Mockito.anyLong()))
                .thenReturn(false);
        Throwable thrown = assertThrows(NoSuchElementException.class, () -> {
            bookingService.getAllUserBookings("ALL", 1, null, null);
        });
        Assertions.assertEquals("Передан некорректный id пользователя", thrown.getMessage());
    }

    @Test
    void testGetAllUserBookingsWrongState() {
        BookingService bookingService = new BookingServiceImpl(
                bookingRepository,
                userRepository,
                itemRepository);
        User user = new User(1, "name", "email@mail.ru");
        Mockito
                .when(userRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);
        Mockito
                .when(userRepository.getReferenceById(Mockito.anyLong()))
                .thenReturn(user);
        Throwable thrown = assertThrows(ValidationException.class, () -> {
            bookingService.getAllUserBookings("ALLasdasda", 1, null, null);
        });
        Assertions.assertEquals("Unknown state: UNSUPPORTED_STATUS", thrown.getMessage());
    }

    @Test
    void testGetAllUserBookingsStateIsAllWithoutPagination() {
        BookingService bookingService = new BookingServiceImpl(
                bookingRepository,
                userRepository,
                itemRepository);

        Mockito
                .when(userRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);
        Mockito
                .when(userRepository.getReferenceById(Mockito.anyLong()))
                .thenReturn(userForList);
        Mockito
                .when(bookingRepository.findBookingsByBookerAndStatusOrStatusOrderByStartDesc(
                        Mockito.any(),
                        Mockito.any(),
                        Mockito.any(),
                        Mockito.any()))
                .thenReturn(bookings);
        Assertions.assertEquals(1, bookingService.getAllUserBookings(
                "ALL",
                1,
                null,
                null).size());
    }

    @Test
    void testGetAllUserBookingsStateIsAllWithPagination() {
        BookingService bookingService = new BookingServiceImpl(
                bookingRepository,
                userRepository,
                itemRepository);
        Mockito
                .when(userRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);
        Mockito
                .when(userRepository.getReferenceById(Mockito.anyLong()))
                .thenReturn(userForList);
        Mockito
                .when(bookingRepository.findBookingsByBookerAndStatusOrStatusOrderByStartDesc(
                        Mockito.any(),
                        Mockito.any(),
                        Mockito.any(),
                        Mockito.any(Pageable.class)))
                .thenReturn(bookings);
        Assertions.assertEquals(1, bookingService.getAllUserBookings(
                "ALL",
                1,
                1,
                1).size());
    }

    @Test
    void testGetAllUserBookingsStateIsCurrent() {
        BookingService bookingService = new BookingServiceImpl(
                bookingRepository,
                userRepository,
                itemRepository);
        Mockito
                .when(userRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);
        Mockito
                .when(userRepository.getReferenceById(Mockito.anyLong()))
                .thenReturn(userForList);
        Mockito
                .when(bookingRepository.findBookingsByBookerAndStartBeforeAndEndAfterOrderByStartDesc(
                        Mockito.any(),
                        Mockito.any(),
                        Mockito.any(),
                        Mockito.any()))
                .thenReturn(bookings);
        Assertions.assertEquals(1, bookingService.getAllUserBookings(
                "CURRENT",
                1,
                null,
                null).size());
    }

    @Test
    void testGetAllUserBookingsStateIsPast() {
        BookingService bookingService = new BookingServiceImpl(
                bookingRepository,
                userRepository,
                itemRepository);
        Mockito
                .when(userRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);
        Mockito
                .when(userRepository.getReferenceById(Mockito.anyLong()))
                .thenReturn(userForList);
        Mockito
                .when(bookingRepository.findBookingsByBookerAndEndBeforeOrderByStartDesc(
                        Mockito.any(),
                        Mockito.any(),
                        Mockito.any()))
                .thenReturn(bookings);
        Assertions.assertEquals(1, bookingService.getAllUserBookings(
                "PAST",
                1,
                null,
                null).size());
    }

    @Test
    void testGetAllUserBookingsStateIsFuture() {
        BookingService bookingService = new BookingServiceImpl(
                bookingRepository,
                userRepository,
                itemRepository);
        Mockito
                .when(userRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);
        Mockito
                .when(userRepository.getReferenceById(Mockito.anyLong()))
                .thenReturn(userForList);
        Mockito
                .when(bookingRepository.findBookingsByBookerAndStartAfterOrderByStartDesc(
                        Mockito.any(),
                        Mockito.any(),
                        Mockito.any()))
                .thenReturn(bookings);
        Assertions.assertEquals(1, bookingService.getAllUserBookings(
                "FUTURE",
                1,
                null,
                null).size());
    }

    @Test
    void testGetAllUserBookingsStateIsWaiting() {
        BookingService bookingService = new BookingServiceImpl(
                bookingRepository,
                userRepository,
                itemRepository);
        User user = new User(1, "name", "email@mail.ru");
        User user2 = new User(2, "2name", "2email@mail.ru");
        Item item = new Item(
                1,
                "name",
                "description",
                true,
                user,
                null, null, null, null, null);
        Booking booking = new Booking(
                1,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                item,
                user2,
                Status.WAITING);
        Page<Booking> bookings = new Page<Booking>() {
            @Override
            public int getTotalPages() {
                return 0;
            }

            @Override
            public long getTotalElements() {
                return 0;
            }

            @Override
            public <U> Page<U> map(Function<? super Booking, ? extends U> converter) {
                return null;
            }

            @Override
            public int getNumber() {
                return 0;
            }

            @Override
            public int getSize() {
                return 0;
            }

            @Override
            public int getNumberOfElements() {
                return 0;
            }

            @Override
            public List<Booking> getContent() {
                List<Booking> bookingList = new ArrayList<>();
                bookingList.add(booking);
                return bookingList;
            }

            @Override
            public boolean hasContent() {
                return false;
            }

            @Override
            public Sort getSort() {
                return null;
            }

            @Override
            public boolean isFirst() {
                return false;
            }

            @Override
            public boolean isLast() {
                return false;
            }

            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public boolean hasPrevious() {
                return false;
            }

            @Override
            public Pageable nextPageable() {
                return null;
            }

            @Override
            public Pageable previousPageable() {
                return null;
            }

            @Override
            public Iterator<Booking> iterator() {
                return null;
            }
        };
        Mockito
                .when(userRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);
        Mockito
                .when(userRepository.getReferenceById(Mockito.anyLong()))
                .thenReturn(user);
        Mockito
                .when(bookingRepository.findBookingsByBookerAndStatusOrderByStartDesc(
                        Mockito.any(),
                        Mockito.any(),
                        Mockito.any()))
                .thenReturn(bookings);
        Assertions.assertEquals(1, bookingService.getAllUserBookings(
                "WAITING",
                1,
                null,
                null).size());
    }

    @Test
    void testGetAllUserBookingsStateIsRejected() {
        BookingService bookingService = new BookingServiceImpl(
                bookingRepository,
                userRepository,
                itemRepository);
        User user = new User(1, "name", "email@mail.ru");
        User user2 = new User(2, "2name", "2email@mail.ru");
        Item item = new Item(
                1,
                "name",
                "description",
                true,
                user,
                null, null, null, null, null);
        Booking booking = new Booking(
                1,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                item,
                user2,
                Status.WAITING);
        Page<Booking> bookings = new Page<Booking>() {
            @Override
            public int getTotalPages() {
                return 0;
            }

            @Override
            public long getTotalElements() {
                return 0;
            }

            @Override
            public <U> Page<U> map(Function<? super Booking, ? extends U> converter) {
                return null;
            }

            @Override
            public int getNumber() {
                return 0;
            }

            @Override
            public int getSize() {
                return 0;
            }

            @Override
            public int getNumberOfElements() {
                return 0;
            }

            @Override
            public List<Booking> getContent() {
                List<Booking> bookingList = new ArrayList<>();
                bookingList.add(booking);
                return bookingList;
            }

            @Override
            public boolean hasContent() {
                return false;
            }

            @Override
            public Sort getSort() {
                return null;
            }

            @Override
            public boolean isFirst() {
                return false;
            }

            @Override
            public boolean isLast() {
                return false;
            }

            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public boolean hasPrevious() {
                return false;
            }

            @Override
            public Pageable nextPageable() {
                return null;
            }

            @Override
            public Pageable previousPageable() {
                return null;
            }

            @Override
            public Iterator<Booking> iterator() {
                return null;
            }
        };
        Mockito
                .when(userRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);
        Mockito
                .when(userRepository.getReferenceById(Mockito.anyLong()))
                .thenReturn(user);
        Mockito
                .when(bookingRepository.findBookingsByBookerAndStatusOrderByStartDesc(
                        Mockito.any(),
                        Mockito.any(),
                        Mockito.any()))
                .thenReturn(bookings);
        Assertions.assertEquals(1, bookingService.getAllUserBookings(
                "REJECTED",
                1,
                null,
                null).size());
    }

    @Test
    void testGetAllUserItemsBookingsWrongUser() {
        BookingService bookingService = new BookingServiceImpl(
                bookingRepository,
                userRepository,
                itemRepository);
        Mockito
                .when(userRepository.existsById(Mockito.anyLong()))
                .thenReturn(false);
        Throwable thrown = assertThrows(NoSuchElementException.class, () -> {
            bookingService.getAllUserItemsBookings("ALL", 1, null, null);
        });
        Assertions.assertEquals("Передан некорректный id пользователя", thrown.getMessage());
    }

    @Test
    void testGetAllUserItemsBookingsWrongState() {
        BookingService bookingService = new BookingServiceImpl(
                bookingRepository,
                userRepository,
                itemRepository);
        User user = new User(1, "name", "email@mail.ru");
        Mockito
                .when(userRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);
        Mockito
                .when(userRepository.getReferenceById(Mockito.anyLong()))
                .thenReturn(user);
        Throwable thrown = assertThrows(ValidationException.class, () -> {
            bookingService.getAllUserItemsBookings("ALLasdasda", 1, null, null);
        });
        Assertions.assertEquals("Unknown state: UNSUPPORTED_STATUS", thrown.getMessage());
    }

    @Test
    void testGetAllUserItemsBookingsStateIsAllWithoutPagination() {
        BookingService bookingService = new BookingServiceImpl(
                bookingRepository,
                userRepository,
                itemRepository);
        Mockito
                .when(userRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);
        Mockito
                .when(userRepository.getReferenceById(Mockito.anyLong()))
                .thenReturn(userForList);
        Mockito
                .when(bookingRepository.findBookingsByItem_OwnerOrderByStartDesc(
                        Mockito.any(),
                        Mockito.any()))
                .thenReturn(bookings);
        Assertions.assertEquals(1, bookingService.getAllUserItemsBookings(
                "ALL",
                1,
                null,
                null).size());
    }

    @Test
    void testGetAllUserItemsBookingsStateIsAllWithPagination() {
        BookingService bookingService = new BookingServiceImpl(
                bookingRepository,
                userRepository,
                itemRepository);
        Mockito
                .when(userRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);
        Mockito
                .when(userRepository.getReferenceById(Mockito.anyLong()))
                .thenReturn(userForList);
        Mockito
                .when(bookingRepository.findBookingsByItem_OwnerOrderByStartDesc(
                        Mockito.any(),
                        Mockito.any(Pageable.class)))
                .thenReturn(bookings);
        Assertions.assertEquals(1, bookingService.getAllUserItemsBookings(
                "ALL",
                1,
                1,
                1).size());
    }

    @Test
    void testGetAllUserItemsBookingsStateIsCurrent() {
        BookingService bookingService = new BookingServiceImpl(
                bookingRepository,
                userRepository,
                itemRepository);
        Mockito
                .when(userRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);
        Mockito
                .when(userRepository.getReferenceById(Mockito.anyLong()))
                .thenReturn(userForList);
        Mockito
                .when(bookingRepository.findBookingsByItem_OwnerAndStartBeforeAndEndAfterOrderByStartDesc(
                        Mockito.any(),
                        Mockito.any(),
                        Mockito.any(),
                        Mockito.any()))
                .thenReturn(bookings);
        Assertions.assertEquals(1, bookingService.getAllUserItemsBookings(
                "CURRENT",
                1,
                null,
                null).size());
    }

    @Test
    void testGetAllUserItemsBookingsStateIsPast() {
        BookingService bookingService = new BookingServiceImpl(
                bookingRepository,
                userRepository,
                itemRepository);
        Mockito
                .when(userRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);
        Mockito
                .when(userRepository.getReferenceById(Mockito.anyLong()))
                .thenReturn(userForList);
        Mockito
                .when(bookingRepository.findBookingsByItem_OwnerAndEndBeforeOrderByStartDesc(
                        Mockito.any(),
                        Mockito.any(),
                        Mockito.any()))
                .thenReturn(bookings);
        Assertions.assertEquals(1, bookingService.getAllUserItemsBookings(
                "PAST",
                1,
                null,
                null).size());
    }

    @Test
    void testGetAllUserItemsBookingsStateIsFuture() {
        BookingService bookingService = new BookingServiceImpl(
                bookingRepository,
                userRepository,
                itemRepository);
        Mockito
                .when(userRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);
        Mockito
                .when(userRepository.getReferenceById(Mockito.anyLong()))
                .thenReturn(userForList);
        Mockito
                .when(bookingRepository.findBookingsByItem_OwnerAndStartAfterOrderByStartDesc(
                        Mockito.any(),
                        Mockito.any(),
                        Mockito.any()))
                .thenReturn(bookings);
        Assertions.assertEquals(1, bookingService.getAllUserItemsBookings(
                "FUTURE",
                1,
                null,
                null).size());
    }

    @Test
    void testGetAllUserItemsBookingsStateIsWaiting() {
        BookingService bookingService = new BookingServiceImpl(
                bookingRepository,
                userRepository,
                itemRepository);
        Mockito
                .when(userRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);
        Mockito
                .when(userRepository.getReferenceById(Mockito.anyLong()))
                .thenReturn(userForList);
        Mockito
                .when(bookingRepository.findBookingsByItem_OwnerAndStatusOrderByStartDesc(
                        Mockito.any(),
                        Mockito.any(),
                        Mockito.any()))
                .thenReturn(bookings);
        Assertions.assertEquals(1, bookingService.getAllUserItemsBookings(
                "WAITING",
                1,
                null,
                null).size());
    }

    @Test
    void testGetAllUserItemsBookingsStateIsRejected() {
        BookingService bookingService = new BookingServiceImpl(
                bookingRepository,
                userRepository,
                itemRepository);
        Mockito
                .when(userRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);
        Mockito
                .when(userRepository.getReferenceById(Mockito.anyLong()))
                .thenReturn(userForList);
        Mockito
                .when(bookingRepository.findBookingsByItem_OwnerAndStatusOrderByStartDesc(
                        Mockito.any(),
                        Mockito.any(),
                        Mockito.any()))
                .thenReturn(bookings);
        Assertions.assertEquals(1, bookingService.getAllUserItemsBookings(
                "REJECTED",
                1,
                null,
                null).size());
    }

    @Test
    void testGetAllBookingByUserFromBD() {
        User itemOwner = new User(
                1,
                "itemOwner",
                "email@mail.ru"
        );
        User itemBooker = new User(
                2,
                "itemBooker",
                "email2@mail.ru"
        );
        userService.create(itemOwner);
        userService.create(itemBooker);
        Item item = new Item(
                1,
                "itemName",
                "itemDescription",
                true,
                null,
                null,
                null,
                null,
                null,
                null
        );
        itemService.create(item, 1);
        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.owner.id = :id", Item.class);
        Item itemFromBD = query
                .setParameter("id", itemOwner.getId())
                .getSingleResult();
        assertThat(itemFromBD.getId(), notNullValue());
        assertThat(itemFromBD.getOwner().getName(), equalTo(itemOwner.getName()));
        Booking booking = new Booking(
                1,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                itemService.findById(itemFromBD.getId(), userService.findById(itemOwner.getId()).getId()),
                userService.findById(itemBooker.getId()),
                Status.WAITING
        );
        bookingService.create(booking, userService.findById(itemBooker.getId()).getId());
        List<Booking> bookingList = bookingService.getAllUserBookings(
                "ALL",
                userService.findById(itemOwner.getId()).getId(),
                null,
                null);
        assertThat(bookingList.size(), equalTo(1));
        assertThat(bookingList.get(0), equalTo(bookingService.getById(
                1,
                userService.findById(itemOwner.getId()).getId())));
    }
}
