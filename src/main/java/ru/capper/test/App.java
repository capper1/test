package ru.capper.test;

import ru.capper.test.server.WebServer;

public class App {
    public static void main(String[] args) throws Exception {
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
