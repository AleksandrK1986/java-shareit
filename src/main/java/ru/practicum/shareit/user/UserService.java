package ru.practicum.shareit.user;

import java.util.List;

public interface UserService {
    List<User> findAll();

    User create(User data);

    User update(User data, long userId);

    User findById(long id);

    void delete(long id);
}
