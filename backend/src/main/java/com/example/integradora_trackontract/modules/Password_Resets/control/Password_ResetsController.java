package com.example.integradora_trackontract.modules.Password_Resets.control;
import com.example.integradora_trackontract.modules.Password_Resets.model.ForgotPasswordRequest;
import com.example.integradora_trackontract.modules.Password_Resets.model.PasswordResetRequest;
import com.example.integradora_trackontract.modules.Password_Resets.model.ValidateTokenRequest;
import com.example.integradora_trackontract.utils.Message;
import com.example.integradora_trackontract.utils.TypesResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/password")
public class Password_ResetsController {


    private final Password_ResetsService service;

    // Endpoint para solicitar reset
    @PostMapping("/forgot")
    public ResponseEntity<Message> forgot(@Valid @RequestBody ForgotPasswordRequest req) {
        return service.createReset(req.getEmail());
    }

    // Endpoint para resetear contraseña
    @PostMapping("/reset")
    public ResponseEntity<?> reset(@Valid @RequestBody PasswordResetRequest req) {
        return service.resetPassword(req.getToken(), req.getNewPassword());
    }

    // Endpoint para validar token de recuperación
    @PostMapping("/validate-recovery-token")
    public ResponseEntity<Message> validateToken(@Valid @RequestBody ValidateTokenRequest req) {
        String token = req.getToken();

        var prOpt = service.findByToken(token);

        if (prOpt.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new Message("Token inválido", TypesResponse.ERROR));
        }

        var pr = prOpt.get();

        if (pr.getUsed_at() != null) {
            return ResponseEntity.badRequest()
                    .body(new Message("Token ya utilizado", TypesResponse.WARNING));
        }

        if (pr.getCreated_at().plusMinutes(service.getExpMinutes()).isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest()
                    .body(new Message("Token expirado", TypesResponse.WARNING));
        }

        // Devuelve info mínima del usuario asociado al token
        return ResponseEntity.ok(
                new Message(pr.getUser_id(), "Token válido", TypesResponse.SUCCESS)
        );
    }
}