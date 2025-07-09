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
                .password(passwordEncoder.encode(request.password()))
                .build();
        var savedUser = userRepository.save(user);
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        savedUserToken(savedUser, jwtToken);
        return new TokenResponse(jwtToken, refreshToken);
    }

    public TokenResponse login(LoginRequest request){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );
        var user = userRepository.findByEmail(request.email())
                .orElseThrow();
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
