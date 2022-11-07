package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.List;

import static ru.practicum.shareit.item.dto.ItemMapper.*;

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
    public ItemWithBookingDto findById(@PathVariable long id,
                                       @RequestHeader(value = "X-Sharer-User-Id") long userId) {
        return toItemWithBookingDto(service.findById(id, userId));
    }

    @GetMapping
    public List<ItemWithBookingDto> findAll(@RequestHeader(value = "X-Sharer-User-Id") long userId,
                                            @RequestParam(name = "from") Integer from,
                                            @RequestParam(name = "size") Integer size) {
        List<Item> items = service.findAll(userId, from, size);
        List<ItemWithBookingDto> itemsDto = new ArrayList<>();
        for (Item i : items) {
            itemsDto.add(toItemWithBookingDto(i));
        }
        return itemsDto;
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam(name = "text") String text) {
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
    public ItemDto create(@RequestBody ItemDto itemDto,
                          @RequestHeader(value = "X-Sharer-User-Id") long userId) {
        return toItemDto(service.create(toItem(itemDto), userId));
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestBody CommentDto commentDto,
                                    @RequestHeader(value = "X-Sharer-User-Id") long userId,
                                    @PathVariable long itemId) {
        return toCommentDto(service.createComment(toComment(commentDto), itemId, userId));
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestBody ItemDto itemDto,
                          @RequestHeader(value = "X-Sharer-User-Id") long userId,
                          @PathVariable long itemId) {
        return toItemDto(service.update(toItem(itemDto), itemId, userId));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id) {
        service.delete(id);
    }

}
