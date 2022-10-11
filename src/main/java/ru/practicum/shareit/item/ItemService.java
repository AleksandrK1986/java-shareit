package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    List<Item> findAll(long userId);

    List<Item> search(String text);

    Item create(Item data, long userId);

    Comment createComment(Comment data, long itemId, long userId);

    Item update(Item data, long itemId, long userId);

    Item findById(long itemId, long userId);

    void delete(long id);
}
