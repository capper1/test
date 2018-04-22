package ru.capper.test;

import org.junit.Test;
import ru.capper.test.dao.*;
import ru.capper.test.dao.impl.DaoFactoryImpl;
import ru.capper.test.dao.impl.UserDaoImpl;
import ru.capper.test.model.User;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedList;

public class UserDaoImplTestDB {

    @Test
    public void t1() throws PersistException, SQLException {
        DaoFactory<Connection> factory = new DaoFactoryImpl();
        Connection connection = factory.getContext();
        connection.setAutoCommit(false);

        UserDaoImpl dao = (UserDaoImpl) factory.getDao(connection, User.class);

        dao.increasePong(-2);

        LinkedList<User> all = (LinkedList<User>) dao.getAll();
        for (User user : all) {
            System.out.println(user.getId() + " - " + user.getCountPong());
        }

//        User user = new User();
//        user.setId(12);
//        user.setCountPong(new BigDecimal(12312312L));

//        dao.persist(user);
//        dao.update(user);

        connection.commit();
        connection.close();
    }
}
