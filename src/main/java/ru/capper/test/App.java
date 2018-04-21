package ru.capper.test;

import org.apache.commons.cli.*;
import ru.capper.test.server.WebServer;

import java.sql.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {

    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

    private static final String SQL_SELECT = "SELECT COUNT(*) FROM user_connect WHERE user_id = ?";
    private static final String SQL_INSERT = "INSERT INTO user_connect(user_id) VALUES (?)";
    private static final String SQL_SELECT_ALL = "SELECT * FROM (SELECT user_id, COUNT(*) FROM user_connect GROUP BY user_id) AS temp";
    private static final String ERROR_MESSAGE = "useiId должен быть целым числом в диапазоне от -2147483648 до 2147483647";

    public static void main(String[] args) throws Exception {

        Options options = new Options();
        CommandLine cmd;
        try {
            cmd = parseCmd(options, args);
        } catch (ParseException e) {
            LOGGER.error(e.getMessage());
            new HelpFormatter().printHelp("utility-name", options);
            System.exit(1);
            return;
        }

        try (Connection connection = DriverManager.getConnection(
                cmd.getOptionValue("db_url"),
                cmd.getOptionValue("user"),
                cmd.getOptionValue("password")
        )) {
            if (null != connection) {
                LOGGER.info("Connected to database");
            }

            new WebServer()

                    .post("/PING", (request, response) -> {

                        assert connection != null;

                        int userId;
                        try {
                            userId = Integer.valueOf(request.body());
                        } catch (NumberFormatException nfe) {
                            LOGGER.warn(ERROR_MESSAGE);
                            return ERROR_MESSAGE;
                        }

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
                    })

                    .get("/STAT", (request, response) -> {
                        assert connection != null;

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

                        return sb.toString();
                    })

                    .start();

        } catch (SQLException ex) {
            LOGGER.error(ex.toString(), ex);
        }
    }


    private static CommandLine parseCmd(Options options, String[] args) throws ParseException {
        Option optDbUrl = new Option("db", "db_url", true, "Database URL, example: jdbc:postgresql://127.0.0.1:5432/testdb");
        optDbUrl.setRequired(true);
        options.addOption(optDbUrl);

        Option optDbUser = new Option("u", "user", true, "Database user");
        optDbUser.setRequired(true);
        options.addOption(optDbUser);

        Option optDbPass = new Option("p", "password", true, "Database password");
        optDbPass.setRequired(true);
        options.addOption(optDbPass);

        return new DefaultParser().parse(options, args);
    }
}
