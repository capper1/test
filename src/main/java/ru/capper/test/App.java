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
                    int page = getIntQuery(query, "page");
                    if (page < 1) page = 1;
                    int size = getIntQuery(query, "size");
                    if (size < 1) size = 10;
                    return toJson(us.getPage(page, size));
                })

                .start();
    }

    private static int getIntQuery(Map<String, List<String>> query, String nameIntQuery) {
        if (query != null && query.size() > 0) {
            try {
                if (query.get(nameIntQuery) != null && query.get(nameIntQuery).size() > 0)
                    return Integer.valueOf(query.get(nameIntQuery).get(0));
            } catch (NumberFormatException | IndexOutOfBoundsException e) {
                LOGGER.warn(e.getMessage(), e);
            }
        }
        return -1;
    }

    private static String toJson(List<User> userList) {
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
        if (sb.length() > 1) sb.deleteCharAt(sb.length() - 1);
        sb.append("]");
        return sb.toString();
    }
}
