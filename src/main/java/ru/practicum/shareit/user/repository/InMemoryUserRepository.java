package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exeption.ForbiddenException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
public class InMemoryUserRepository implements UserRepository {
    private final Map<Integer, User> users = new HashMap<>();
    private final AtomicInteger generatorId = new AtomicInteger(0);

    private int countGeneratorId() {
        return generatorId.incrementAndGet();
    }

    @Override
    public User addUser(User user) {
        user.setId(countGeneratorId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User getUserById(int id) {
        User user = users.get(id);
        if (user != null) {
            return user;
        }
        throw new ForbiddenException(String.format("User с id: %d не существует", id));
    }

    @Override
    public void deleteUserById(int userId) {
        users.remove(userId);
    }

    @Override
    public boolean userExists(int id) {
        return users.containsKey(id);
    }
}
