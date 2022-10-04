package ru.practicum.shareit.user;

import java.util.List;

public interface UserStorage {
    User findById(long id);

    List<User> findAll();

    User create(User data);

    User update(User data);

    void delete(long id);
}
