package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.ArrayList;
import java.util.List;

import static ru.practicum.shareit.booking.dto.BookingMapper.toBookingForItemDto;
import static ru.practicum.shareit.user.dto.UserMapper.toUser;
import static ru.practicum.shareit.user.dto.UserMapper.toUserDto;

public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        UserDto userDto = null;
        if (item.getOwner() != null) {
            userDto = toUserDto(item.getOwner());
        }
        List<CommentDto> commentDtos = new ArrayList<>();
        if (item.getComments() != null) {
            for (Comment c : item.getComments()) {
                commentDtos.add(toCommentDto(c));
            }
        }
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                userDto,
                item.getRequest() != null ? item.getRequest() : null,
                commentDtos
        );
    }

    public static ItemWithBookingDto toItemWithBookingDto(Item item) {
        UserDto userDto = null;
        if (item.getOwner() != null) {
            userDto = toUserDto(item.getOwner());
        }
        BookingForItemDto lastBooking = null;
        BookingForItemDto nextBooking = null;
        if (item.getLastBooking() != null) {
            lastBooking = toBookingForItemDto(item.getLastBooking());
        }
        if (item.getNextBooking() != null) {
            nextBooking = toBookingForItemDto(item.getNextBooking());
        }
        List<CommentDto> commentDtos = new ArrayList<>();
        if (item.getComments() != null) {
            for (Comment c : item.getComments()) {
                commentDtos.add(toCommentDto(c));
            }
        }
        return new ItemWithBookingDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                userDto,
                item.getRequest() != null ? item.getRequest() : null,
                lastBooking,
                nextBooking,
                commentDtos
        );
    }

    public static Item toItem(ItemDto itemDto) {
        User user = null;
        if (itemDto.getOwner() != null) {
            user = toUser(itemDto.getOwner());
        }
        return new Item(
                itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                user,
                itemDto.getRequest() != null ? itemDto.getRequest() : null,
                null,
                null,
                null
        );
    }

    public static Comment toComment(CommentDto commentDto) {
        if (commentDto == null) {
            return null;
        } else {
            Comment comment = new Comment();
            comment.setText(commentDto.getText());
            return comment;
        }
    }

    public static CommentDto toCommentDto(Comment comment) {
        if (comment == null) {
            return null;
        } else {
            return new CommentDto(
                    comment.getId(),
                    comment.getText(),
                    comment.getAuthor().getName(),
                    comment.getCreated()
            );
        }
    }
}

/*
 В дальнейшем можно будет добавлять в
них методы, которые нужны для обратного преобразования типов, — например,
toItem().
 */