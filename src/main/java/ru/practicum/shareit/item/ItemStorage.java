package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {
    Item findById(long id);

    List<Item> findAll();

    Item create(Item data);

    Item update(Item data);

    void delete(long id);
}
