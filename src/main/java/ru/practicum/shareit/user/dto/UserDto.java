package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * // TODO .
 */
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class UserDto {
    private long id;
    private String name;
    private String email;
}