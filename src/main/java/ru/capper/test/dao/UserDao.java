package ru.capper.test.dao;


import ru.capper.test.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.LinkedList;
import java.util.List;

public class UserDao extends AbstractJDBCDao<User, Integer> {

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
    public User create() throws PersistException {
        User user = new User();
        user.setId(3);
        return persist(user);
    }

    public UserDao(Connection connection) {
        super(connection);
    }

    @Override
    protected List<User> parseResultSet(ResultSet rs) throws PersistException {
        LinkedList<User> result = new LinkedList<User>();
        try {
            while (rs.next()) {
                PersistUser user = new PersistUser();
                user.setId(rs.getInt("ID"));
                user.setCountPong(rs.getBigDecimal("COUNT_PONG"));
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
