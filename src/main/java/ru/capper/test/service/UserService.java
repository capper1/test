package ru.capper.test.service;

import ru.capper.test.model.User;

import java.math.BigDecimal;
import java.util.List;

public interface UserService {
    BigDecimal incrementPong(int userId);

    List<User> getPage(int page, int size);
}
