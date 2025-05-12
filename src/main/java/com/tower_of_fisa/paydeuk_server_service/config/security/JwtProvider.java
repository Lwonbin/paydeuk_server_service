package com.tower_of_fisa.paydeuk_server_service.config.security;

import com.tower_of_fisa.paydeuk_server_service.domain.entity.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.security.Key;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtProvider {
  @Value("${jwt.secret-key}")
  private String rawKey;

  private Key secretKey;

  @PostConstruct
  public void init() {
    byte[] keyBytes = Decoders.BASE64.decode(rawKey);
    this.secretKey = Keys.hmacShaKeyFor(keyBytes);
  }

  public String generateAccessToken(User user) {
    return Jwts.builder()
        .setSubject(user.getUsername())
        .claim("role", user.getRole().name())
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 30)) // 30분
        .signWith(SignatureAlgorithm.HS256, secretKey)
        .compact();
  }

  public String generateRefreshToken(User user) {
    return Jwts.builder()
        .setSubject(user.getUsername())
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 7)) // 7일
        .signWith(SignatureAlgorithm.HS256, secretKey)
        .compact();
  }

  public String extractUsername(String token) {
    return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
  }

  public boolean isTokenExpired(String token) {
    Date expiration =
        Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getExpiration();
    return expiration.before(new Date());
  }

  public boolean validateRefreshToken(String token) {
    try {
      Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
      return !isTokenExpired(token);
    } catch (Exception e) {
      return false;
    }
  }
}
