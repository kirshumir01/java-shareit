package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    User create(User user);

    List<User> getAll();

    Optional<User> get(long userId);

    User update(User newUser);

    void delete(long userId);
}