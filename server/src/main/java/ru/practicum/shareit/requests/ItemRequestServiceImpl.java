package ru.practicum.shareit.requests;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ItemRequestServiceImpl implements ItemRequestService {
    private ItemRequestRepository itemRequestRepository;
    private UserRepository userRepository;
    private ItemService itemService;

    private final int maxSize = 50;

    @Autowired
    public ItemRequestServiceImpl(ItemRequestRepository itemRequestRepository,
                                  UserRepository userRepository,
                                  ItemService itemService) {
        this.itemRequestRepository = itemRequestRepository;
        this.userRepository = userRepository;
        this.itemService = itemService;
    }

    @Override
    public ItemRequest create(ItemRequest data, long userId) {
        checkUser(userId);
        if (data.getDescription() == null || data.getDescription().isBlank()) {
            throw new NullPointerException("Передано пустое описание");
        }
        data.setRequestor(userRepository.getReferenceById(userId));
        data.setCreated(LocalDateTime.now());
        return itemRequestRepository.save(data);
    }

    @Override
    public ItemRequest findById(long id, long userId) {
        checkUser(userId);
        if (!itemRequestRepository.existsById(id)) {
            throw new NoSuchElementException("Запрос с id " + id + " не найден");
        }
        ItemRequest itemRequest = itemRequestRepository.getReferenceById(id);
        itemRequest.setItems(itemService.findAllByRequestId(id));
        return itemRequest;
    }

    @Override
    public List<ItemRequest> findAllUserRequest(long userId, Integer from, Integer size) {
        checkUser(userId);
        List<ItemRequest> itemRequests = new ArrayList<>();
        Sort sortByCreated = Sort.by(Sort.Direction.DESC, "created");
        Pageable page;
        if (from == null || size == null) {
            page = PageRequest.of(0, maxSize, sortByCreated);
        } else {
            page = PageRequest.of(from, from + size, sortByCreated);
        }
        Page<ItemRequest> itemRequestPage = itemRequestRepository.findAll(page);
        for (ItemRequest ir : itemRequestPage.getContent()) {
            if (ir.getRequestor().getId() == userId) {
                itemRequests.add(ir);
            }
        }
        for (ItemRequest ir : itemRequests) {
            ir.setItems(itemService.findAllByRequestId(ir.getId()));
        }
        return itemRequests;
    }

    @Override
    public List<ItemRequest> findAllAlienRequests(long userId, Integer from, Integer size) {
        checkUser(userId);
        List<ItemRequest> itemRequests = new ArrayList<>();
        Sort sortByCreated = Sort.by(Sort.Direction.DESC, "created");
        Pageable page;
        if (from == null || size == null) {
            page = PageRequest.of(0, maxSize, sortByCreated);
        } else {
            page = PageRequest.of(from, from + size, sortByCreated);
        }
        Page<ItemRequest> itemRequestPage = itemRequestRepository.findAll(page);
        for (ItemRequest ir : itemRequestPage.getContent()) {
            if (ir.getRequestor().getId() != userId) {
                itemRequests.add(ir);
            }
        }
        for (ItemRequest ir : itemRequests) {
            ir.setItems(itemService.findAllByRequestId(ir.getId()));
        }
        return itemRequests;
    }

    private void checkUser(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NoSuchElementException("Пользователь с id " + userId + " не найден");
        }
    }
}


