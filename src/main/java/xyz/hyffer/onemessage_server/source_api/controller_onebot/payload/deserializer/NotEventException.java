package xyz.hyffer.onemessage_server.source_api.controller_onebot.payload.deserializer;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonLocation;

public class NotEventException extends JacksonException {
    protected NotEventException(String msg) {
        super(msg);
    }

    @Override
    public JsonLocation getLocation() {
        return null;
    }

    @Override
    public String getOriginalMessage() {
        return null;
    }

    @Override
    public Object getProcessor() {
        return null;
    }
}
