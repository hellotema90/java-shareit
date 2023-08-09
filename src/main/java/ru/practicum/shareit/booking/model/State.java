package ru.practicum.shareit.booking.model;

import ru.practicum.shareit.exeption.IllegalArgumentException;

public enum State {
    ALL, CURRENT, PAST, FUTURE, WAITING, REJECTED;

    public static State getState(String text) {
        try {
            return State.valueOf(text.toUpperCase().trim());
        } catch (Exception e) {
            throw new IllegalArgumentException(String.format("Unknown state: %s", text));
        }
    }
}
