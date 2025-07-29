package com.example.integradora_trackontract.auth.service;

import com.example.integradora_trackontract.modules.User.model.User;
import com.example.integradora_trackontract.modules.User.model.UserRepository;
import com.example.integradora_trackontract.auth.controller.LoginRequest;
import com.example.integradora_trackontract.auth.controller.RegisterRequest;
import com.example.integradora_trackontract.auth.controller.TokenResponse;
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

    public TokenResponse login(LoginRequest request){
        //Recuperar el usuario
        User user = userRepository.findByEmail(request.email())
                        .orElseThrow(() -> new UsernameNotFoundException("No existe usuario con email " + request.email()));

        //Mandar LockedException cuando esté bloqueado
        if (!user.isStatus()) {
            throw new LockedException("Cuenta bloqueada por exceder el número de intentos.");
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
            }
            userRepository.save(user);

            // Rejected por credenciales o account locked (si alcanzó 3)
            if (!user.isStatus()){
                throw new LockedException("Cuenta bloqueada por 3 intentos fallidos");
            }
            throw ex;
        }

        // si autenticacion exitosa: reiniciamos contador de intentos
        if (user.getLogin_attempts() > 0){
            user.setLogin_attempts(0);
            userRepository.save(user);
        }

        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        revokeAllUserTokens(user);
        savedUserToken(user, jwtToken);
        return new TokenResponse(jwtToken, refreshToken);
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


}
