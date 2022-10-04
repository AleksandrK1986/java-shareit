package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * // TODO .
 */
@AllArgsConstructor
@Setter
@Getter
public class UserDto {
    private long id;
    private String name;
    private String email;
}