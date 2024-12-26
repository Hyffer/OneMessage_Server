package xyz.hyffer.onemessage_server.source_api.controller_onebot;

import org.springframework.stereotype.Component;
import xyz.hyffer.onemessage_server.source_api.service.SourceHandler;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class SourceHandlerManager {

    private static final ConcurrentHashMap<Integer, SourceHandler> HANDLER_POOL = new ConcurrentHashMap<>();

    public static void put(int _SID, SourceHandler handler) {
        HANDLER_POOL.put(_SID, handler);
    }

    public static void remove(int _SID) {
        HANDLER_POOL.remove(_SID);
    }

    public static SourceHandler get(int _SID) {
        return HANDLER_POOL.get(_SID);
    }

}
