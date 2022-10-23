package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.requests.ItemRequestRepository;
import ru.practicum.shareit.user.UserRepository;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ItemServiceImpl implements ItemService {
    private ItemRepository itemRepository;
    private UserRepository userRepository;
    private BookingRepository bookingRepository;
    private CommentRepository commentRepository;
    private ItemRequestRepository itemRequestRepository;

    private final int maxSize = 50;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository,
                           UserRepository userRepository,
                           BookingRepository bookingRepository,
                           CommentRepository commentRepository,
                           ItemRequestRepository itemRequestRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
        this.itemRequestRepository = itemRequestRepository;
    }

    @Override
    public Item create(Item item, long userId) {
        checkUser(userId);
        item.setOwner(userRepository.getReferenceById(userId));
        if (item.getName().isBlank()) {
            throw new ValidationException("У вещи должно быть указано название");
        }
        if (item.getAvailable() == null) {
            throw new ValidationException("У вещи должна быть указан доступность");
        }
        if (item.getDescription() == null) {
            throw new ValidationException("У вещи должно быть указано описание");
        }
        if (item.getRequestId() != null) {
            item.setRequest(itemRequestRepository.getReferenceById(item.getRequestId()));
        }
        return itemRepository.save(item);
    }

    @Override
    public Comment createComment(Comment comment, long itemId, long userId) {
        checkItem(itemId);
        checkUser(userId);
        Booking booking = bookingRepository.findBookingByBookerAndItemAndEndBefore(
                userRepository.getReferenceById(userId),
                itemRepository.getReferenceById(itemId),
                LocalDateTime.now());
        if (comment.getText().isBlank()) {
            throw new ValidationException("Передан пустой комментарий");
        }
        if (booking != null) {
            comment.setAuthor(userRepository.getReferenceById(userId));
            comment.setItem(itemRepository.getReferenceById(itemId));
            comment.setCreated(LocalDateTime.now());
            return commentRepository.save(comment);
        } else {
            throw new ValidationException("Пользователь не может комментирировать вещь, которую не бронировал");
        }
    }

    @Override
    public List<Item> findAll(long userId, Integer from, Integer size) {
        checkUser(userId);
        Sort sortBy = Sort.by(Sort.Direction.ASC, "id");
        Pageable page = null;
        if (from != null || size != null) {
            page = PageRequest.of(from / size, from / size, sortBy);
        } else {
            page = PageRequest.of(0, maxSize, sortBy);
        }
        Page<Item> items = itemRepository.findAllByOrderByIdAsc(page);
        List<Item> userItems = new ArrayList<>();
        for (Item i : items.getContent()) {
            if (i.getOwner().getId() == userId) {
                userItems.add(checkAndAddItemBookings(i, userId));
            }
        }
        return userItems;
    }

    @Override
    public List<Item> findAllByRequestId(long requestId) {
        return itemRepository.findItemsByRequestOrderByIdAsc(itemRequestRepository.getReferenceById(requestId));
    }

    @Override
    public Item update(Item item, long itemId, long userId) {
        checkItem(itemId);
        checkUser(userId);
        if (itemRepository.getReferenceById(itemId).getOwner().getId() != userId) {
            throw new NoSuchElementException("Нельзя обновлять вещь, не принадлежащую пользователю");
        }
        Item newItem = itemRepository.getReferenceById(itemId);
        if (item.getName() != null) {
            newItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            newItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            newItem.setAvailable(item.getAvailable());
        }
        if (item.getOwner() != null) {
            newItem.setOwner(userRepository.getReferenceById(userId));
        }
        if (item.getRequest() != null) {
            newItem.setRequest(item.getRequest());
        }
        return itemRepository.save(newItem);
    }

    @Override
    public Item findById(long itemId, long userId) {
        checkItem(itemId);
        Item item = itemRepository.getReferenceById(itemId);
        item.setComments(commentRepository.findCommentsByItemOrderByCreatedDesc(item));
        return checkAndAddItemBookings(item, userId);
    }

    @Override
    public void delete(long id) {
        checkItem(id);
        itemRepository.delete(itemRepository.getReferenceById(id));
    }

    public List<Item> search(String text) {
        List<Item> allItems = itemRepository.findAll();
        List<Item> items = new ArrayList<>();
        if (text.equals("")) {
            return null;
        }
        for (Item i : allItems) {
            if ((i.getDescription().toLowerCase().contains(text.toLowerCase()) ||
                    i.getName().toLowerCase().contains(text.toLowerCase())) &&
                    i.getAvailable()) {
                items.add(i);
            }
        }
        return items;
    }

    private void checkUser(long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new NoSuchElementException("Пользователь не найден в хранилище");
        }
    }

    private void checkItem(long itemId) {
        if (itemRepository.findById(itemId).isEmpty()) {
            throw new NoSuchElementException("Вещь не найдена в хранилище");
        }
    }

    private Item checkAndAddItemBookings(Item i, long userId) {
        if (i.getOwner().getId() == userId) {
            Booking lastBooking = null;
            Booking nextBooking = null;
            List<Booking> itemBookings = bookingRepository.findBookingsByItemOrderByStart(i);
            for (Booking b : itemBookings) {
                if ((b.getEnd().isAfter(LocalDateTime.now()) &&
                        b.getStart().isBefore(LocalDateTime.now())) ||
                        b.getEnd().isBefore(LocalDateTime.now())) {
                    lastBooking = b;
                }
            }
            for (Booking b : itemBookings) {
                if (b.getStart().isAfter(LocalDateTime.now())) {
                    nextBooking = b;
                }
            }
            i.setLastBooking(lastBooking);
            i.setNextBooking(nextBooking);
        }
        return i;
    }
}
