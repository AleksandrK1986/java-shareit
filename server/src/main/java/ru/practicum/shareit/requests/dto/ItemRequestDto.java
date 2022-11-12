package ru.practicum.shareit.requests.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * // TODO .
 */
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ItemRequestDto {
    private long id;
    private String description;
    private LocalDateTime created;
    private List<ItemDto> items;
}
