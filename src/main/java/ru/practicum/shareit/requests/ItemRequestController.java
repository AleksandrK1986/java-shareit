package ru.practicum.shareit.requests;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.requests.dto.ItemRequestDto;

import javax.validation.Valid;

import java.util.ArrayList;
import java.util.List;

import static ru.practicum.shareit.item.dto.ItemMapper.*;
import static ru.practicum.shareit.requests.dto.ItemRequestMapper.toItemRequest;
import static ru.practicum.shareit.requests.dto.ItemRequestMapper.toItemRequestDto;

/**
 * // TODO .
 */
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private ItemRequestService itemRequestService;

    @Autowired
    public ItemRequestController(ItemRequestService itemRequestService) {
        this.itemRequestService = itemRequestService;
    }

    @PostMapping
    public ItemRequestDto create(@Valid @RequestBody ItemRequestDto itemRequestDto,
                                 @RequestHeader(value = "X-Sharer-User-Id") long userId) {
        return toItemRequestDto(itemRequestService.create(toItemRequest(itemRequestDto), userId));
    }

    @GetMapping
    public List<ItemRequestDto> findAll(@RequestHeader(value = "X-Sharer-User-Id") long userId) {
        List<ItemRequestDto> itemRequestDtos = new ArrayList<>();
        List<ItemRequest> itemRequests = itemRequestService.findAll(userId, null, null);
        if (itemRequests != null) {
            for (ItemRequest ir : itemRequests) {
                itemRequestDtos.add(toItemRequestDto(ir));
            }
        }
        return itemRequestDtos;
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto findById(@PathVariable long requestId,
                                   @RequestHeader(value = "X-Sharer-User-Id") long userId) {
        return toItemRequestDto(itemRequestService.findById(requestId, userId));
    }

    @GetMapping("/all")
    public List<ItemRequestDto> findAllInPages(@RequestHeader(value = "X-Sharer-User-Id") long userId,
                                              @RequestParam(required = false) Integer from,
                                              @RequestParam(required = false) Integer size) {

        List<ItemRequest> itemRequests = itemRequestService.findAll(userId, from, size);
        List<ItemRequestDto> itemRequestDtos = new ArrayList<>();
        for (ItemRequest ir : itemRequests) {
            itemRequestDtos.add(toItemRequestDto(ir));
        }
        return itemRequestDtos;
    }
}


