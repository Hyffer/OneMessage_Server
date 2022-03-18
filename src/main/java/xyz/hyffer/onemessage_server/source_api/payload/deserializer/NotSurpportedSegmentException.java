package xyz.hyffer.onemessage_server.source_api.payload.deserializer;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonLocation;

public class NotSurpportedSegmentException extends JacksonException {
    protected NotSurpportedSegmentException(String msg) {
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
