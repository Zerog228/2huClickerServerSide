package me.zink.clicker.security.service;

import jakarta.servlet.http.HttpServletRequest;
import me.zink.clicker.util.FailedLoginAttempt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class LoginAttemptService {
    private HashMap<String, FailedLoginAttempt> attempts = new HashMap<>();

    @Autowired
    private HttpServletRequest request;

    public LoginAttemptService() {}

    public void loginFailed(final String key) {
        FailedLoginAttempt failedLoginAttempt = new FailedLoginAttempt();
        attempts.put(key, failedLoginAttempt.addOrCreate(attempts.get(key), System.currentTimeMillis()));
    }

    public boolean isBlocked() {
        try {
            return attempts.get(getClientIP()).blocked(System.currentTimeMillis());
        } catch (Exception ignored) {
            return false;
        }
    }

    private String getClientIP() {
        final String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader != null) {
            return xfHeader.split(",")[0];
        }
        return request.getRemoteAddr();
    }
}