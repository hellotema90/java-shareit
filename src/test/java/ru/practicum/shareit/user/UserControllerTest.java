package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.ValidationException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class UserControllerTest {
    @MockBean
    private UserService userService;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;
    private UserDto userDto;
    private User user;

    @BeforeEach
    void beforeEach() {
        user = User.builder().id(1L).name("user1").email("user1@mail.ru").build();
        userDto = UserDto.builder().id(1L).name("user1").email("user1@mail.ru").build();
    }

    @Test
    void createUserIsOk() throws Exception {
        when(userService.addUser(any())).thenReturn(userDto);
        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(user.getName())))
                .andExpect(jsonPath("$.email", is(user.getEmail())));
        verify(userService).addUser(any());
    }

    @Test
    void createUserErrorEmptyName() throws Exception {
        when(userService.addUser(any())).thenThrow(ValidationException.class);
        userDto.setName("");
        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createUserErrorBadEmail() throws Exception {
        when(userService.addUser(any())).thenThrow(ValidationException.class);
        userDto.setEmail("user_mail.ru");
        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void patchUpdateIsOk() throws Exception {
        Map<String, String> updates = Map.of(
                "name", "nameUpdate",
                "email", "update@yandex.ru");
        userDto.setName(updates.get("name"));
        userDto.setEmail(updates.get("email"));
        when(userService.updateUser(anyLong(), anyMap())).thenReturn(userDto);
        mvc.perform(patch("/users/{userId}", 1L)
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(updates.get("name"))))
                .andExpect(jsonPath("$.email", is(updates.get("email"))));
        verify(userService).updateUser(anyLong(), anyMap());
    }

    @Test
    void getUserIsOk() throws Exception {
        when(userService.getUserDtoById(anyLong())).thenReturn(userDto);
        mvc.perform(get("/users/{userId}", anyLong())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userDto.getId()))
                .andExpect(jsonPath("$.email").value(userDto.getEmail()))
                .andExpect(jsonPath("$.name").value(userDto.getName()));
        verify(userService).getUserDtoById(anyLong());
    }

    @Test
    void getUserWithIncorrectId() throws Exception {
        when(userService.getUserDtoById(anyLong())).thenThrow(NotFoundException.class);
        mvc.perform(get("/users/{userId}", anyLong()))
                .andExpect(status().isNotFound());
        verify(userService).getUserDtoById(anyLong());
    }

    @Test
    void getUserWithIncorrectId2() throws Exception {
        doThrow(NotFoundException.class).when(userService).getUserDtoById(anyLong());
        mvc.perform(get("/users/{userId}", anyLong()))
                .andExpect(status().isNotFound());
        verify(userService).getUserDtoById(anyLong());
    }

    @Test
    void getAllUsersIsOk() throws Exception {
        when(userService.getAllUsers()).thenReturn(List.of(userDto));
        mvc.perform(get("/users")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[*]").exists())
                .andExpect(jsonPath("$.[*]").isNotEmpty())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$.[0].id").value(userDto.getId()))
                .andExpect(jsonPath("$.[0].email").value(userDto.getEmail()))
                .andExpect(jsonPath("$.[0].name").value(userDto.getName()));
        verify(userService).getAllUsers();
    }

    @Test
    void getAllWithEmptyCollection() throws Exception {
        when(userService.getAllUsers()).thenReturn(List.of());
        mvc.perform(get("/users")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[*]").isEmpty());
        verify(userService).getAllUsers();
    }

    @Test
    void deleteIsOk() throws Exception {
        mvc.perform(delete("/users/1"))
                .andDo(print())
                .andExpectAll(
                        status().isOk());
        verify(userService).deleteUserById(1L);
    }
}
