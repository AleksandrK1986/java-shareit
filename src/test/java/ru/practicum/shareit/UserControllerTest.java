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

//import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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
            theMock.when(() -> UserMapper.toUser(ArgumentMatchers.any()))
                    .thenReturn(user);
            assertEquals(user, UserMapper.toUser(userDto));
        }
        try (MockedStatic<UserMapper> theMock = Mockito.mockStatic(UserMapper.class)) {
            theMock.when(() -> UserMapper.toUserDto(ArgumentMatchers.any()))
                    .thenReturn(userDto);
            assertEquals(userDto, UserMapper.toUserDto(user));
        }
    }

    @Test
    void testCreateUser() throws Exception {
        Mockito.when(userService.create(ArgumentMatchers.any()))
                .thenReturn(user);
        mvc.perform(MockMvcRequestBuilders.post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(jsonPath("$.id", is(userDto.getId()), long.class))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.is(userDto.getName()), String.class))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email", Matchers.is(userDto.getEmail()), String.class));
    }

    @Test
    void testFindByIdUser() throws Exception {
        Mockito.when(userService.findById(Mockito.anyLong()))
                .thenReturn(user);
        mvc.perform(MockMvcRequestBuilders.get("/users/{id}", 1))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(jsonPath("$.id", is(userDto.getId()), long.class))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.is(userDto.getName()), String.class))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email", Matchers.is(userDto.getEmail()), String.class));
    }

    @Test
    void testFindAll() throws Exception {
        List<User> users = new ArrayList<>();
        users.add(user);
        Mockito.when(userService.findAll())
                .thenReturn(users);
        mvc.perform(MockMvcRequestBuilders.get("/users"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(1)))
                .andExpect(jsonPath("$[0].id", Matchers.is(userDto.getId()), long.class));
    }

    @Test
    void testUpdateUser() throws Exception {
        Mockito.when(userService.update(ArgumentMatchers.any(), Mockito.anyLong()))
                .thenReturn(user);
        mvc.perform(MockMvcRequestBuilders.patch("/users/{userId}", 1)
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(jsonPath("$.id", is(userDto.getId()), long.class))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.is(userDto.getName()), String.class))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email", Matchers.is(userDto.getEmail()), String.class));
    }

    @Test
    void testDeleteUser() throws Exception {
        mvc.perform(MockMvcRequestBuilders.delete("/users/{id}", 1)
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
        Mockito
                .verify(userService, Mockito.times(1))
                .delete(Mockito.anyLong());
    }

}
