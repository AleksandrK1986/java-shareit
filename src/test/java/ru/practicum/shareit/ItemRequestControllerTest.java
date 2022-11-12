package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
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
import java.util.List;

//import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@WebMvcTest(ItemRequestController.class)
public class ItemRequestControllerTest {

    @MockBean
    private ItemRequestService itemRequestService;

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
        Mockito.when(itemRequestService.create(Mockito.any(), Mockito.anyLong()))
                .thenReturn(itemRequest);
        mvc.perform(MockMvcRequestBuilders.post("/requests")
                        .header("X-Sharer-User-Id", "1")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription()), String.class));
    }

    @Test
    void testFindAllItemRequest() throws Exception {
        List<ItemRequest> itemRequestList = new ArrayList<>();
        itemRequestList.add(itemRequest);
        Mockito.when(itemRequestService.findAllUserRequest(Mockito.anyLong(), Mockito.any(), Mockito.any()))
                .thenReturn(itemRequestList);
        mvc.perform(MockMvcRequestBuilders.get("/requests")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(1)))
                .andExpect(jsonPath("$[0].id", Matchers.is(itemRequestDto.getId()), long.class));
    }

    @Test
    void testFindAllItemRequestPages() throws Exception {
        List<ItemRequest> itemRequestList = new ArrayList<>();
        itemRequestList.add(itemRequest);
        Mockito.when(itemRequestService.findAllAlienRequests(Mockito.anyLong(), Mockito.any(), Mockito.any()))
                .thenReturn(itemRequestList);
        mvc.perform(MockMvcRequestBuilders.get("/requests/all")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(1)))
                .andExpect(jsonPath("$[0].id", Matchers.is(itemRequestDto.getId()), long.class));
    }

    @Test
    void testFindAllItemRequestById() throws Exception {
        Mockito.when(itemRequestService.findById(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(itemRequest);
        mvc.perform(MockMvcRequestBuilders.get("/requests/{requestId}", 1)
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(jsonPath("$.id", Matchers.is(itemRequestDto.getId()), long.class));
    }

}
