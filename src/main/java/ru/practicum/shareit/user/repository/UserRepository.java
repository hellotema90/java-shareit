package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository {
    User addUser(User user);

    User updateUser(User user);

    List<User> getAllUsers();

    User getUserById(int id);

    void deleteUserById(int id);

    boolean userExists(int id);

}
