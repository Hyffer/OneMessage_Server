package xyz.hyffer.onemessage_server.source_api.controller_onebot;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import xyz.hyffer.onemessage_server.model.Source;
import xyz.hyffer.onemessage_server.source_api.service.SourceHandler;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SourceHandlerManager {

    private ArrayList<Source> sources;

    private static final ConcurrentHashMap<Integer, SourceHandler> HANDLER_POOL = new ConcurrentHashMap<>();

    @PostConstruct
    public void loadSourceConfig() {
//        sources = (ArrayList<Source>) sourceMapper.getSources();
    }

    /**
     * Search for source in database
     * or add a new source
     * @param name name of the source
     * @return _SID of the source
     */
    public int registerSource(String name) {
        for (Source s : sources) {
            if (s.getName().equals(name))
                return s.get_SID();
        }
        Source newSrc = new Source(name);
//        sourceMapper.addSource(newSrc);
        sources.add(newSrc);
        return newSrc.get_SID();
    }

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
