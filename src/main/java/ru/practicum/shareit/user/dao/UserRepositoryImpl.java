package ru.practicum.shareit.user.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
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
            user.setEmail("duplicate");
            --currentId;
            return user;
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
        if (newUser.getEmail() == null) {
            newUser.setEmail(users.get(newUser.getId()).getEmail());
        }
        if (newUser.getName() == null) {
            newUser.setName(users.get(newUser.getId()).getName());
        }
        if (emails.contains(newUser.getEmail()) &&
           !users.get(newUser.getId()).getEmail().equals(newUser.getEmail())
        ) {
            newUser.setEmail("duplicate");
            return newUser;
        }
        emails.remove(users.get(newUser.getId()).getEmail());
        emails.add(newUser.getEmail());
        users.put(newUser.getId(), newUser);
        return newUser;
    }

    @Override
    public void delete(long userId) {
        emails.remove(users.get(userId).getEmail());
        users.remove(userId);
    }
}