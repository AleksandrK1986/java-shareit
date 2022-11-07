package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {

    private final ItemClient itemClient;

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> findById(@PathVariable long itemId,
                                           @RequestHeader(value = "X-Sharer-User-Id") long userId) {
        log.info("Get item with itemId={}, userId={}", itemId, userId);
        return itemClient.findById(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> findAll(@RequestHeader(value = "X-Sharer-User-Id") long userId,
                                          @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                          @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Get items with userId={}, from={}, size={}", userId, from, size);
        return itemClient.findAll(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestParam(name = "text", defaultValue = "") String text) {
        log.info("Search item with text={}", text);
        return itemClient.search(text);
    }

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody ItemDto itemDto,
                          @RequestHeader(value = "X-Sharer-User-Id") long userId) {
        log.info("Create item={} with userId={}", itemDto, userId);
        return itemClient.create(itemDto, userId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@Valid @RequestBody CommentDto commentDto,
                                    @RequestHeader(value = "X-Sharer-User-Id") long userId,
                                    @PathVariable long itemId) {
        log.info("Create comment {} to item={}, user={}", commentDto, itemId, userId);
        return itemClient.createComment(commentDto, itemId, userId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@Valid @RequestBody ItemDto itemDto,
                          @RequestHeader(value = "X-Sharer-User-Id") long userId,
                          @PathVariable long itemId) {
        log.info("Update item {} itemId={}, userId={}", itemDto, itemId, userId);
        return itemClient.update(itemDto, itemId, userId);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable long itemId) {
        log.info("Delete item with itemId={}", itemId);
        return itemClient.delete(itemId);
    }
}
