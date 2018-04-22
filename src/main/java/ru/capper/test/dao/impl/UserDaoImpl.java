package ru.capper.test.dao.impl;


import ru.capper.test.dao.AbstractJDBCDao;
import ru.capper.test.dao.PersistException;
import ru.capper.test.model.User;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.LinkedList;
import java.util.List;

public class UserDaoImpl extends AbstractJDBCDao<User, Integer> {

    private class PersistUser extends User {
        public void setId(int id) {
            super.setId(id);
        }
    }

    @Override
    public String getSelectQuery() {
        return "SELECT id, count_pong FROM test.user ";
    }

    @Override
    public String getCreateQuery() {
        return "INSERT INTO test.user (id, count_pong) VALUES (?, ?);";
    }

    @Override
    public String getUpdateQuery() {
        return "UPDATE test.user SET count_pong = ? WHERE id = ?;";
    }

    @Override
    public String getDeleteQuery() {
        return "DELETE FROM test.user WHERE id= ?;";
    }

    @Override
    public User create(Integer key) throws PersistException {
        User user = new User();
        user.setId(key);
        return persist(user);
    }

    public void increasePong(Integer key) throws PersistException {
        String sql = "UPDATE test.user SET count_pong = count_pong + 1 WHERE id = ?;";
        try (PreparedStatement statement = super.getConnection().prepareStatement(sql);) {
            statement.setInt(1, key);
            int count = statement.executeUpdate();
            if (count == 0) {
                User user = new User();
                user.setId(key);
                user.setCountPong(BigDecimal.ONE);
                persist(user);
            } else if (count != 1) {
                throw new PersistException("On update modify more then 1 record: " + count);
            }
        } catch (Exception e) {
            throw new PersistException(e);
        }
    }

    public UserDaoImpl(Connection connection) {
        super(connection);
    }

    @Override
    protected List<User> parseResultSet(ResultSet rs) throws PersistException {
        LinkedList<User> result = new LinkedList<User>();
        try {
            while (rs.next()) {
                PersistUser user = new PersistUser();
                user.setId(rs.getInt("id"));
                user.setCountPong(rs.getBigDecimal("count_pong"));
                result.add(user);
            }
        } catch (Exception e) {
            throw new PersistException(e);
        }
        return result;
    }

    @Override
    protected void prepareStatementForUpdate(PreparedStatement statement, User object) throws PersistException {
        try {
            statement.setInt(1, object.getId());
            statement.setBigDecimal(2, object.getCountPong());
        } catch (Exception e) {
            throw new PersistException(e);
        }
    }

    @Override
    protected void prepareStatementForInsert(PreparedStatement statement, User object) throws PersistException {
        try {
            statement.setInt(1, object.getId());
            statement.setBigDecimal(2, object.getCountPong());
        } catch (Exception e) {
            throw new PersistException(e);
        }
    }
}
