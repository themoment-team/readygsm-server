package team.themoment.readygsm.global.security.jwt;


import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import team.themoment.readygsm.domain.user.data.constant.UserRole;

import java.security.Key;
import java.time.*;
import java.util.Date;

@Component
public class JwtProvider {

    private final Key secretKey;
    private final long expirationSeconds;

    public JwtProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration-seconds}") long expirationSeconds
    ) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
        this.expirationSeconds = expirationSeconds;
    }

    public String createToken(Long userId, UserRole role) {
        ZonedDateTime now = ZonedDateTime.now();
        Date issuedAt = Date.from(now.toInstant());
        Date expiration = Date.from(now.toInstant().plusSeconds(expirationSeconds));

        return Jwts.builder()
                .setSubject(userId.toString())
                .claim("role", role.name())
                .setIssuedAt(issuedAt)
                .setExpiration(expiration)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public Long getUserId(String token) {
        Claims claims = parseClaims(token);
        try {
            return Long.parseLong(claims.getSubject());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid userId format in token", e);
        }
    }

    public UserRole getRole(String token) {
        Claims claims = parseClaims(token);
        return UserRole.valueOf(claims.get("role", String.class));
    }

    public Date getTokenExpiration(String token) {
        return parseClaims(token).getExpiration();
    }

    public Date getTokenIssuedAt(String token) {
        return parseClaims(token).getIssuedAt();
    }

    public LocalDateTime getTokenExpirationLocalDateTime(String token) {
        return convertToLocalDateTime(getTokenExpiration(token));
    }

    public LocalDateTime getTokenIssuedAtLocalDateTime(String token) {
        return convertToLocalDateTime(getTokenIssuedAt(token));
    }

    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private LocalDateTime convertToLocalDateTime(Date date) {
        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    public long getExpirationMinutes() {
        return expirationSeconds / 60;
    }
}
