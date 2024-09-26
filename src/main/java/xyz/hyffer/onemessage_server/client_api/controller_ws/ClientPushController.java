package xyz.hyffer.onemessage_server.client_api.controller_ws;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import xyz.hyffer.onemessage_server.client_api.service.cs_api.ClientException;
import xyz.hyffer.onemessage_server.client_api.service.cs_api.ClientNotification;

@Slf4j
@Controller
@RequestMapping("/app")
public class ClientPushController {

    @Resource
    ClientSseManager clientSseManager;

    @GetMapping("/notification/{type}")
    public SseEmitter register(HttpServletRequest request, HttpServletResponse response,
                               @PathVariable String type) throws ClientException {
        log.trace("{} {}", request.getMethod(), request.getRequestURI());
        ClientSseManager.Type registrationType;
        try {
            registrationType = ClientSseManager.Type.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }

        SseEmitter emitter = new SseEmitter();
        emitter.onCompletion(() -> clientSseManager.remove(registrationType, emitter));
        emitter.onTimeout(emitter::complete);
        emitter.onError((e) -> {
            log.error("SSE emitter error: ", e);
            emitter.complete();
        });
        clientSseManager.add(registrationType, emitter);
        return emitter;
    }

    public void pushNotification(ClientNotification notification) {
        log.trace("Pushing notification {}", notification);
        for (SseEmitter emitter : clientSseManager.getAll()) {
            try {
                emitter.send(notification);
            } catch (Exception e) {
                log.error("Failed to push notification to client: ", e);
            }
        }
    }
}
