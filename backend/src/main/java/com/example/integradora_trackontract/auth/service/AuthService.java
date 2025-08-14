package com.example.integradora_trackontract.auth.service;

import com.example.integradora_trackontract.modules.User.model.User;
import com.example.integradora_trackontract.modules.User.model.UserRepository;
import com.example.integradora_trackontract.auth.controller.LoginRequest;
import com.example.integradora_trackontract.auth.controller.RegisterRequest;
import com.example.integradora_trackontract.auth.controller.TokenResponse;
import com.example.integradora_trackontract.auth.controller.LoginResponse;
import com.example.integradora_trackontract.auth.repository.Token;
import com.example.integradora_trackontract.auth.repository.TokenRespository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final TokenRespository tokenRespository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public TokenResponse register(RegisterRequest request){
        var user = User.builder()
                .name(request.name())
                .email(request.email())
                .lastName(request.lastName())
                .phoneNumber(request.phoneNumber())
                .status(request.status() != null ? request.status() : true)
                .created_at(request.createdAt() != null ? request.createdAt().toLocalDateTime() : null)
                .updated_at(request.updatedAt() != null ? request.updatedAt().toLocalDateTime() : null)
                .login_attempts(0)
                .password(passwordEncoder.encode(request.password()))
                .build();
        var savedUser = userRepository.save(user);
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        savedUserToken(savedUser, jwtToken);
        return new TokenResponse(jwtToken, refreshToken);
    }

    public LoginResponse login(LoginRequest request){
        //Recuperar el usuario
        User user = userRepository.findByEmail(request.email())
                        .orElseThrow(() -> new UsernameNotFoundException("No existe usuario con email " + request.email()));

        LocalDateTime now = LocalDateTime.now();

        // Si estaba bloqueado, verificar si ya venció el bloqueo
        if (user.getLocked_until() != null) {
            if (now.isBefore(user.getLocked_until())) {
                long minutesLeft = Duration.between(now, user.getLocked_until()).toMinutes();
                throw new LockedException("La cuenta está bloqueada. Intenta en " + minutesLeft + " minuto(s).");
            } else {
                // auto-desbloqueo
                user.setLocked_until(null);
                user.setStatus(true);
                user.setLogin_attempts(0);
                userRepository.save(user);
            }
        }

        try {
            //Se intenta autentificar
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.email(),
                            request.password()
                    )
            );
        } catch (BadCredentialsException ex) {
            // incrementar intentos
            int intentos = user.getLogin_attempts() + 1;
            user.setLogin_attempts(intentos);

            // bloquear al llegar a 3
            if (intentos >= 3){
                user.setStatus(false);
                user.setLocked_until(now.plusMinutes(30));
            }
            userRepository.save(user);

            if (user.getLocked_until() != null && now.isBefore(user.getLocked_until())) {
                long minutesLeft = Duration.between(now, user.getLocked_until()).toMinutes();
                throw new LockedException("Cuenta bloqueada por intentos fallidos. Intenta de nuevo en " + minutesLeft + " minuto(s).");
            }
            throw ex;
        }

        // si autenticacion exitosa: reiniciamos contador de intentos
        if (user.getLogin_attempts() > 0 || user.getLocked_until() != null || !user.isStatus()){
            user.setLogin_attempts(0);
            user.setLocked_until(null);
            user.setStatus(true);
            userRepository.save(user);
        }

        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        revokeAllUserTokens(user);
        savedUserToken(user, jwtToken);
        
        // Crear LoginResponse con token y información del usuario
        var userInfo = new LoginResponse.UserInfo(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getRol_id().getName()
        );
        
        return new LoginResponse(jwtToken, userInfo);
    }

    public void savedUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(Token.TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRespository.save(token);
    }

    public void revokeAllUserTokens(final User user){
        final List<Token> validUserTokens = tokenRespository.findAllValidIsFalseOrRevokedIsFalseByUserId(user.getId());
        if(!validUserTokens.isEmpty()){
            for (final Token token : validUserTokens) {
                token.setExpired(true);
                token.setRevoked(true);
            }
            tokenRespository.saveAll(validUserTokens);
        }
    }

    public TokenResponse refreshToken(final String authHeader){
        if(authHeader == null || !authHeader.startsWith("Bearer ")){
            throw new IllegalArgumentException("Invalid Bearer token");
        }

        final String refreshToken = authHeader.substring(7);
        final String userEmail = jwtService.extractUsername(refreshToken);

        if(userEmail == null){
            throw new IllegalArgumentException("Invalid Refresh token");
        }

        final User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException(userEmail));

        if(!jwtService.isTokenValid(refreshToken, user)){
            throw new IllegalArgumentException("Invalid Refresh token");
        }

        final String accessToken = jwtService.generateToken(user);
        revokeAllUserTokens(user);
        savedUserToken(user, accessToken);
        return new TokenResponse(accessToken, refreshToken);
    }

    public void logout(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            revokeToken(token);
        }
    }

    private void revokeToken(String token) {
        var storedToken = tokenRespository.findByToken(token)
                .orElse(null);
        if (storedToken != null) {
            storedToken.setExpired(true);
            storedToken.setRevoked(true);
            tokenRespository.save(storedToken);
        }
    }
}
