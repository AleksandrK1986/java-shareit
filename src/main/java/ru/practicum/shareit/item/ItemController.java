package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

import static ru.practicum.shareit.item.dto.ItemMapper.toItem;
import static ru.practicum.shareit.item.dto.ItemMapper.toItemDto;

/**
 * // TODO .
 */
@RestController
@RequestMapping("/items")
public class ItemController {
    private ItemService service;

    @Autowired
    public ItemController(ItemService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public ItemDto findById(@PathVariable long id) {
        return toItemDto(service.findById(id));
    }

    @GetMapping
    public List<ItemDto> findAll(@RequestHeader(value = "X-Sharer-User-Id") long userId) {
        List<Item> items = service.findAll(userId);
        List<ItemDto> itemsDto = new ArrayList<>();
        for (Item i : items) {
            itemsDto.add(toItemDto(i));
        }
        return itemsDto;
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam(defaultValue = "") String text) {
        List<Item> items = service.search(text);
        List<ItemDto> itemsDto = new ArrayList<>();
        if (items != null) {
            for (Item i : items) {
                itemsDto.add(toItemDto(i));
            }
        }
        return itemsDto;
    }

    @PostMapping
    public ItemDto create(@Valid @RequestBody ItemDto itemDto,
                          @RequestHeader(value = "X-Sharer-User-Id") long userId) {
        return toItemDto(service.create(toItem(itemDto), userId));
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@Valid @RequestBody ItemDto itemDto,
                          @RequestHeader(value = "X-Sharer-User-Id") long userId,
                          @PathVariable long itemId) {
        return toItemDto(service.update(toItem(itemDto), itemId, userId));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id) {
        service.delete(id);
    }

}
