package xyz.hyffer.onemessage_server.client_api.controller_ws;

import com.fasterxml.jackson.annotation.JsonView;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import xyz.hyffer.onemessage_server.client_api.service.ClientService;
import xyz.hyffer.onemessage_server.client_api.service.cs_api.ClientException;
import xyz.hyffer.onemessage_server.client_api.service.cs_api.ClientRequestBody;
import xyz.hyffer.onemessage_server.client_api.service.cs_api.ClientResponse;

/**
 * RequestBody is deserialized by {@link xyz.hyffer.onemessage_server.client_api.service.cs_api.ClientRequestBodyDeserializer}.
 * So if deserialization succeeds, the payload is guaranteed to be valid.
 */
@Slf4j
@Controller
@RequestMapping("/app")
public class ClientController {

    @Resource
    private ClientService clientService;

    @GetMapping("/get_contacts")
    @ResponseBody
    @JsonView(ClientResponse.NoStatusCodeView.class)
    public ClientResponse.GetContacts getContacts(HttpServletRequest request, HttpServletResponse response,
                                                  @RequestBody ClientRequestBody.GetContacts payload) {
        logTrace(request, payload);
        ClientResponse.GetContacts clientResp = clientService.getContacts(payload);
        response.setStatus(clientResp.getCode());
        return clientResp;
    }

    @ExceptionHandler({ClientException.class, RuntimeException.class})
    @ResponseBody
    @JsonView(ClientResponse.NoStatusCodeView.class)
    public ClientResponse.Error handleException(HttpServletRequest request, HttpServletResponse response, Exception _e) {
        ClientException ex;
        if (_e instanceof ClientException e) {
            ex = e;
        } else if (_e.getCause() instanceof ClientException e) {
            // ClientException wrapped in unchecked exception
            ex = e;
        } else {
            ex = new ClientException(ClientException.Type.INTERNAL_ERROR, "Unexpected unchecked exception", _e);
        }
        logTrace(request, ex);

        ClientResponse.Error errorResp = new ClientResponse.Error(ex);
        response.setStatus(errorResp.getCode());
        return errorResp;
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    @JsonView(ClientResponse.NoStatusCodeView.class)
    public ClientResponse.Error handleExceptionFallback(HttpServletRequest request, HttpServletResponse response, Exception e) {
        // fallback
        ClientException ex = new ClientException(ClientException.Type.INTERNAL_ERROR, "Unexpected exception", e);
        logTrace(request, ex);

        ClientResponse.Error errorResp = new ClientResponse.Error(ex);
        response.setStatus(errorResp.getCode());
        return errorResp;
    }

    void logTrace(HttpServletRequest request, ClientRequestBody payload) {
        log.trace("{} {} - {}", request.getMethod(), request.getRequestURI(), payload);
    }

    void logTrace(HttpServletRequest request, ClientException e) {
        log.trace("Exception caused by: {} {}", request.getMethod(), request.getRequestURI());
    }
}
