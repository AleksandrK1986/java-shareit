package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@AutoConfigureMockMvc
@WebMvcTest(UserController.class)
public class UserControllerTest {
    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mvc;

    private UserDto userDto;

    private User user;

    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        userDto = new UserDto(
                1,
                "john",
                "doe@mail.com");
        user = new User(
                1,
                "john",
                "doe@mail.com");
        try (MockedStatic<UserMapper> theMock = Mockito.mockStatic(UserMapper.class)) {
            theMock.when(() -> UserMapper.toUser(Mockito.any()))
                    .thenReturn(user);
            assertEquals(user, UserMapper.toUser(userDto));
        }
        try (MockedStatic<UserMapper> theMock = Mockito.mockStatic(UserMapper.class)) {
            theMock.when(() -> UserMapper.toUserDto(Mockito.any()))
                    .thenReturn(userDto);
            assertEquals(userDto, UserMapper.toUserDto(user));
        }
    }

    @Test
    void testCreateUser() throws Exception {
        when(userService.create(any()))
                .thenReturn(user);
        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.id", is(userDto.getId()), long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName()), String.class))
                .andExpect(jsonPath("$.email", is(userDto.getEmail()), String.class));
    }

    @Test
    void testFindByIdUser() throws Exception {
        when(userService.findById(Mockito.anyLong()))
                .thenReturn(user);
        mvc.perform(get("/users/{id}", 1))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.id", is(userDto.getId()), long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName()), String.class))
                .andExpect(jsonPath("$.email", is(userDto.getEmail()), String.class));
    }

    @Test
    void testFindAll() throws Exception {
        List<User> users = new ArrayList<>();
        users.add(user);
        when(userService.findAll())
                .thenReturn(users);
        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", Matchers.is(userDto.getId()), long.class));
    }

    @Test
    void testUpdateUser() throws Exception {
        when(userService.update(Mockito.any(), Mockito.anyLong()))
                .thenReturn(user);
        mvc.perform(patch("/users/{userId}", 1)
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.id", is(userDto.getId()), long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName()), String.class))
                .andExpect(jsonPath("$.email", is(userDto.getEmail()), String.class));
    }

    @Test
    void testDeleteUser() throws Exception {
        mvc.perform(delete("/users/{id}", 1)
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
        Mockito
                .verify(userService, Mockito.times(1))
                .delete(Mockito.anyLong());
    }

}
