package ru.capper.test;

import org.apache.commons.cli.*;
import ru.capper.test.server.WebServer;

public class App {

    public static void main(String[] args) throws Exception {
        Options options = new Options();

        Option optDbUrl = new Option("db", "dburl", true, "DB_URL");
        optDbUrl.setRequired(true);
        options.addOption(optDbUrl);

        Option optDbUser = new Option("u", "user", true, "DB_USER");
        optDbUser.setRequired(true);
        options.addOption(optDbUser);

        Option optDbPass = new Option("p", "password", true, "DB_PASSWORD");
        optDbPass.setRequired(true);
        options.addOption(optDbPass);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("utility-name", options);
            System.exit(1);
            return;
        }

        String dbUrl = cmd.getOptionValue("dburl");
        String dbUser = cmd.getOptionValue("user");
        String dbPass = cmd.getOptionValue("password");

        System.out.println(dbUrl);
        System.out.println(dbUser);
        System.out.println(dbPass);

        new WebServer()

                .post("/PING", (request, response) -> {
                    return "Hello world: " + request.body();
                })

                .get("/STAT", (request, response) -> {
                    return "What is this? " + request.body();
                })

                .start();
    }
}
