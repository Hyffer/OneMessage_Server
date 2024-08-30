package xyz.hyffer.onemessage_server.client_api.service.cs_api;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class ClientException extends Exception {

    public enum Type {
        AUTH_FAILED,
        FORMAT_INCORRECT,
        UNEXPECTED_VALUE,
        NOT_IMPLEMENT,
        INTERNAL_ERROR
    }

    int code;
    String msg;

    /**
     * Raise a ClientException based on the caught exception.
     * @param type exception type
     * @param cause caught exception
     */
    public ClientException(Type type, String readable, Throwable cause) {
        super(readable, cause);
        set(type);
    }

    /**
     * Raise a ClientException.
     * @param type exception type
     * @param readable error message
     */
    public ClientException(Type type, String readable) {
        super(readable);
        set(type);
    }

    private void set(Type type) {
        switch (type) {
            case AUTH_FAILED -> {
                this.code = 401;
                this.msg = "Authentication failed.";
            }
            case FORMAT_INCORRECT -> {
                this.code = 400;
                this.msg = "Request format incorrect.";
            }
            case UNEXPECTED_VALUE -> {
                this.code = 422;
                this.msg = "Unexpected value.";
            }
            case NOT_IMPLEMENT -> {
                this.code = 501;
                this.msg = "Not implemented yet.";
            }
            default -> {
                this.code = 500;
                this.msg = "Internal error.";
            }
        }

        log.error("Raise ClientException {}({}): {}", type.name(), this.code, this.msg);
        log.error("Details: ", this);
    }
}
