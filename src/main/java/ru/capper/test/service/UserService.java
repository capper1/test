package ru.capper.test.service;

import ru.capper.test.model.User;

import java.math.BigInteger;
import java.util.List;

public interface UserService {
    BigInteger incrementPong(int userId);
    List<User> getPage(int page);
}
