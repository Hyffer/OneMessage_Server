package xyz.hyffer.onemessage_server.client_api.controller_ws;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@EnableAutoConfiguration
public class AuthController {

    @Value("${om.auth.username}")
    private String USERNAME;

    @Value("${om.auth.password}")
    private String PASSWORD;

    @Value("${om.allowed-origins}")
    private String[] ALLOW_ORIGINS;

    @RequestMapping(value = "/auth", method = {RequestMethod.POST})
    public String auth(HttpServletRequest request, HttpServletResponse response,
                        @RequestParam(required = false) String username,
                        @RequestParam(required = false) String password) {
        if (request.getSession().getAttribute("Auth") != null) {
            return null;
        }
        if (username != null && password != null &&
                username.equals(USERNAME) && password.equals(PASSWORD)) {
            request.getSession().setAttribute("Auth", true);
            return null;
        }
        response.setStatus(403);
        return "Authenticate failed.";
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/auth")
                        .allowedOriginPatterns(ALLOW_ORIGINS)
                        .allowCredentials(true);
            }
        };
    }

}
