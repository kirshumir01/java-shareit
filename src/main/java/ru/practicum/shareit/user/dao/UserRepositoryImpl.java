package ru.practicum.shareit.user.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {
    private final Map<Long, User> users = new HashMap<>();
    private long currentId = 0L;

    @Override
    public User create(User user) {
        user.setId(++currentId);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public Optional<User> get(long userId) {
        return Optional.ofNullable(users.get(userId));
    }

    @Override
    public User update(User newUser, long userId) {
        users.put(userId, newUser);
        return newUser;
    }

    @Override
    public void delete(long userId) {
        users.remove(userId);
    }
}