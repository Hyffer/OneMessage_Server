package xyz.hyffer.onemessage_server.source_api;

import xyz.hyffer.onemessage_server.source_api.service.SourceHandler;

import java.util.concurrent.ConcurrentHashMap;

public class SourceHandlerManager {

    private static final ConcurrentHashMap<String, SourceHandler> HANDLER_POOL = new ConcurrentHashMap<>();

    public static void put(String name, SourceHandler handler) {
        HANDLER_POOL.put(name, handler);
    }

    public static void remove(String name) {
        HANDLER_POOL.remove(name);
    }

    public static SourceHandler get(String name) {
        return HANDLER_POOL.get(name);
    }

}
