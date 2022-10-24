package ru.practicum.shareit;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.CommentRepository;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.ItemServiceImpl;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.requests.ItemRequest;
import ru.practicum.shareit.requests.ItemRequestRepository;
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
public class ItemServiceImplTest {
    @Mock
    ItemRepository itemRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    BookingRepository bookingRepository;
    @Mock
    CommentRepository commentRepository;
    @Mock
    ItemRequestRepository itemRequestRepository;

    private final EntityManager em;
    private final ItemService itemService;
    private final UserService userService;

    @Test
    void testCreateWithNoFoundUser() {
        ItemService itemService = new ItemServiceImpl(
                itemRepository,
                userRepository,
                bookingRepository,
                commentRepository,
                itemRequestRepository);
        Item item = new Item();
        Throwable thrown = assertThrows(NoSuchElementException.class, () -> {
            itemService.create(item, 1);
        });
        assertNotNull(thrown.getMessage());
    }

    @Test
    void testCreateWithBlankName() {
        ItemService itemService = new ItemServiceImpl(
                itemRepository,
                userRepository,
                bookingRepository,
                commentRepository,
                itemRequestRepository);
        Item item = new Item(
                1,
                "",
                "description",
                true,
                null, null, null, null, null, null);
        User user = new User(1, "name", "email@mail.ru");
        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(userRepository.getReferenceById(Mockito.anyLong()))
                .thenReturn(user);
        Throwable thrown = assertThrows(ValidationException.class, () -> {
            itemService.create(item, 1);
        });
        assertNotNull(thrown.getMessage());
    }

    @Test
    void testCreateWithNullAvailable() {
        ItemService itemService = new ItemServiceImpl(
                itemRepository,
                userRepository,
                bookingRepository,
                commentRepository,
                itemRequestRepository);
        Item item = new Item(
                1,
                "name",
                "description",
                null,
                null, null, null, null, null, null);
        User user = new User(1, "name", "email@mail.ru");
        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(userRepository.getReferenceById(Mockito.anyLong()))
                .thenReturn(user);
        Throwable thrown = assertThrows(ValidationException.class, () -> {
            itemService.create(item, 1);
        });
        assertNotNull(thrown.getMessage());
    }

    @Test
    void testCreateWithNullDescription() {
        ItemService itemService = new ItemServiceImpl(
                itemRepository,
                userRepository,
                bookingRepository,
                commentRepository,
                itemRequestRepository);
        Item item = new Item(
                1,
                "name",
                null,
                true,
                null, null, null, null, null, null);
        User user = new User(1, "name", "email@mail.ru");
        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(userRepository.getReferenceById(Mockito.anyLong()))
                .thenReturn(user);
        Throwable thrown = assertThrows(ValidationException.class, () -> {
            itemService.create(item, 1);
        });
        assertNotNull(thrown.getMessage());
    }

    @Test
    void testCreate() {
        ItemService itemService = new ItemServiceImpl(
                itemRepository,
                userRepository,
                bookingRepository,
                commentRepository,
                itemRequestRepository);
        Item item = new Item(
                1,
                "name",
                "description",
                true,
                null, null, null, null, null, 1L);
        User user = new User(1, "name", "email@mail.ru");
        ItemRequest itemRequest = new ItemRequest();
        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(userRepository.getReferenceById(Mockito.anyLong()))
                .thenReturn(user);
        Mockito
                .when(itemRequestRepository.getReferenceById(Mockito.anyLong()))
                .thenReturn(itemRequest);
        Mockito
                .when(itemRepository.save(Mockito.any()))
                .thenReturn(item);
        Assertions.assertEquals(1, itemService.create(item, 1).getId());
    }

    @Test
    void testCreateCommentWithNullItem() {
        ItemService itemService = new ItemServiceImpl(
                itemRepository,
                userRepository,
                bookingRepository,
                commentRepository,
                itemRequestRepository);
        Comment comment = new Comment();
        Throwable thrown = assertThrows(NoSuchElementException.class, () -> {
            itemService.createComment(comment, 1, 1);
        });
        assertNotNull(thrown.getMessage());
    }

