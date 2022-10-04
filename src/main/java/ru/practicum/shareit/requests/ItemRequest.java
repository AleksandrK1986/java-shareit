package ru.practicum.shareit.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

/**
 * // TODO .
 */
@AllArgsConstructor
@Setter
@Getter
public class ItemRequest {
    private long id;
    private String description;
    private User requestor;
    private LocalDateTime created;

    //@Column(name = "registration_date")
    //    private Instant registrationDate = Instant.now();
}
