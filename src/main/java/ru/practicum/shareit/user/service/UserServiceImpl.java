package ru.practicum.shareit.user.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exeption.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserDto addUser(User user) {
        validateContainsEmail(user.getEmail(), user.getId());
        validateContainsName(user.getName(), user.getId());
        return UserMapper.toUserDto(userRepository.addUser(user));
    }

    public UserDto updateUser(int id, Map<String, String> updates) {
        User user = userRepository.getUserById(id);
        if (updates.containsKey("name")) {
            String name = updates.get("name");
            validateContainsName(name, id);
            user.setName(name.trim());
            userRepository.updateUser(user);
        }
        if (updates.containsKey("email")) {
            String email = updates.get("email");
            validateContainsEmail(email, id);
            user.setEmail(email.trim());
            userRepository.updateUser(user);
        }
        return UserMapper.toUserDto(user);
    }

    public List<UserDto> getAllUsers() {
        return UserMapper.toUserDtoList(userRepository.getAllUsers());
    }

    public UserDto getUserById(int id) {
        return UserMapper.toUserDto(userRepository.getUserById(id));
    }

    public void deleteUserById(int userId) {
        userRepository.deleteUserById(userId);
    }

    public void validateContainsEmail(String email, int id) {
        if ((email == null) || (email.isBlank())) {
            log.info("email пользователя пустой");
            throw new NotFoundException("email пользователя пустой");
        }
        String foundEmail = email.trim();

        Optional<User> userFound = userRepository.getAllUsers().stream()
                .filter(t -> t.getId() != id)
                .filter(t -> foundEmail.equalsIgnoreCase(t.getEmail()))
                .findFirst();
        if (userFound.isPresent()) {
            log.info("Пользователь с таким email {} уже существует.", email);
            throw new ConflictException("Пользователь с таким EMAIL уже существует.");
        }
    }

    public void validateContainsName(String name, int id) {
        if ((name == null) || (name.isBlank())) {
            log.info("email пользователя пустой");
            throw new ForbiddenException("email пользователя пустой");
        }
        String foundName = name.trim();

        Optional<User> userFound = userRepository.getAllUsers().stream()
                .filter(t -> t.getId() != id)
                .filter(t -> foundName.equalsIgnoreCase(t.getName()))
                .findFirst();
        if (userFound.isPresent()) {
            log.info("Пользователь с таким name {} уже существует.", name);
            throw new NotFoundException("Пользователь с таким NAME уже существует.");
        }
    }


}

