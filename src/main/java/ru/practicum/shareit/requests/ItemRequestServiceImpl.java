package ru.practicum.shareit.requests;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.user.UserRepository;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ItemRequestServiceImpl implements ItemRequestService {
    private ItemRequestRepository itemRequestRepository;
    private UserRepository userRepository;
    private ItemService itemService;

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
            throw new ValidationException("Передано пустое описание");
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
    public List<ItemRequest> findAll(long userId, Integer from, Integer size) {
        checkUser(userId);
        List<ItemRequest> itemRequests = new ArrayList<>();
        if (from == null || size == null) {
            itemRequests = itemRequestRepository.findAll();
        } else {
            if (from.intValue() < 0 || size.intValue() <= 0) {
                throw new ValidationException("Передан некорректный размер ожидаемого ответа: from "
                        + from + ", size " + size);
            }
            Sort sortByCreated = Sort.by(Sort.Direction.DESC, "created");
            Pageable page = PageRequest.of(from.intValue(), from.intValue() + size.intValue(), sortByCreated);
            do {
                Page<ItemRequest> itemRequestPage = itemRequestRepository.findAll(page);
                for (ItemRequest ir : itemRequestPage.getContent()) {
                    if (ir.getRequestor().getId() != userId) {
                        itemRequests.add(ir);
                    }
                }
                if (itemRequestPage.hasNext()) {
                    page = itemRequestPage.nextOrLastPageable();
                } else {
                    page = null;
                }
            } while (page != null);
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