    @Test
    void testCreateCommentWithWrongUser() {
        ItemService itemService = new ItemServiceImpl(
                itemRepository,
                userRepository,
                bookingRepository,
                commentRepository,
                itemRequestRepository);
        Comment comment = new Comment();
        Item item = new Item(
                1,
                "name",
                "description",
                true,
                null, null, null, null, null, null);
        Mockito
                .when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(item));
        Throwable thrown = assertThrows(NoSuchElementException.class, () -> {
            itemService.createComment(comment, 1, 1);
        });
        assertNotNull(thrown.getMessage());
    }

    @Test
    void testCreateCommentWithBlankText() {
        ItemService itemService = new ItemServiceImpl(
                itemRepository,
                userRepository,
                bookingRepository,
                commentRepository,
                itemRequestRepository);
        Item item = new Item(
                1,
                "name",
                "description",
                true,
                null, null, null, null, null, null);
        User user = new User(1, "name", "email@mail.ru");
        Comment comment = new Comment(1,
                "",
                item,
                user,
                LocalDateTime.now());
        Mockito
                .when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(item));
        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(bookingRepository.findBookingByBookerAndItemAndEndBefore(
                        Mockito.any(),
                        Mockito.any(),
                        Mockito.any()))
                .thenReturn(null);
        Throwable thrown = assertThrows(ValidationException.class, () -> {
            itemService.createComment(comment, 1, 1);
        });
        assertNotNull(thrown.getMessage());
    }

    @Test
    void testCreateCommentWithWrongBooking() {
        ItemService itemService = new ItemServiceImpl(
                itemRepository,
                userRepository,
                bookingRepository,
                commentRepository,
                itemRequestRepository);
        Item item = new Item(
                1,
                "name",
                "description",
                true,
                null, null, null, null, null, null);
        User user = new User(1, "name", "email@mail.ru");
        Comment comment = new Comment(1,
                "text",
                item,
                user,
                LocalDateTime.now());
        Mockito
                .when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(item));
        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(bookingRepository.findBookingByBookerAndItemAndEndBefore(
                        Mockito.any(),
                        Mockito.any(),
                        Mockito.any()))
                .thenReturn(null);
        Throwable thrown = assertThrows(ValidationException.class, () -> {
            itemService.createComment(comment, 1, 1);
        });
        assertNotNull(thrown.getMessage());
    }

    @Test
    void testCreateComment() {
        ItemService itemService = new ItemServiceImpl(
                itemRepository,
                userRepository,
                bookingRepository,
                commentRepository,
                itemRequestRepository);
        Item item = new Item(
                1,
                "name",
                "description",
                true,
                null, null, null, null, null, null);
        User user = new User(1, "name", "email@mail.ru");
        Comment comment = new Comment(3,
                "text",
                item,
                user,
                LocalDateTime.now());
        Booking booking = new Booking(
                1,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(2),
                item,
                user,
                Status.APPROVED);
        Mockito
                .when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(item));
        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(bookingRepository.findBookingByBookerAndItemAndEndBefore(
                        Mockito.any(),
                        Mockito.any(),
                        Mockito.any()))
                .thenReturn(booking);
        Mockito
                .when(commentRepository.save(Mockito.any()))
                .thenReturn(comment);
        Assertions.assertEquals(3, itemService.createComment(comment, 1, 1).getId());
    }

    @Test
    void testFindAll() {
        ItemService itemService = new ItemServiceImpl(
                itemRepository,
                userRepository,
                bookingRepository,
                commentRepository,
                itemRequestRepository);
        User user = new User(1, "name", "email@mail.ru");
        Sort sortBy = Sort.by(Sort.Direction.ASC, "id");
        Pageable page = PageRequest.of(0, 50, sortBy);
        Page<Item> itemPage = new Page<Item>() {
            @Override
            public int getTotalPages() {
                return 0;
            }

            @Override
            public long getTotalElements() {
                return 0;
            }

            @Override
            public <U> Page<U> map(Function<? super Item, ? extends U> converter) {
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
            public List<Item> getContent() {
                Item item1 = new Item();
                item1.setOwner(user);
                Item item2 = new Item();
                item2.setOwner(user);
                List<Item> items = new ArrayList<>();
                items.add(item1);
                items.add(item2);
                return items;
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
            public Iterator<Item> iterator() {
                return null;
            }
        };
        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(itemRepository.findAllByOrderByIdAsc(page))
                .thenReturn(itemPage);
        Assertions.assertEquals(2, itemService.findAll(user.getId(), null, null).size());
    }

    @Test
    void testFindAllByRequestId() {
        ItemService itemService = new ItemServiceImpl(
                itemRepository,
                userRepository,
                bookingRepository,
                commentRepository,
                itemRequestRepository);
        User user = new User(1, "name", "email@mail.ru");
        Item item1 = new Item();
        item1.setOwner(user);
        Item item2 = new Item();
        item2.setOwner(user);
        List<Item> items = new ArrayList<>();
        items.add(item1);
        items.add(item2);
        Mockito
                .when(itemRequestRepository.getReferenceById(Mockito.anyLong()))
                .thenReturn(new ItemRequest());
        Mockito
                .when(itemRepository.findItemsByRequestOrderByIdAsc(Mockito.any()))
                .thenReturn(items);
        Assertions.assertEquals(2, itemService.findAllByRequestId(1).size());
    }

    @Test
    void testUpdateWithWrongOwner() {
        ItemService itemService = new ItemServiceImpl(
                itemRepository,
                userRepository,
                bookingRepository,
                commentRepository,
                itemRequestRepository);
        User user = new User(2, "name", "email@mail.ru");
        Item item = new Item(
                1,
                "name",
                "description",
                true,
                user,
                null, null, null, null, null);
        Mockito
                .when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(item));
        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(itemRepository.getReferenceById(Mockito.anyLong()))
                .thenReturn(item);
        Throwable thrown = assertThrows(NoSuchElementException.class, () -> {
            itemService.update(item, 1, 1);
        });
        assertNotNull(thrown.getMessage());
    }

    @Test
    void testUpdateItemWithNewName() {
        ItemService itemService = new ItemServiceImpl(
                itemRepository,
                userRepository,
                bookingRepository,
                commentRepository,
                itemRequestRepository);
        User user = new User(2, "name", "email@mail.ru");
        Item item = new Item(
                1,
                "name",
                "description",
                true,
                user,
                null, null, null, null, null);
        Item newItem = new Item(
                1,
                "newName",
                null,
                null,
                null,
                null, null, null, null, null);
        Mockito
                .when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(item));
        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(itemRepository.getReferenceById(Mockito.anyLong()))
                .thenReturn(item);
        Mockito
                .when(itemRepository.save(Mockito.any()))
                .thenReturn(newItem);
        Assertions.assertEquals("newName", itemService.update(newItem, item.getId(), user.getId()).getName());
    }

    @Test
    void testUpdateItemWithNewDescription() {
        ItemService itemService = new ItemServiceImpl(
                itemRepository,
                userRepository,
                bookingRepository,
                commentRepository,
                itemRequestRepository);
        User user = new User(2, "name", "email@mail.ru");
        Item item = new Item(
                1,
                "name",
                "description",
                true,
                user,
                null, null, null, null, null);
        Item newItem = new Item(
                1,
                "newName",
                "newDescription",
                null,
                null,
                null, null, null, null, null);
        Mockito
                .when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(item));
        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(itemRepository.getReferenceById(Mockito.anyLong()))
                .thenReturn(item);
        Mockito
                .when(itemRepository.save(Mockito.any()))
                .thenReturn(newItem);
        Assertions.assertEquals("newDescription", itemService.update(newItem, item.getId(), user.getId()).getDescription());
    }

    @Test
    void testUpdateItemWithNewAvailable() {
        ItemService itemService = new ItemServiceImpl(
                itemRepository,
                userRepository,
                bookingRepository,
                commentRepository,
                itemRequestRepository);
        User user = new User(2, "name", "email@mail.ru");
        Item item = new Item(
                1,
                "name",
                "description",
                true,
                user,
                null, null, null, null, null);
        Item newItem = new Item(
                1,
                "newName",
                "newDescription",
                false,
                null,
                null, null, null, null, null);
        Mockito
                .when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(item));
        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(itemRepository.getReferenceById(Mockito.anyLong()))
                .thenReturn(item);
        Mockito
                .when(itemRepository.save(Mockito.any()))
                .thenReturn(newItem);
        Assertions.assertEquals(false, itemService.update(newItem, item.getId(), user.getId()).getAvailable());
    }

    @Test
    void testUpdateItemWithNewOwner() {
        ItemService itemService = new ItemServiceImpl(
                itemRepository,
                userRepository,
                bookingRepository,
                commentRepository,
                itemRequestRepository);
        User user = new User(2, "name", "email@mail.ru");
        User newUser = new User(3, "name", "email@mail.ru");
        Item item = new Item(
                1,
                "name",
                "description",
                true,
                user,
                null, null, null, null, null);
        Item newItem = new Item(
                1,
                "newName",
                "newDescription",
                false,
                newUser,
                null, null, null, null, null);
        Mockito
                .when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(item));
        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(itemRepository.getReferenceById(Mockito.anyLong()))
                .thenReturn(item);
        Mockito
                .when(itemRepository.save(Mockito.any()))
                .thenReturn(newItem);
        Assertions.assertEquals(3, itemService.update(newItem, item.getId(), user.getId()).getOwner().getId());
    }

    @Test
    void testUpdateItemWithNewRequest() {
        ItemService itemService = new ItemServiceImpl(
                itemRepository,
                userRepository,
                bookingRepository,
                commentRepository,
                itemRequestRepository);
        User user = new User(2, "name", "email@mail.ru");
        Item item = new Item(
                1,
                "name",
                "description",
                true,
                user,
                null, null, null, null, null);
        ItemRequest itemRequest = new ItemRequest(5, null, null, null, null);
        Item newItem = new Item(
                1,
                "Name",
                "Description",
                true,
                user,
                itemRequest,
                null, null, null, null);
        Mockito
                .when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(item));
        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(itemRepository.getReferenceById(Mockito.anyLong()))
                .thenReturn(item);
        Mockito
                .when(itemRepository.save(Mockito.any()))
                .thenReturn(newItem);
        Assertions.assertEquals(5, itemService.update(newItem, item.getId(), user.getId()).getRequest().getId());
    }

    @Test
    void testFindById() {
        ItemService itemService = new ItemServiceImpl(
                itemRepository,
                userRepository,
                bookingRepository,
                commentRepository,
                itemRequestRepository);
        User user = new User(2, "name", "email@mail.ru");
        User owner = new User(3, "name3", "3email@mail.ru");
        Item item = new Item(
                1,
                "name",
                "description",
                true,
                owner,
                null, null, null, null, null);
        Comment comment1 = new Comment();
        Comment comment2 = new Comment();
        List<Comment> comments = new ArrayList<>();
        comments.add(comment1);
        comments.add(comment2);
        Booking booking1 = new Booking(
                1,
                LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1),
                item,
                user,
                Status.APPROVED);
        Booking booking2 = new Booking(
                2,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                item,
                user,
                Status.APPROVED);
        List<Booking> bookings = new ArrayList<>();
        bookings.add(booking1);
        bookings.add(booking2);
        Mockito
                .when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(item));
        Mockito
                .when(itemRepository.getReferenceById(Mockito.anyLong()))
                .thenReturn(item);
        Mockito
                .when(commentRepository.findCommentsByItemOrderByCreatedDesc(Mockito.any()))
                .thenReturn(comments);
        Mockito
                .when(bookingRepository.findBookingsByItemOrderByStart(Mockito.any()))
                .thenReturn(bookings);
        Assertions.assertEquals(1, itemService.findById(1, 3).getId());
    }

    @Test
    void testDeleteItem() {
        ItemService itemService = new ItemServiceImpl(
                itemRepository,
                userRepository,
                bookingRepository,
                commentRepository,
                itemRequestRepository);
        Item item = new Item(
                1,
                "name",
                "description",
                true,
                null,
                null, null, null, null, null);
        Mockito
                .when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(item));
        Mockito
                .when(itemRepository.getReferenceById(Mockito.anyLong()))
                .thenReturn(item);
        itemService.delete(1);
        Mockito
                .verify(itemRepository, Mockito.times(1))
                .delete(item);
    }

    @Test
    void testSearchItemsWithBlankText() {
        ItemService itemService = new ItemServiceImpl(
                itemRepository,
                userRepository,
                bookingRepository,
                commentRepository,
                itemRequestRepository);
        Mockito
                .when(itemRepository.findAll())
                .thenReturn(null);
        Assertions.assertEquals(null, itemService.search(""));
    }

    @Test
    void testSearchItems() {
        ItemService itemService = new ItemServiceImpl(
                itemRepository,
                userRepository,
                bookingRepository,
                commentRepository,
                itemRequestRepository);
        Item item1 = new Item(
                1,
                "nameDESC",
                "xxccvvv",
                true,
                null,
                null, null, null, null, null);
        Item item2 = new Item(
                2,
                "name",
                "description",
                false,
                null,
                null, null, null, null, null);
        List<Item> items = new ArrayList<>();
        items.add(item1);
        items.add(item2);
        Mockito
                .when(itemRepository.findAll())
                .thenReturn(items);
        Assertions.assertEquals(1, itemService.search("desc").size());
    }

    @Test
    void testGetUserItemsFromBD() {
        User user = new User(
                1,
                "userName",
                "email@mail.ru"
        );
        User userFromBd = userService.create(user);
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
        itemService.create(item, userFromBd.getId());
        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.owner.id = :id", Item.class);
        Item itemFromBD = query
                .setParameter("id", userFromBd.getId())
                .getSingleResult();
        assertThat(itemFromBD.getId(), notNullValue());
        assertThat(itemFromBD.getName(), equalTo(item.getName()));
        assertThat(itemFromBD.getDescription(), equalTo(item.getDescription()));
        assertThat(itemFromBD.getOwner().getName(), equalTo(userFromBd.getName()));

        List<Item> items = itemService.findAll(userFromBd.getId(), null, null);
        assertThat(items.size(), equalTo(1));
        assertThat(items.get(0).getOwner().getName(), equalTo(user.getName()));

    }


}
