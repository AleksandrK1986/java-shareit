package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.ArrayList;
import java.util.List;

import static ru.practicum.shareit.user.dto.UserMapper.toUser;
import static ru.practicum.shareit.user.dto.UserMapper.toUserDto;

/**
 * // TODO .
 */
@RestController
@RequestMapping(path = "/users")
public class UserController {
    private UserService service;

    @Autowired
    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public UserDto findById(@PathVariable long id) {
        return toUserDto(service.findById(id));
    }

    @GetMapping
    public List<UserDto> findAll() {
        List<User> users = service.findAll();
        List<UserDto> usersDto = new ArrayList<>();
        for (User u : users) {
            usersDto.add(toUserDto(u));
        }
        return usersDto;
    }

    @PostMapping
    public UserDto create(@RequestBody UserDto userDto) {
        return toUserDto(service.create(toUser(userDto)));
    }

    @PatchMapping("/{userId}")
    public UserDto update(@RequestBody UserDto userDto, @PathVariable long userId) {
        return toUserDto(service.update(toUser(userDto), userId));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id) {
        service.delete(id);
    }

}
