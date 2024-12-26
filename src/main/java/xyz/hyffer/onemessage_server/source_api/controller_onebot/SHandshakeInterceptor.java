package xyz.hyffer.onemessage_server.source_api.controller_onebot;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import xyz.hyffer.onemessage_server.model.Source;
import xyz.hyffer.onemessage_server.storage.SourceRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service("source_handshake_interceptor")
public class SHandshakeInterceptor implements HandshakeInterceptor {

    @Resource
    private SourceRepository sourceRepository;

    String VAR_NAME_PATTERN = "[a-zA-Z_][a-zA-Z0-9_]*";

    /**
     * Search for source in database
     * or add a new source
     *
     * @param name name of the source
     * @return _SID of the source
     */
    public synchronized int registerSource(String name) {
        Optional<Source> r = sourceRepository.findByName(name);
        if (r.isPresent()) {
            return r.get().get_SID();
        }
        Source newSrc = new Source(name);
        sourceRepository.saveAndFlush(newSrc);
        return newSrc.get_SID();
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        String url = request.getURI().toString();
        log.info("Received connection from {} to {}.", request.getRemoteAddress(), url);

        String name;

        // get source name from url
        Pattern urlPattern = Pattern.compile("/source/(" + VAR_NAME_PATTERN + ")$");
        Matcher urlMatcher = urlPattern.matcher(url);
        if (urlMatcher.find()) {
            name = urlMatcher.group(1);
        }

        // get source name from header
        else {
            List<String> values = request.getHeaders().get("authorization");
            if (values == null || values.isEmpty()) {
                log.info("Connection rejected: no identity information.");
                log.trace("Full headers: {}", request.getHeaders());
                return false;
            }
            String authorization = values.get(0);
            Pattern pattern = Pattern.compile("^Bearer (" + VAR_NAME_PATTERN + ")$");
            Matcher matcher = pattern.matcher(authorization);
            if (!matcher.matches()) {
                log.info("Connection rejected: invalid identity information \"{}\".", authorization);
                log.trace("Authorization headers: {}", request.getHeaders().get("authorization"));
                return false;
            }
            name = matcher.group(1);
        }

        int _SID = registerSource(name);
        request.getHeaders().set("_SID", String.valueOf(_SID));
        log.info("Connection accepted: source \"{}\" with _SID {}.", name, _SID);
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

    }
}
