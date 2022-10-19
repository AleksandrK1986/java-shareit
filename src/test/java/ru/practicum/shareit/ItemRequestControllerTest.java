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
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.requests.ItemRequest;
import ru.practicum.shareit.requests.ItemRequestController;
import ru.practicum.shareit.requests.ItemRequestService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
public class ItemRequestControllerTest {

    @Mock
    private ItemRequestService itemRequestService;

    @InjectMocks
    private ItemRequestController controller;

    @Autowired
    private MockMvc mvc;

    private ItemRequest itemRequest;

    private ItemRequestDto itemRequestDto;

    private Item item;
    private ItemDto itemDto;
    private Comment comment;
    private CommentDto commentDto;

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
                items);
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
                items);
        itemRequestDto = new ItemRequestDto(
                1,
                "description",
                time,
                itemDtos);
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
    }

    @Test
    void testCreateItemRequest() throws Exception {
        when(itemRequestService.create(Mockito.any(), Mockito.anyLong()))
                .thenReturn(itemRequest);
        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", "1")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription()), String.class));
    }

    @Test
    void testFindAllItemRequest() throws Exception {
        List<ItemRequest> itemRequestList = new ArrayList<>();
        itemRequestList.add(itemRequest);
        when(itemRequestService.findAll(Mockito.anyLong(), Mockito.any(), Mockito.any()))
                .thenReturn(itemRequestList);
        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().json(mapper.writeValueAsString(Arrays.asList(itemRequestDto))));
    }

    @Test
    void testFindAllItemRequestPages() throws Exception {
        List<ItemRequest> itemRequestList = new ArrayList<>();
        itemRequestList.add(itemRequest);
        when(itemRequestService.findAll(Mockito.anyLong(), Mockito.any(), Mockito.any()))
                .thenReturn(itemRequestList);
        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().json(mapper.writeValueAsString(Arrays.asList(itemRequestDto))));
    }

    @Test
    void testFindAllItemRequestById() throws Exception {
        when(itemRequestService.findById(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(itemRequest);
        mvc.perform(get("/requests/{requestId}", 1)
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.id", Matchers.is(itemRequestDto.getId()), long.class));
    }

}
