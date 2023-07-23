package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Map;

public interface UserService {
    UserDto addUser(User user);

    UserDto updateUser(int id, Map<String, String> updates);

    List<UserDto> getAllUsers();

    UserDto getUserById(int id);

    void deleteUserById(int userId);

    void validateContainsEmail(String email, int id);

    void validateContainsName(String name, int id);
}
