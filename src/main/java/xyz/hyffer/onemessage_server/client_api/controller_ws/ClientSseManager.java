package xyz.hyffer.onemessage_server.client_api.controller_ws;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@Component
public class ClientSseManager {

    public enum Type {
        FULL,
        INFO,
        INFO_EXT,
        REDUCED,
        REDUCED_EXT
    }

    private final ConcurrentHashMap<Type, ConcurrentLinkedQueue<SseEmitter>> SESSION_POOL = new ConcurrentHashMap<>();

    @PostConstruct
    void init() {
        for (Type type : Type.values()) {
            SESSION_POOL.put(type, new ConcurrentLinkedQueue<>());
        }
    }

    public void add(Type type, SseEmitter emitter) {
        SESSION_POOL.get(type).add(emitter);
    }

    public void remove(Type type, SseEmitter emitter) {
        SESSION_POOL.get(type).remove(emitter);
    }

    public int countOfType(Type type) {
        return SESSION_POOL.get(type).size();
    }

    public int countAll() {
        int count = 0;
        for (ConcurrentLinkedQueue<SseEmitter> emitters : SESSION_POOL.values()) {
            count += emitters.size();
        }
        return count;
    }

    public List<SseEmitter> getOfType(Type type) {
        return new LinkedList<>(SESSION_POOL.get(type));
    }

    public List<SseEmitter> getAll() {
        List<SseEmitter> all = new LinkedList<>();
        for (ConcurrentLinkedQueue<SseEmitter> emitters : SESSION_POOL.values()) {
            all.addAll(emitters);
        }
        return all;
    }

}
