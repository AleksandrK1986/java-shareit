package ru.practicum.shareit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.requests.ItemRequest;
import ru.practicum.shareit.requests.ItemRequestRepository;
import ru.practicum.shareit.requests.ItemRequestService;
import ru.practicum.shareit.requests.ItemRequestServiceImpl;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceImplTest {

    @Mock
    ItemRequestRepository itemRequestRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    ItemService itemService;

    @Test
    void testItemRequestCreateWrongUser() {
        ItemRequestService itemRequestService = new ItemRequestServiceImpl(
                itemRequestRepository,
                userRepository,
                itemService);
        ItemRequest itemRequest = new ItemRequest();
        Throwable thrown = assertThrows(NoSuchElementException.class, () -> {
            itemRequestService.create(itemRequest, 1);
        });
        assertNotNull(thrown.getMessage());
    }

    @Test
    void testItemRequestCreate() {
        ItemRequestService itemRequestService = new ItemRequestServiceImpl(
                itemRequestRepository,
                userRepository,
                itemService);
        User requester = new User(1, "name", "email@mail.ru");
        ItemRequest itemRequest = new ItemRequest(
                1,
                "description",
                requester,
                LocalDateTime.now(),
                null);
        Mockito
                .when(userRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);
        Mockito
                .when(userRepository.getReferenceById(Mockito.anyLong()))
                .thenReturn(new User());
        Mockito
                .when(itemRequestRepository.save(Mockito.any()))
                .thenReturn(itemRequest);
        Assertions.assertEquals(1, itemRequestService.create(itemRequest, 1).getId());
    }

    @Test
    void testItemRequestCreateWithEmptyDescription() {
        ItemRequestService itemRequestService = new ItemRequestServiceImpl(
                itemRequestRepository,
                userRepository,
                itemService);
        ItemRequest itemRequest = new ItemRequest(
                1,
                "",
                null,
                LocalDateTime.now(),
                null);
        Mockito
                .when(userRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);
        Throwable thrown = assertThrows(ValidationException.class, () -> {
            itemRequestService.create(itemRequest, 1);
        });
        assertNotNull(thrown.getMessage());
    }

    @Test
    void testFindByIdItemRequest() {
        ItemRequestService itemRequestService = new ItemRequestServiceImpl(
                itemRequestRepository,
                userRepository,
                itemService);
        Item item = new Item();
        List<Item> items = new ArrayList<>();
        items.add(item);
        ItemRequest itemRequest = new ItemRequest(
                1,
                "description",
                null,
                LocalDateTime.now(),
                items);
        Mockito
                .when(userRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);
        Mockito
                .when(itemRequestRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);
        Mockito
                .when(itemRequestRepository.getReferenceById(Mockito.anyLong()))
                .thenReturn(itemRequest);
        Mockito
                .when(itemService.findAllByRequestId(Mockito.anyLong()))
                .thenReturn(null);
        Assertions.assertEquals(1, itemRequestService.findById(1, 1).getId());
    }

    @Test
    void testFindByIdItemRequestNoSuchElement() {
        ItemRequestService itemRequestService = new ItemRequestServiceImpl(
                itemRequestRepository,
                userRepository,
                itemService);
        Item item = new Item();
        List<Item> items = new ArrayList<>();
        items.add(item);
        ItemRequest itemRequest = new ItemRequest(
                1,
                "description",
                null,
                LocalDateTime.now(),
                items);
        Mockito
                .when(userRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);
        Mockito
                .when(itemRequestRepository.existsById(Mockito.anyLong()))
                .thenReturn(false);
        Throwable thrown = assertThrows(NoSuchElementException.class, () -> {
            itemRequestService.findById(1, 1);
        });
        assertNotNull(thrown.getMessage());
    }

    @Test
    void testFindAllWithoutPagination() {
        ItemRequestService itemRequestService = new ItemRequestServiceImpl(
                itemRequestRepository,
                userRepository,
                itemService);
        ItemRequest itemRequestFr = new ItemRequest();
        ItemRequest itemRequestSec = new ItemRequest();
        ItemRequest itemRequestTh = new ItemRequest();
        List<ItemRequest> itemRequests = new ArrayList<>();
        itemRequests.add(itemRequestFr);
        itemRequests.add(itemRequestSec);
        itemRequests.add(itemRequestTh);
        List<Item> items = new ArrayList<>();
        Item item = new Item();
        items.add(item);
        Mockito
                .when(userRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);
        Mockito
                .when(itemRequestRepository.findAll())
                .thenReturn(itemRequests);
        Mockito
                .when(itemService.findAllByRequestId(Mockito.anyLong()))
                .thenReturn(items);
        Assertions.assertEquals(3, itemRequestService.findAll(1, null, null).size());

    }

    @Test
    void testFindAllWithWrongPagination() {
        ItemRequestService itemRequestService = new ItemRequestServiceImpl(
                itemRequestRepository,
                userRepository,
                itemService);
        Mockito
                .when(userRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);

        Throwable thrown = assertThrows(ValidationException.class, () -> {
            itemRequestService.findAll(1, -1, 0);
        });
        assertNotNull(thrown.getMessage());
    }

    @Test
    void testFindAllWithPagination() {
        ItemRequestService itemRequestService = new ItemRequestServiceImpl(
                itemRequestRepository,
                userRepository,
                itemService);
        Page<ItemRequest> itemRequests = new Page<ItemRequest>() {
            @Override
            public int getTotalPages() {
                return 0;
            }

            @Override
            public long getTotalElements() {
                return 0;
            }

            @Override
            public <U> Page<U> map(Function<? super ItemRequest, ? extends U> converter) {
                return null;
            }

            @Override
            public int getNumber() {
                return 0;
            }

            @Override
            public int getSize() {
                return 0;
            }

            @Override
            public int getNumberOfElements() {
                return 0;
            }

            @Override
            public List<ItemRequest> getContent() {
                List<ItemRequest> itemRequestList = new ArrayList<>();
                User requester = new User(22, "name", "email@mail.ru");
                ItemRequest itemRequestFr = new ItemRequest(
                        1,
                        "description",
                        requester,
                        LocalDateTime.now(),
                        null);
                ItemRequest itemRequestSec = new ItemRequest(
                        2,
                        "description",
                        requester,
                        LocalDateTime.now(),
                        null);
                ItemRequest itemRequestTh = new ItemRequest(
                        3,
                        "description",
                        requester,
                        LocalDateTime.now(),
                        null);
                itemRequestList.add(itemRequestFr);
                itemRequestList.add(itemRequestSec);
                itemRequestList.add(itemRequestTh);
                return itemRequestList;
            }

            @Override
            public boolean hasContent() {
                return false;
            }

            @Override
            public Sort getSort() {
                return null;
            }

            @Override
            public boolean isFirst() {
                return false;
            }

            @Override
            public boolean isLast() {
                return false;
            }

            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public boolean hasPrevious() {
                return false;
            }

            @Override
            public Pageable nextPageable() {
                return null;
            }

            @Override
            public Pageable previousPageable() {
                return null;
            }

            @Override
            public Iterator<ItemRequest> iterator() {
                return null;
            }
        };
        List<Item> items = new ArrayList<>();
        Item item = new Item();
        items.add(item);
        Mockito
                .when(userRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);
        Mockito
                .when(itemRequestRepository.findAll(Mockito.any(Pageable.class)))
                .thenReturn(itemRequests);
        Mockito
                .when(itemService.findAllByRequestId(Mockito.anyLong()))
                .thenReturn(items);
        Assertions.assertEquals(3, itemRequestService.findAll(1, 0, 3).size());

    }
}
