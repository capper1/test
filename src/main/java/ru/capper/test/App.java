package ru.capper.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.capper.test.server.WebServer;

import java.sql.*;

public class App {

    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

    private static final String SQL_SELECT = "SELECT COUNT(*) FROM user_connect WHERE user_id = ?";
    private static final String SQL_INSERT = "INSERT INTO user_connect(user_id) VALUES (?)";
    private static final String SQL_SELECT_ALL = "SELECT * FROM (SELECT user_id, COUNT(*) FROM user_connect GROUP BY user_id) AS temp";
    private static final String ERROR_MESSAGE = "useiId должен быть целым числом в диапазоне от -2147483648 до 2147483647";

    public static void main(String[] args) throws Exception {

        new ConnectionPool();

        new WebServer()

                .post("/PING", (request, response) -> {
                    int userId;
                    try {
                        userId = Integer.valueOf(request.body());
                    } catch (NumberFormatException nfe) {
                        LOGGER.warn(ERROR_MESSAGE);
                        return ERROR_MESSAGE;
                    }

                    try (Connection connection = ConnectionPool.getConnection()) {

                        try (PreparedStatement preparedStatement = connection.prepareStatement(SQL_INSERT)) {
                            if (null != preparedStatement) {
                                preparedStatement.setInt(1, userId);
                                preparedStatement.executeUpdate();
                            }
                        }

                        int i = 0;
                        try (PreparedStatement preparedStatementSelect = connection.prepareStatement(SQL_SELECT)) {
                            preparedStatementSelect.setInt(1, userId);
                            try (ResultSet resultSet = preparedStatementSelect.executeQuery()) {
                                if (null != resultSet && resultSet.next()) {
                                    i = resultSet.getInt(1);
                                }
                            }
                        }

                        return "PONG " + i;

                    } catch (SQLException ex) {
                        LOGGER.error(ex.toString(), ex);
                        return null;
                    }
                })

                .get("/STAT", (request, response) -> {
                    try (Connection connection = ConnectionPool.getConnection()) {

                        StringBuilder sb = new StringBuilder();
                        sb.append("[");
                        try (Statement statement = connection.createStatement()) {
                            try (ResultSet resultSet = statement.executeQuery(SQL_SELECT_ALL)) {
                                if (null != resultSet) {
                                    while (resultSet.next()) {
                                        int userId = resultSet.getInt(1);
                                        long countConnect = resultSet.getLong(2);
                                        sb
                                                .append("{\"userId\":\"")
                                                .append(userId)
                                                .append("\",\"times\":")
                                                .append(countConnect)
                                                .append("},");
                                    }
                                    sb.deleteCharAt(sb.length() - 1);
                                }
                            }
                        }

                        sb.append("]");

                        connection.close();
                        return sb.toString();
                    } catch (SQLException ex) {
                        LOGGER.error(ex.toString(), ex);
                        return null;
                    }
                })

                .start();
    }
}
