package ru.practicum.shareit.booking.model;

public enum Status {
    WAITING,
    APPROVED,
    REJECTED,
    CANCELED
}

/*
status — статус бронирования. Может принимать одно из следующих
значений: WAITING — новое бронирование, ожидает одобрения, APPROVED —
бронирование подтверждено владельцем, REJECTED — бронирование
отклонено владельцем, CANCELED — бронирование отменено создателем
 */