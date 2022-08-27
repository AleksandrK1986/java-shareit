package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import ru.practicum.shareit.requests.ItemRequest;

/**
 * // TODO .
 */
@AllArgsConstructor
@Setter
@Getter
public class Item {
    private long id;
    private String name;
    private String description;
    private Boolean available;
    private long owner;
    private ItemRequest request;
}
