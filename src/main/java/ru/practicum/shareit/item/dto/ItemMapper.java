package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

import static ru.practicum.shareit.user.dto.UserMapper.toUser;
import static ru.practicum.shareit.user.dto.UserMapper.toUserDto;

public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        UserDto userDto = null;
        if (item.getOwner() != null) {
            userDto = toUserDto(item.getOwner());
        }
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                userDto,
                item.getRequest() != null ? item.getRequest() : null
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
                itemDto.getRequest() != null ? itemDto.getRequest() : null
        );
    }
}

/*
 В дальнейшем можно будет добавлять в
них методы, которые нужны для обратного преобразования типов, — например,
toItem().
 */