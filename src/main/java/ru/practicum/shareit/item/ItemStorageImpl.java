package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Repository
public class ItemStorageImpl implements ItemStorage {
    private HashMap<Long, Item> items = new HashMap<>();
    private static long counter = 0;

    @Override
    public Item findById(long id) {
        return items.get(id);
    }

    @Override
    public List<Item> findAll() {
        List<Item> itemsNew = new ArrayList<>();
        for (Item i : items.values()) {
            itemsNew.add(i);
        }
        return itemsNew;
    }

    @Override
    public Item create(Item item) {
        item.setId(getNewId());
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item update(Item item) {
        Item newItem = items.get(item.getId());
        if(item.getName() != null) {
            newItem.setName(item.getName());
        }
        if(item.getDescription() != null) {
            newItem.setDescription(item.getDescription());
        }
        if(item.getAvailable() != null) {
            newItem.setAvailable(item.getAvailable());
        }
        if(item.getOwner() != 0) {
            newItem.setOwner(item.getOwner());
        }
        if(item.getRequest() != null) {
            newItem.setRequest(item.getRequest());
        }
        items.remove(item.getId());
        items.put(item.getId(), newItem);
        return newItem;
    }

    @Override
    public void delete(long id) {
        items.remove(id);
    }

    private long getNewId() {
        counter = counter + 1;
        return counter;
    }
}
