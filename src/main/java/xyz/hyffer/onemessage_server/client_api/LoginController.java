package xyz.hyffer.onemessage_server.client_api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@EnableAutoConfiguration
public class LoginController {

    @Value("${auth.username}")
    private String USERNAME;

    @Value("${auth.password}")
    private String PASSWORD;

    @Value("${allowed-origins}")
    private String[] ALLOW_ORIGINS;

    @RequestMapping(value = "/login", method = {RequestMethod.POST})
    public String login(HttpServletRequest request, HttpServletResponse response,
                        @RequestParam(required = false) String username,
                        @RequestParam(required = false) String password) {
        if (request.getSession().getAttribute("hasLogin") != null) {
            return "Has Already Login.";
        }
        if (username != null && password != null &&
                username.equals(USERNAME) && password.equals(PASSWORD)) {
            request.getSession().setAttribute("hasLogin", true);
            return "Login Successfully.";
        }
        response.setStatus(403);
        return "Login Failed.";
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/login")
                        .allowedOriginPatterns(ALLOW_ORIGINS)
                        .allowCredentials(true);
            }
        };
    }

}
