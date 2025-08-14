package com.example.integradora_trackontract.modules.Password_Resets.control;
import com.example.integradora_trackontract.modules.Password_Resets.model.ForgotPasswordRequest;
import com.example.integradora_trackontract.modules.Password_Resets.model.PasswordResetRequest;
import com.example.integradora_trackontract.utils.Message;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/password")
public class Password_ResetsController {

    private final Password_ResetsService service;

    // Público
    @PostMapping("/forgot")
    public ResponseEntity<Message> forgot(@Valid @RequestBody ForgotPasswordRequest req) {
        return service.createReset(req.getEmail());
    }

    // Público
    @PostMapping("/reset")
    public ResponseEntity<Message> reset(@Valid @RequestBody PasswordResetRequest req) {
        return service.resetPassword(req.getToken(), req.getNewPassword());
    }
}