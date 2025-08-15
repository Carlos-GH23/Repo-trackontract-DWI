package com.example.integradora_trackontract.modules.Password_Resets.control;
import com.example.integradora_trackontract.config.EmailService;
import com.example.integradora_trackontract.modules.Password_Resets.model.*;
import com.example.integradora_trackontract.modules.User.model.User;
import com.example.integradora_trackontract.modules.User.model.UserRepository;
import com.example.integradora_trackontract.utils.Message;
import com.example.integradora_trackontract.utils.TypesResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.integradora_trackontract.auth.service.JwtService;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class Password_ResetsService {

    private final Password_ResetsRepository passwordResetsRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final JwtService jwtService;


    // minutos de validez del token
    @Value("${application.security.password-reset.exp-minutes:30}")
    private long expMinutes;

    // URL base del frontend para redirección
    @Value("${application.frontend.reset-url:http://localhost:5173/reset-password}")
    private String frontendResetUrl;

    private String generateToken() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder token = new StringBuilder(5);
        for (int i = 0; i < 5; i++) {
            token.append(chars.charAt(random.nextInt(chars.length())));
        }
        return token.toString();
    }


    /** Inserta un registro en password_resets y envía correo */
    @Transactional
    public ResponseEntity<Message> createReset(String email) {
        var userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return new ResponseEntity<>(
                    new Message("No existe usuario con email " + email, TypesResponse.WARNING),
                    HttpStatus.NOT_FOUND
            );
        }
        User user = userOpt.get();

        // (Opcional) invalidar tokens previos
        var actives = passwordResetsRepository.findActiveByUserId(user.getId());
        // actives.forEach(pr -> pr.setUsed_at(LocalDateTime.now()));
        // passwordResetsRepository.saveAll(actives);

        String token = generateToken();

        Password_Resets pr = new Password_Resets();
        pr.setUser_id(user);
        pr.setToken(token);
        pr.setCreated_at(LocalDateTime.now());
        pr.setUsed_at(null);
        passwordResetsRepository.save(pr);

        // Generar link para frontend
        String resetLink = frontendResetUrl + "?token=" + token;

        // Enviar email
        // Enviar email con HTML más atractivo
        // Dentro de tu Password_ResetsService, reemplaza el HTML del email:
        String htmlContent = """
<html>
<body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333;">
    <h2 style="color: #4CAF50;">Hola %s,</h2>
    <p>Hemos recibido una solicitud para restablecer tu contraseña.</p>
    <p>
        Usa el siguiente <strong>código de verificación</strong> para continuar:
    </p>
    <p style="text-align: center; font-size: 24px; font-weight: bold; color: #4CAF50; margin: 20px 0;">
        %s
    </p>
    <p>Este código expirará en %d minutos.</p>
    <hr>
    <p style="font-size: 0.9em; color: #555;">
        Si no solicitaste este cambio, ignora este mensaje.
    </p>
</body>
</html>
""".formatted(user.getName(), token, expMinutes);

        emailService.sendEmail(email, "Código de recuperación de contraseña", htmlContent);



        return new ResponseEntity<>(
                new Message("Se ha enviado un enlace de restablecimiento a tu correo.", TypesResponse.SUCCESS),
                HttpStatus.CREATED
        );
    }

    /** Consume el token, cambia contraseña y marca used_at */
    @Transactional
    public ResponseEntity<?> resetPassword(String token, String newPassword) {
        var prOpt = passwordResetsRepository.findByToken(token);
        if (prOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(new Message("Token inválido", TypesResponse.ERROR));
        }
        var pr = prOpt.get();

        if (pr.getUsed_at() != null) {
            return ResponseEntity.badRequest().body(new Message("Token ya utilizado", TypesResponse.WARNING));
        }

        if (pr.getCreated_at().plusMinutes(expMinutes).isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body(new Message("Token expirado", TypesResponse.WARNING));
        }

        var user = pr.getUser_id();
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setLogin_attempts(0);
        user.setStatus(true);
        userRepository.save(user);

        pr.setUsed_at(LocalDateTime.now());
        passwordResetsRepository.save(pr);

        // Generar token JWT para que el front lo use directamente
        String jwtToken = jwtService.generateToken(user);

        // Devolver el token y mensaje
        return ResponseEntity.ok(new ResetPasswordResponse("Contraseña actualizada correctamente", jwtToken));
    }

    public Optional<Password_Resets> findByToken(String token) {
        return passwordResetsRepository.findByToken(token);
    }

    public long getExpMinutes() {
        return expMinutes;
    }
}
