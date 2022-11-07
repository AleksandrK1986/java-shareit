package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.ValidationException;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User create(User user) {
        checkUserEmail(user);
        return userRepository.save(user);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User update(User user, long userId) {
        checkUser(userId);
        User newUser = userRepository.getReferenceById(userId);
        if (user.getEmail() != null) {
            newUser.setEmail(user.getEmail());
        }
        if (user.getName() != null) {
            newUser.setName(user.getName());
        }
        return userRepository.save(newUser);
    }

    @Override
    public User findById(long id) {
        checkUser(id);
        return userRepository.getReferenceById(id);
    }

    @Override
    public void delete(long id) {
        checkUser(id);
        userRepository.delete(userRepository.getReferenceById(id));
    }

    private void checkUserEmail(User user) {
        if (user.getEmail() == null) {
            throw new ValidationException("Передан пустой email");
        }
        if (!user.getEmail().contains("@") || !user.getEmail().contains(".")) {
            throw new ValidationException("Передан некорректный email");
        }

    }

    private void checkUser(long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new NoSuchElementException("Пользователь не найден в хранилище");
        }
    }
}
