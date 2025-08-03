package com.example.integradora_trackontract.auth.service;

import com.example.integradora_trackontract.modules.User.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.*;

@Service
public class JwtService {

    @Value("${application.security.jwt.secret-key}")
    private String secretKey;
    @Value("${application.security.jwt.expiration-time}")
    private long jwtExpiration;
    @Value("${application.security.jwt.refresh-token-expiration-time}")
    private long refreshExpiration;


    public String extractUsername(final String token) {
        final Claims jwtToken = Jwts.parser()
                .verifyWith(getSingInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return jwtToken.getSubject();
    }

    public String generateToken(final User user) {
        return  buildToken(user, jwtExpiration);
    }

    public String generateRefreshToken(final User user){
        return buildToken(user, refreshExpiration);
    }

    public String buildToken(final User user, final long expiration){
        Map<String, Object> claims = new HashMap<>();
        claims.put("name", user.getName());
        claims.put("roles", Collections.singletonList(
                user.getRol_id().getName()
        ));
        return Jwts.builder()
                .setId(user.getId().toString())
                .setClaims(claims)
                //.claims(Map.of("name", user.getName()))
                .setSubject(user.getEmail())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSingInKey())
                .compact();

    }

    public boolean isTokenValid(final String token, final User user) {
        final String username = extractUsername(token);
        return (username.equals(user.getEmail()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(final String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(final String token) {
        final Claims jwtToken = Jwts.parser()
                .verifyWith(getSingInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return jwtToken.getExpiration();
    }

    public SecretKey getSigningKey() {
        return getSingInKey();
    }

    private SecretKey getSingInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
