package ru.capper.test.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.capper.test.dao.DaoFactory;
import ru.capper.test.dao.PersistException;
import ru.capper.test.dao.impl.DaoFactoryImpl;
import ru.capper.test.dao.impl.UserDaoImpl;
import ru.capper.test.model.User;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class UserServiceImpl implements UserService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    private DaoFactory<Connection> factory = new DaoFactoryImpl();

    @Override
    public BigDecimal incrementPong(int userId) {
        try (Connection connection = factory.getContext()) {
            connection.setAutoCommit(false);
            UserDaoImpl dao = (UserDaoImpl) factory.getDao(connection, User.class);
            dao.increasePong(userId);
            User user = dao.getByPK(userId);
            connection.commit();
            return user.getCountPong();
        } catch (PersistException | SQLException e) {
            LOGGER.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public List<User> getPage(int page, int size) {
        try (Connection connection = factory.getContext()) {
            connection.setAutoCommit(false);
            UserDaoImpl dao = (UserDaoImpl) factory.getDao(connection, User.class);
            List<User> userList = dao.getAll();
            connection.commit();

            return userList.stream()
                    .sorted(Comparator.comparing(User::getId))
                    .skip((page - 1) * size)
                    .limit(size)
                    .collect(Collectors.toList());

        } catch (PersistException | SQLException e) {
            LOGGER.error(e.getMessage(), e);
            return null;
        }
    }
}
