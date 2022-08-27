package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Repository
public class UserStorageImpl implements UserStorage {

    private HashMap<Long, User> users = new HashMap<>();
    private static int counter = 0;

    @Override
    public User findById(long id) {
        return users.get(id);
    }

    @Override
    public List<User> findAll() {
        List<User> usersNew = new ArrayList<>();
        for (User u : users.values()) {
            usersNew.add(u);
        }
        return usersNew;
    }

    @Override
    public User create(User user) {
        user.setId(getNewId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        User newUser = users.get(user.getId());
        if(user.getName() != null) {
            newUser.setName(user.getName());
        }
        if(user.getEmail() != null) {
            newUser.setEmail(user.getEmail());
        }
        users.remove(user.getId());
        users.put(user.getId(), newUser);
        return newUser;
    }

    @Override
    public void delete(long id) {
        users.remove(id);
    }

    private int getNewId() {
        counter = counter + 1;
        return counter;
    }

}
