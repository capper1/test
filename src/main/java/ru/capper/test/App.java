package ru.capper.test;

import org.apache.commons.cli.*;
import ru.capper.test.server.WebServer;

import java.sql.*;

public class App {

    private static final String JDBC_DRIVER = "org.postgresql.Driver";
    private static final String SQL_SELECT = "SELECT COUNT(*) FROM user_connect";
    private static final String SQL_INSERT = "INSERT INTO user_connect(user_id) VALUES (?)";

    public static void main(String[] args) throws Exception {
        Options options = new Options();
        CommandLine cmd;
        try {
            cmd = parseCmd(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("utility-name", options);
            System.exit(1);
            return;
        }

        Class.forName(JDBC_DRIVER);

        try (Connection connection = DriverManager.getConnection(
                cmd.getOptionValue("db_url"),
                cmd.getOptionValue("user"),
                cmd.getOptionValue("password")
        )) {
            if (null != connection) {
                System.out.println("Connected to database");
            }

            Connection finalConnection = connection;
            new WebServer()

                    .post("/PING", (request, response) -> {

                        assert finalConnection != null;
                        PreparedStatement preparedStatement = finalConnection.prepareStatement(SQL_INSERT);
                        if (null != preparedStatement) {
                            preparedStatement.setInt(1, Integer.valueOf(request.body()));
                            preparedStatement.executeUpdate();
                        }

                        ResultSet rs = finalConnection.createStatement().executeQuery(SQL_SELECT);
                        int i = 0;
                        if (rs.next()) {
                            i = rs.getInt(1);
                        }
                        return request.body() + " = " + i;
                    })

                    .get("/STAT", (request, response) -> {
                        return "What is this? " + request.body();
                    })

                    .start();

        } catch (SQLException ex) {
            ex.printStackTrace();
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
