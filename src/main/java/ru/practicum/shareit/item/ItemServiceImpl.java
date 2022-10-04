package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserStorage;

import javax.validation.ValidationException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ItemServiceImpl implements ItemService {
    private ItemStorage storage;
    private UserStorage userStorage;

    @Autowired
    public ItemServiceImpl(ItemStorage storage, UserStorage userStorage) {
        this.storage = storage;
        this.userStorage = userStorage;
    }

    @Override
    public Item create(Item item, long userId) {
        checkUser(userId);
        item.setOwner(userStorage.findById(userId));
        if (item.getName().isBlank()) {
            throw new ValidationException("У вещи должно быть указано название");
        }
        if (item.getAvailable() == null) {
            throw new ValidationException("У вещи должна быть указан доступность");
        }
        if (item.getDescription() == null) {
            throw new ValidationException("У вещи должно быть указано описание");
        }
        return storage.create(item);
    }

    @Override
    public List<Item> findAll(long userId) {
        checkUser(userId);
        List<Item> items = storage.findAll();
        List<Item> userItems = new ArrayList<>();
        for (Item i : items) {
            if (i.getOwner().getId() == userId) {
                userItems.add(i);
            }
        }
        return userItems;
    }

    @Override
    public Item update(Item item, long itemId, long userId) {
        checkItem(itemId);
        checkUser(userId);
        if (storage.findById(itemId).getOwner().getId() != userId) {
            throw new NoSuchElementException("Нельзя обновлять вещь, не принадлежащую пользователю");
        }
        if (item.getId() == 0) {
            item.setId(itemId);
        }
        return storage.update(item);
    }

    @Override
    public Item findById(long id) {
        checkItem(id);
        return storage.findById(id);
    }

    @Override
    public void delete(long id) {
        checkItem(id);
        storage.delete(id);
    }

    public List<Item> search(String text) {
        List<Item> allItems = storage.findAll();
        List<Item> items = new ArrayList<>();
        if (text.equals("")) {
            return null;
        }
        for (Item i : allItems) {
            if ((i.getDescription().toLowerCase().contains(text.toLowerCase()) ||
                    i.getName().toLowerCase().contains(text.toLowerCase())) &&
                    i.getAvailable()) {
                items.add(i);
            }
        }
        return items;
    }

    private void checkUser(long userId) {
        if (userStorage.findById(userId) == null) {
            throw new NoSuchElementException("Пользователь не найден в хранилище");
        }
    }

    private void checkItem(long itemId) {
        if (storage.findById(itemId) == null) {
            throw new NoSuchElementException("Вещь не найдена в хранилище");
        }
    }
}
