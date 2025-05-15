package com.tower_of_fisa.paydeuk_server_service.config.security;

import com.tower_of_fisa.paydeuk_server_service.domain.entity.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.security.Key;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtProvider {

  @Value("${jwt.secret-key}")
  private String rawKey;

  private final RedisTemplate<String, Object> redisTemplate;
  private Key secretKey;

  private static final String BLACKLIST_PREFIX = "blacklist:";
  private static final String REFRESH_TOKEN_PREFIX = "refresh_token:";

  @PostConstruct
  public void init() {
    byte[] keyBytes = Decoders.BASE64.decode(rawKey);
    this.secretKey = Keys.hmacShaKeyFor(keyBytes);
  }

  /**
   * 액세스 토큰 생성
   *
   * @param user 사용자 정보
   * @return 생성된 액세스 토큰
   */
  public String generateAccessToken(User user) {
    return Jwts.builder()
        .setSubject(user.getUsername())
        .claim("role", user.getRole().name())
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 30)) // 30분
        .signWith(SignatureAlgorithm.HS256, secretKey)
        .compact();
  }

  /**
   * 리프레시 토큰 생성
   *
   * @param user 사용자 정보
   * @return 생성된 리프레시 토큰
   */
  public String generateRefreshToken(User user) {
    String refreshToken =
        Jwts.builder()
            .setSubject(user.getUsername())
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 7)) // 7일
            .signWith(SignatureAlgorithm.HS256, secretKey)
            .compact();

    // Redis에 RefreshToken 저장
    String key = REFRESH_TOKEN_PREFIX + user.getId();
    redisTemplate.opsForValue().set(key, refreshToken, 7, TimeUnit.DAYS);

    return refreshToken;
  }

  /**
   * 토큰에서 사용자 이름 추출
   *
   * @param token JWT 토큰
   * @return 사용자 이름
   */
  public String extractUsername(String token) {
    return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
  }

  /**
   * 토큰 만료 여부 확인
   *
   * @param token JWT 토큰
   * @return true이면 만료됨, false이면 만료되지 않음
   */
  public boolean isTokenExpired(String token) {
    Date expiration =
        Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getExpiration();
    return expiration.before(new Date());
  }

  /**
   * 액세스 토큰 검증
   *
   * @param token JWT 토큰
   * @return true이면 유효함, false이면 유효하지 않음
   */
  public boolean validateAccessToken(String token) {
    try {
      // 블랙리스트 체크
      if (isBlacklisted(token)) {
        return false;
      }

      Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
      return !isTokenExpired(token);
    } catch (Exception e) {
      return false;
    }
  }

  /**
   * 리프레시 토큰 검증
   *
   * @param token JWT 토큰
   * @return true이면 유효함, false이면 유효하지 않음
   */
  public boolean validateRefreshToken(String token) {
    try {
      Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
      return !isTokenExpired(token);
    } catch (Exception e) {
      return false;
    }
  }

  /**
   * Redis에 저장된 RefreshToken과 클라이언트의 RefreshToken이 일치하는지 검증
   *
   * @param userId 사용자 ID
   * @param refreshToken 검증할 RefreshToken
   * @return boolean 토큰 일치 여부
   */
  public boolean validateStoredRefreshToken(Long userId, String refreshToken) {
    String key = REFRESH_TOKEN_PREFIX + userId;
    String storedToken = (String) redisTemplate.opsForValue().get(key);
    return refreshToken.equals(storedToken);
  }

  /**
   * 블랙리스트에 토큰 추가
   *
   * @param token JWT 토큰
   */
  public void addToBlacklist(String token) {
    String key = BLACKLIST_PREFIX + token;
    Date expiration =
        Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getExpiration();

    long ttl = expiration.getTime() - System.currentTimeMillis();
    if (ttl > 0) {
      redisTemplate.opsForValue().set(key, "blacklisted", ttl, TimeUnit.MILLISECONDS);
    }
  }

  /**
   * Redis에 저장된 블랙리스트 체크
   *
   * @param token JWT 토큰
   * @return true이면 블랙리스트에 있음, false이면 블랙리스트에 없음
   */
  public boolean isBlacklisted(String token) {
    String key = BLACKLIST_PREFIX + token;
    return redisTemplate.hasKey(key);
  }

  /**
   * Redis에 저장된 RefreshToken을 삭제
   *
   * @param userId 사용자 ID
   */
  public void removeRefreshToken(Long userId) {
    String key = REFRESH_TOKEN_PREFIX + userId;
    redisTemplate.delete(key);
  }
}
