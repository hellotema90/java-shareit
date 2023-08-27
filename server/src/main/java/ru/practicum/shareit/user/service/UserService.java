package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    UserDto addUser(User user);

    UserDto updateUser(Long userId, UserDto userDto);

    List<UserDto> getAllUsers();

    User getUserById(long id);

    UserDto getUserDtoById(long id);

    void deleteUserById(long userId);

}
