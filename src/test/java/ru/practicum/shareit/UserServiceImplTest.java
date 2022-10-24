package ru.practicum.shareit;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.UserServiceImpl;

import javax.validation.ValidationException;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    UserRepository userRepository;

    @Test
    void testCreateUserWithEmail() {
        UserService userService = new UserServiceImpl(userRepository);
        User user = new User(1, "name", "email@mail.ru");
        User newUser = new User();
        newUser.setEmail("email@mail.ru");
        Mockito
                .when(userRepository.save(Mockito.any()))
                .thenReturn(user);
        Assertions.assertEquals(1, userService.create(newUser).getId());
    }

    @Test
    void testCreateUserWithoutEmail() {
        UserService userService = new UserServiceImpl(userRepository);
        User newUser = new User();
        Throwable thrown = assertThrows(ValidationException.class, () -> {
            userService.create(newUser);
        });
        assertNotNull(thrown.getMessage());
    }

    @Test
    void testCreateUserWithWrongEmail() {
        UserService userService = new UserServiceImpl(userRepository);
        User user = new User(1, "name", "emailmail.ru");
        Throwable thrown = assertThrows(ValidationException.class, () -> {
            userService.create(user);
        });
        assertNotNull(thrown.getMessage());
    }

    @Test
    void testFindAll() {
        UserService userService = new UserServiceImpl(userRepository);
        List<User> users = new ArrayList<>();
        users.add(new User());
        users.add(new User());
        Mockito
                .when(userRepository.findAll())
                .thenReturn(users);
        Assertions.assertEquals(2, userService.findAll().size());
    }

    @Test
    void testUpdateWithNoFoundUser() {
        UserService userService = new UserServiceImpl(userRepository);
        User newUser = new User();
        Throwable thrown = assertThrows(NoSuchElementException.class, () -> {
            userService.update(newUser, 1);
        });
        assertNotNull(thrown.getMessage());
    }

    @Test
    void testUpdateUser() {
        UserService userService = new UserServiceImpl(userRepository);
        User user = new User(1, "name", "email@mail.ru");
        User newUser = user;
        newUser.setEmail("newEmail@mail.ru");
        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(userRepository.getReferenceById(user.getId()))
                .thenReturn(user);
        Mockito
                .when(userRepository.save(Mockito.any()))
                .thenReturn(newUser);
        Assertions.assertEquals("newEmail@mail.ru", userService.update(user, 1).getEmail());
    }

    @Test
    void testFindByIdUser() {
        UserService userService = new UserServiceImpl(userRepository);
        User user = new User(1, "name", "email@mail.ru");
        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(userRepository.getReferenceById(user.getId()))
                .thenReturn(user);
        Assertions.assertEquals(1, userService.findById(user.getId()).getId());
    }

    @Test
    void testDeleteUser() {
        UserService userService = new UserServiceImpl(userRepository);
        User user = new User(1, "name", "email@mail.ru");
        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(userRepository.getReferenceById(user.getId()))
                .thenReturn(user);
        userService.delete(user.getId());
        Mockito
                .verify(userRepository, Mockito.times(1))
                .delete(user);
    }


}
