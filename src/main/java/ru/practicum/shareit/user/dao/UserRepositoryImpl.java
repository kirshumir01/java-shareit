package ru.practicum.shareit.user.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {
    private final Map<Long, User> users = new HashMap<>();
    Set<String> emails = new HashSet<>();
    private long currentId = 0L;

    @Override
    public User create(User user) {
        user.setId(++currentId);
        if (emails.contains(user.getEmail())) {
            --currentId;
            throw new ConflictException(String.format("Пользователь с адресом электронной %s почты существует.",
                    user.getEmail()));
        }
        emails.add(user.getEmail());
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
    public User update(User newUser) {
        if (emails.contains(newUser.getEmail()) &&
           !users.get(newUser.getId()).getEmail().equals(newUser.getEmail())
        ) {
            throw new ConflictException(String.format("Пользователь с адресом электронной %s почты существует.",
                    newUser.getEmail()));
        }
        emails.remove(users.get(newUser.getId()).getEmail());
        emails.add(newUser.getEmail());
        users.put(newUser.getId(), newUser);
        return newUser;
    }

    @Override
    public void delete(long userId) {
        User deletedUser = users.remove(userId);
        if (users.get(userId) == null) {
            emails.remove(deletedUser.getEmail());
        }
    }
}