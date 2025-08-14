package com.example.integradora_trackontract.modules.Password_Resets.control;
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

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class Password_ResetsService {

    private final Password_ResetsRepository passwordResetsRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // minutos de validez del token (puedes definirlo en application.properties)
    @Value("${application.security.password-reset.exp-minutes:30}")
    private long expMinutes;

    private String generateToken() {
        byte[] bytes = new byte[32];
        new SecureRandom().nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    /** Inserta un registro en password_resets */
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

        // (Opcional) invalida tokens pendientes
        var actives = passwordResetsRepository.findActiveByUserId(user.getId());
        // Si prefieres, bórralos o márcalos usados:
        // actives.forEach(pr -> pr.setUsed_at(LocalDateTime.now()));
        // passwordResetsRepository.saveAll(actives);

        String token = generateToken();

        Password_Resets pr = new Password_Resets();
        pr.setUser_id(user);
        pr.setToken(token);
        pr.setCreated_at(LocalDateTime.now());
        pr.setUsed_at(null);
        passwordResetsRepository.save(pr);

        // En producción NO devuelvas el token. Aquí lo retornamos para pruebas en Postman.
        return new ResponseEntity<>(
                new Message(token, "Solicitud creada (DEV: usa este token en /auth/password/reset)", TypesResponse.SUCCESS),
                HttpStatus.CREATED
        );
    }

    /** Consume el token, cambia contraseña y marca used_at */
    @Transactional
    public ResponseEntity<Message> resetPassword(String token, String newPassword) {
        var prOpt = passwordResetsRepository.findByToken(token);
        if (prOpt.isEmpty()) {
            return new ResponseEntity<>(
                    new Message("Token inválido", TypesResponse.ERROR),
                    HttpStatus.BAD_REQUEST
            );
        }
        var pr = prOpt.get();

        if (pr.getUsed_at() != null) {
            return new ResponseEntity<>(
                    new Message("Token ya utilizado", TypesResponse.WARNING),
                    HttpStatus.BAD_REQUEST
            );
        }

        if (pr.getCreated_at().plusMinutes(expMinutes).isBefore(LocalDateTime.now())) {
            return new ResponseEntity<>(
                    new Message("Token expirado", TypesResponse.WARNING),
                    HttpStatus.BAD_REQUEST
            );
        }

        var user = pr.getUser_id();
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setLogin_attempts(0);
        user.setStatus(true);
        userRepository.save(user);

        pr.setUsed_at(LocalDateTime.now());
        passwordResetsRepository.save(pr);

        return new ResponseEntity<>(
                new Message("Contraseña actualizada correctamente", TypesResponse.SUCCESS),
                HttpStatus.OK
        );
    }
}