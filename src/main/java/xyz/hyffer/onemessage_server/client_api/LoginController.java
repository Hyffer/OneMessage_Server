package xyz.hyffer.onemessage_server.client_api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@EnableAutoConfiguration
public class LoginController {

    @Value("${auth.username}")
    private String USERNAME;

    @Value("${auth.password}")
    private String PASSWORD;

    @RequestMapping(value = "/login", method = {RequestMethod.POST})
    @CrossOrigin(value = "http://localhost:3000", allowCredentials = "true")
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

}
