package ru.capper.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.capper.test.dao.ConnectionPool;
import ru.capper.test.model.User;
import ru.capper.test.server.WebServer;
import ru.capper.test.service.UserService;
import ru.capper.test.service.UserServiceImpl;

import java.util.List;
import java.util.Map;

public class App {

    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

    private static final String SQL_SELECT_ALL = "SELECT * FROM (SELECT user_id, COUNT(*) FROM user_connect GROUP BY user_id) AS temp";

    public static void main(String[] args) throws Exception {
        new ConnectionPool();

        UserService us = new UserServiceImpl();

        new WebServer()

                .post("/PING", (request, response) -> {
                    int userId;
                    try {
                        userId = Integer.valueOf(request.body());
                    } catch (NumberFormatException nfe) {
                        LOGGER.warn("useiId должен быть целым числом в диапазоне от -2147483648 до 2147483647");
                        return "useiId должен быть целым числом в диапазоне от -2147483648 до 2147483647";
                    }
                    return "PONG " + us.incrementPong(userId).longValue();
                })

                .get("/STAT", (request, response) -> {
                    Map<String, List<String>> query = request.getQuery();

                    int size = Integer.valueOf(query.get("size").get(0));
                    int page = Integer.valueOf(query.get("page").get(0));

                    List<User> userList = us.getPage(page, size);

                    StringBuilder sb = new StringBuilder();
                    sb.append("[");

                    for (User user : userList) {
                        sb
                                .append("{\"userId\":\"")
                                .append(user.getId())
                                .append("\",\"times\":")
                                .append(user.getCountPong())
                                .append("},");
                    }
                    sb.deleteCharAt(sb.length() - 1);

                    sb.append("]");

                    return sb.toString();
                })

                .start();
    }
}
