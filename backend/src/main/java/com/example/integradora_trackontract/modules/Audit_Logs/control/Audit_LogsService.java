package com.example.integradora_trackontract.modules.Audit_Logs.control;

import com.example.integradora_trackontract.modules.Audit_Logs.model.Audit_Logs;
import com.example.integradora_trackontract.modules.Audit_Logs.model.Audit_LogsRepository;
import com.example.integradora_trackontract.modules.User.model.User;
import com.example.integradora_trackontract.modules.User.model.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class Audit_LogsService {

    private final Audit_LogsRepository repo;
    private final UserRepository userRepo;

    public void log(HttpServletRequest req,
                    String action,
                    String entityName,
                    Long entityId,
                    String details,
                    Integer status) {

        User user = null;
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            user = userRepo.findByEmail(auth.getName()).orElse(null);
        }

        Audit_Logs log = new Audit_Logs();
        log.setUser_id(user);
        log.setAction(action);
        log.setEntityName(entityName);
        log.setEntityId(entityId);
        log.setDetails(details);
        log.setMethod(req.getMethod());
        log.setPath(req.getRequestURI());
        log.setIp(req.getRemoteAddr());
        log.setUserAgent(req.getHeader("User-Agent"));
        log.setStatus(status);

        repo.save(log);
    }
}
