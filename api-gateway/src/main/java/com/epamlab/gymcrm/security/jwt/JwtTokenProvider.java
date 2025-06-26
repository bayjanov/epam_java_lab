package com.epamlab.gymcrm.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private static final String SECRET = "k6a/62x0yKGBnpOeD8dHiXZ3Y1OD1MwbMGEulC6J9no=";
    private final Key secretKey;
    private final long expirationInMinutes;

    public JwtTokenProvider (
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expriation:3600000}") long expirationInMinutes // 1 hour default
    ) {
        this.secretKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secret));
        this.expirationInMinutes = expirationInMinutes;
    }

    public SecretKey getSigningKey() {
        byte[] bytes = Base64.getDecoder().decode(SECRET);
        return Keys.hmacShaKeyFor(bytes);        // <-- io.jsonwebtoken.security.Keys
    }

    public String generateToken(String username) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationInMinutes);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    boolean validateToken(String token) {
        try{
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }


    public String generateInternalServiceToken() {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(5 * 60); // TLL

        return Jwts.builder()
                .setSubject("internal-service")
                .claim("scope", "intsvc")
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(exp))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }
}
