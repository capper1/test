package ru.capper.test.dao.impl;

import ru.capper.test.dao.ConnectionPool;
import ru.capper.test.dao.DaoFactory;
import ru.capper.test.dao.GenericDao;
import ru.capper.test.dao.PersistException;
import ru.capper.test.model.User;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class DaoFactoryImpl implements DaoFactory<Connection> {

    private Map<Class, DaoCreator> creators;

    public Connection getContext() throws PersistException {
        Connection connection = null;
        try {
            connection = ConnectionPool.getConnection();
        } catch (SQLException e) {
            throw new PersistException(e);
        }
        return connection;
    }

    @Override
    public GenericDao getDao(Connection connection, Class dtoClass) throws PersistException {
        DaoCreator creator = creators.get(dtoClass);
        if (creator == null) {
            throw new PersistException("Dao object for " + dtoClass + " not found.");
        }
        return creator.create(connection);
    }

    public DaoFactoryImpl() {
        creators = new HashMap<Class, DaoCreator>();
        creators.put(User.class, new DaoCreator<Connection>() {
            @Override
            public GenericDao create(Connection connection) {
                return new UserDaoImpl(connection);
            }
        });
    }
}
