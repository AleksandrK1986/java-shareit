package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.ValidationException;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class UserServiceImpl implements UserService {
    private UserStorage storage;

    @Autowired
    public UserServiceImpl(UserStorage storage) {
        this.storage = storage;
    }

    @Override
    public User create(User user) {
        checkUserEmail(user);
        return storage.create(user);
    }

    @Override
    public List<User> findAll() {
        return storage.findAll();
    }

    @Override
    public User update(User user, long userId) {
        checkUser(userId);
        if(user.getEmail() != null) {
            checkUserEmail(user);
        }
        if(user.getId()==0){
            user.setId(userId);
        }
        return storage.update(user);
    }

    @Override
    public User findById(long id) {
        checkUser(id);
        return storage.findById(id);
    }

    @Override
    public void delete(long id) {
        checkUser(id);
        storage.delete(id);
    }

    private void checkUserEmail (User user) {
        if(user.getEmail() == null) {
            throw new ValidationException("Передан пустой email");
        } else if(!user.getEmail().contains("@") || !user.getEmail().contains(".")) {
            throw new ValidationException("Передан некорректный email");
        } else {
            for(User u: storage.findAll()){
                if(u.getEmail().equals(user.getEmail())) {
                    throw new RuntimeException("Пользователь с таким email уже существует");
                }
            }
        }
    }

    private void checkUser(long userId) {
        if(storage.findById(userId) == null) {
            throw new NoSuchElementException("Пользователь не найден в хранилище");
        }
    }
}
