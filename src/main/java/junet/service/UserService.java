package junet.service;


import junet.dao.UserDAO;
import junet.dto.User;

import java.util.*;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;

public class UserService {
    private final List<User> users = new ArrayList<>();
    private final UserDAO userDAO;

    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }
    public boolean delete (Integer userId) {
        return userDAO.delete(userId);
    }


    public List<User> getAll() {
        return users;
    }

    public void add(User... user) {
        this.users.addAll(Arrays.asList(user));
    }


    public Optional<User> login(String username, String password) {
        if (username==null || password==null) {
            throw new  IllegalArgumentException ("username or password is not null");
        }
        return users.stream().filter(user -> user.getUsername().equals(username))
                .filter(user -> user.getPassword().equals(password)).findFirst();

    }


    public Map<Integer, User> getAllConvertedById() {
        return users.stream().collect(toMap(User::getId, Function.identity()));
    }
}

