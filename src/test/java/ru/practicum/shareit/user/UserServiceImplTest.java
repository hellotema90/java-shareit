package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.exeption.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    UserRepository userRepository;
    @InjectMocks
    UserServiceImpl userService;
    User user;
    UserDto userDto;

    @BeforeEach
    void beforeEach() {
        user = User.builder().id(1L).name("user1").email("user1@mail.ru").build();
        userDto = UserMapper.toUserDto(user);
    }

    @Test
    void addIsOk() {
        when(userRepository.save(any())).thenReturn(user);
        assertEquals(UserMapper.toUserDto(user), userService.addUser(user));
        verify(userRepository).save(any());
    }

    @Test
    void patchUpdateWithIncorrectField() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        Map<String, String> fieldsUpdate = Map.of("name1", "name2");
        assertThrows(ValidationException.class, () -> userService.updateUser(user.getId(), fieldsUpdate));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getUserDtoByIdIsOk() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        UserDto userDto = userService.getUserDtoById(user.getId());
        assertEquals(userDto.getName(), user.getName());
        verify(userRepository).findById(anyLong());
    }

    @Test
    void getUserDtoByIdWithIncorrectId() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> userService.getUserDtoById(0L));
        verify(userRepository).findById(anyLong());
    }

    @Test
    void getByCorrectId() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        User checkUser = userService.getUserById(anyLong());
        assertEquals(user, checkUser);
        verify(userRepository).findById(anyLong());
    }

    @Test
    void getByIncorrectId() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> userService.getUserById(anyLong()));
    }

    @Test
    void getAllWithCollectionUser() {
        when(userRepository.findAll()).thenReturn(List.of(user));
        List<UserDto> users = userService.getAllUsers();
        assertEquals(1, users.size());
        assertEquals(user.getId(), users.get(0).getId());
        assertEquals(user.getName(), users.get(0).getName());
        verify(userRepository).findAll();
    }

    @Test
    void getAllWithEmptyCollection() {
        when(userRepository.findAll()).thenReturn(Collections.emptyList());
        List<UserDto> users = userService.getAllUsers();
        assertTrue(users.isEmpty());
        verify(userRepository).findAll();
    }

    @Test
    void deleteIsOk() {
        userService.deleteUserById(anyLong());
        verify(userRepository).deleteById(anyLong());
    }
}
