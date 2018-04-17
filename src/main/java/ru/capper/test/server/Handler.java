package ru.capper.test.server;

public interface Handler {

    Object handle(Request request, Response response) throws Exception;

}
