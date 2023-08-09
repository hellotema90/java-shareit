package ru.practicum.shareit.user.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exeption.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Transactional
    public UserDto addUser(User user) {
        return UserMapper.toUserDto(userRepository.save(user));
    }

    @Transactional
    public UserDto updateUser(long id, Map<String, String> updates) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с таким id %d not не найден", id)));
        if (updates.containsKey("name")) {
            String name = updates.get("name");
            user.setName(name.trim());
            userRepository.save(user);//
        }
        if (updates.containsKey("email")) {
            String email = updates.get("email");
            user.setEmail(email.trim());
            userRepository.save(user);//
        }
        return UserMapper.toUserDto(user);
    }

    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        return UserMapper.toUserDtoList(userRepository.findAll());
    }

    @Transactional(readOnly = true)
    public User getUserById(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с таким id %d not не найден", id)));
    }

    @Transactional
    public UserDto getUserDtoById(long id) {
        return UserMapper.toUserDto(getUserById(id));
    }

    @Transactional
    public void deleteUserById(long userId) {
        userRepository.deleteById(userId);
    }


}

