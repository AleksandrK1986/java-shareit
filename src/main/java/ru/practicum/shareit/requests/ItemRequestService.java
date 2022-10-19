package ru.practicum.shareit.requests;

import java.util.List;

public interface ItemRequestService {
    List<ItemRequest> findAll(long userId, Integer from, Integer size);

    ItemRequest create(ItemRequest data, long userId);

    ItemRequest findById(long id, long userId);

}
