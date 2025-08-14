package com.example.integradora_trackontract.config;

import com.example.integradora_trackontract.modules.Audit_Logs.control.Audit_LogsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class AuditInterceptor implements HandlerInterceptor {

    private final Audit_LogsService audit;
    private static final Set<String> WRITE = Set.of("POST","PUT","PATCH","DELETE");

    @Override
    public void afterCompletion(HttpServletRequest req, HttpServletResponse res,
                                Object handler, Exception ex) {
        String uri = req.getRequestURI();
        boolean isWrite = WRITE.contains(req.getMethod());
        boolean isAuth = uri.startsWith("/auth/");

        if (isWrite || isAuth) {
            String entity = uri.split("/")[1].toUpperCase();
            String action = inferAction(req.getMethod(), uri);
            audit.log(req, action, entity, null, null, res.getStatus());
        }
    }

    private String inferAction(String method, String uri) {
        if (uri.contains("/login")) return "LOGIN";
        if (uri.contains("/logout")) return "LOGOUT";
        if (uri.contains("/password/forgot")) return "FORGOT";
        if (uri.contains("/password/reset")) return "RESET";
        return switch (method) {
            case "POST" -> "CREATE";
            case "PUT", "PATCH" -> "UPDATE";
            case "DELETE" -> "DELETE";
            default -> "READ";
        };
    }
}