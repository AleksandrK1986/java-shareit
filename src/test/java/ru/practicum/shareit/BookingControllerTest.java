package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.requests.ItemRequest;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.dto.ItemRequestMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
public class BookingControllerTest {
    @Mock
    private BookingService bookingService;

    @InjectMocks
    private BookingController controller;

    @Autowired
    private MockMvc mvc;

    private Item item;

    private ItemDto itemDto;

    private Comment comment;

    private CommentDto commentDto;

    private ItemRequest itemRequest;

    private ItemRequestDto itemRequestDto;

    private ItemWithBookingDto itemWithBookingDto;

    private Booking booking;

    private BookingDto bookingDto;

    private ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();
        mapper = JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .build();
        LocalDateTime time = LocalDateTime.now();
        List<Comment> comments = new ArrayList<>();
        User user = new User(1, "name", "email@mail.ru");
        UserDto userDto = new UserDto(1, "name", "email@mail.ru");
        comment = new Comment(
                1,
                "text",
                item,
                user,
                time);
        comments.add(comment);
        List<CommentDto> commentDtos = new ArrayList<>();
        commentDto = new CommentDto(
                1,
                "text",
                "name",
                time);
        commentDtos.add(commentDto);
        List<Item> items = new ArrayList<>();
        ItemRequest itemRequestForItem = new ItemRequest(
                1,
                "description",
                user,
                time,
                null);
        item = new Item(
                1,
                "name",
                "description",
                true,
                user,
                itemRequestForItem,
                null, null,
                comments,
                1L);
        items.add(item);
        List<ItemDto> itemDtos = new ArrayList<>();
        itemDto = new ItemDto(
                1,
                "name",
                "description",
                true,
                userDto,
                1L,
                commentDtos);
        itemDtos.add(itemDto);
        itemRequest = new ItemRequest(
                1,
                "description",
                user,
                time,
                null);
        itemRequestDto = new ItemRequestDto(
                1,
                "description",
                time,
                null);
        itemWithBookingDto = new ItemWithBookingDto(
                1,
                "name",
                "description",
                true,
                userDto,
                itemRequest,
                null,
                null,
                commentDtos);
        booking = new Booking(
                1,
                time,
                time.plusDays(1),
                item,
                user,
                Status.APPROVED);
        bookingDto = new BookingDto(
                1,
                1,
                itemDto,
                userDto,
                time,
                time.plusDays(1),
                Status.APPROVED);
        BookingForItemDto bookingForItemDto = new BookingForItemDto(
                1,
                1,
                time,
                time.plusDays(1),
                Status.APPROVED);

        try (MockedStatic<ItemRequestMapper> theMock = Mockito.mockStatic(ItemRequestMapper.class)) {
            theMock.when(() -> ItemRequestMapper.toItemRequest(Mockito.any()))
                    .thenReturn(itemRequest);
            assertEquals(itemRequest, ItemRequestMapper.toItemRequest(itemRequestDto));
        }
        try (MockedStatic<ItemRequestMapper> theMock = Mockito.mockStatic(ItemRequestMapper.class)) {
            theMock.when(() -> ItemRequestMapper.toItemRequestDto(Mockito.any()))
                    .thenReturn(itemRequestDto);
            assertEquals(itemRequestDto, ItemRequestMapper.toItemRequestDto(itemRequest));
        }
        try (MockedStatic<ItemMapper> theMock = Mockito.mockStatic(ItemMapper.class)) {
            theMock.when(() -> ItemMapper.toItemDto(Mockito.any()))
                    .thenReturn(itemDto);
            assertEquals(itemDto, ItemMapper.toItemDto(item));
        }
        try (MockedStatic<ItemMapper> theMock = Mockito.mockStatic(ItemMapper.class)) {
            theMock.when(() -> ItemMapper.toItem(Mockito.any()))
                    .thenReturn(item);
            assertEquals(item, ItemMapper.toItem(itemDto));
        }
        try (MockedStatic<ItemMapper> theMock = Mockito.mockStatic(ItemMapper.class)) {
            theMock.when(() -> ItemMapper.toComment(Mockito.any()))
                    .thenReturn(comment);
            assertEquals(comment, ItemMapper.toComment(commentDto));
        }
        try (MockedStatic<ItemMapper> theMock = Mockito.mockStatic(ItemMapper.class)) {
            theMock.when(() -> ItemMapper.toCommentDto(Mockito.any()))
                    .thenReturn(commentDto);
            assertEquals(commentDto, ItemMapper.toCommentDto(comment));
        }
        try (MockedStatic<ItemMapper> theMock = Mockito.mockStatic(ItemMapper.class)) {
            theMock.when(() -> ItemMapper.toItemWithBookingDto(Mockito.any()))
                    .thenReturn(itemWithBookingDto);
            assertEquals(itemWithBookingDto, ItemMapper.toItemWithBookingDto(item));
        }
        try (MockedStatic<BookingMapper> theMock = Mockito.mockStatic(BookingMapper.class)) {
            theMock.when(() -> BookingMapper.toBookingDto(Mockito.any()))
                    .thenReturn(bookingDto);
            assertEquals(bookingDto, BookingMapper.toBookingDto(booking));
        }
        try (MockedStatic<BookingMapper> theMock = Mockito.mockStatic(BookingMapper.class)) {
            theMock.when(() -> BookingMapper.toBooking(Mockito.any()))
                    .thenReturn(booking);
            assertEquals(booking, BookingMapper.toBooking(bookingDto));
        }
        try (MockedStatic<BookingMapper> theMock = Mockito.mockStatic(BookingMapper.class)) {
            theMock.when(() -> BookingMapper.toBookingForItemDto(Mockito.any()))
                    .thenReturn(bookingForItemDto);
            assertEquals(bookingForItemDto, BookingMapper.toBookingForItemDto(booking));
        }
    }

    @Test
    void testCreateBooking() throws Exception {
        when(bookingService.create(Mockito.any(), Mockito.anyLong()))
                .thenReturn(booking);
        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", "1")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), long.class))
                .andExpect(jsonPath("$.itemId", is(bookingDto.getItemId()), long.class));
    }

    @Test
    void testApproveBooking() throws Exception {
        when(bookingService.approved(Mockito.anyLong(), Mockito.anyBoolean(), Mockito.anyLong()))
                .thenReturn(booking);
        mvc.perform(patch("/bookings/{bookingId}", 1)
                        .header("X-Sharer-User-Id", "1")
                        .param("approved", "true")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), long.class))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString()), String.class));
    }

    @Test
    void testFindByIdBooking() throws Exception {
        when(bookingService.getById(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(booking);
        mvc.perform(get("/bookings/{bookingId}", 1)
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.id", Matchers.is(bookingDto.getId()), long.class));
    }

    @Test
    void testFindAllBookings() throws Exception {
        List<Booking> bookingList = new ArrayList<>();
        bookingList.add(booking);
        when(bookingService.getAllUserBookings(Mockito.anyString(), Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(bookingList);
        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", "1")
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().json(mapper.writeValueAsString(Arrays.asList(bookingDto))));
    }

    @Test
    void testFindAllUserItemsBookings() throws Exception {
        List<Booking> bookingList = new ArrayList<>();
        bookingList.add(booking);
        when(bookingService.getAllUserItemsBookings(Mockito.anyString(), Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(bookingList);
        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", "1")
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().json(mapper.writeValueAsString(Arrays.asList(bookingDto))));
    }
}

