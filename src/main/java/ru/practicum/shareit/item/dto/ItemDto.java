package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.requests.ItemRequest;

/**
 * // TODO .
 */
@AllArgsConstructor
@Setter
@Getter
public class ItemDto {
    private long id;
    private String name;
    private String description;
    private Boolean available;
    private long owner;
    private ItemRequest request;
}
