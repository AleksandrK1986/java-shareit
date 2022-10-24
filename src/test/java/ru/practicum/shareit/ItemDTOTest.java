package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;


import static org.junit.jupiter.api.Assertions.assertEquals;

@JsonTest
public class ItemDTOTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testMappingItemToItemDTO() throws Exception {
        User user = new User(
                1,
                "userName",
                "email@mail.ru"
        );
        Item item = new Item(
                1,
                "name",
                "description",
                true,
                user,
                null, null, null, null, null
        );
        String expectedResult = "{\"id\":1,\"name\":\"name\",\"description\":" +
                "\"description\",\"available\":true,\"owner\":{\"id\":1,\"name\":\"userName\"," +
                "\"email\":\"email@mail.ru\"},\"requestId\":null,\"comments\":[]}";
        assertEquals(expectedResult, objectMapper.writeValueAsString(ItemMapper.toItemDto(item)));
    }

    @Test
    void testMappingItemToItemWithBookingDTO() throws Exception {
        User user = new User(
                1,
                "userName",
                "email@mail.ru"
        );
        User booker = new User(
                2,
                "booker",
                "email@mail.ru"
        );
        Item item = new Item(
                1,
                "name",
                "description",
                true,
                user,
                null, null, null, null, null
        );
        Booking lastBooking = new Booking(
                1,
                null,
                null,
                item,
                booker,
                Status.APPROVED
        );
        Booking nextBooking = new Booking(
                2,
                null,
                null,
                item,
                booker,
                Status.APPROVED
        );
        item.setLastBooking(lastBooking);
        item.setNextBooking(nextBooking);
        String expectedResult = "{\"id\":1,\"name\":\"name\",\"description\":\"description\"," +
                "\"available\":true,\"owner\":{\"id\":1,\"name\":\"userName\",\"" +
                "email\":\"email@mail.ru\"},\"request\":null,\"lastBooking\":{\"id\":1," +
                "\"bookerId\":2,\"start\":null,\"end\":null,\"status\":\"APPROVED\"}," +
                "\"nextBooking\":{\"id\":2,\"bookerId\":2,\"start\":null,\"end\":null," +
                "\"status\":\"APPROVED\"},\"comments\":[]}";
        assertEquals(expectedResult, objectMapper.writeValueAsString(ItemMapper.toItemWithBookingDto(item)));

    }
}
