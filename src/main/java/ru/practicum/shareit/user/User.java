package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * // TODO .
 */
@AllArgsConstructor
@Setter
@Getter
public class User {
    private long id;
    private String name;
    private String email;
}