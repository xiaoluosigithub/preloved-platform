package com.example.secondhand.common;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;

public class JwtUtil {
  private static final long EXPIRATION_MS = 7L * 24 * 3600 * 1000;
  private static final Key KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);

  private static Key key() {
    return KEY;
  }

  public static String generateToken(Long userId) {
    return Jwts.builder()
            .setSubject(String.valueOf(userId))
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
            .signWith(key(), SignatureAlgorithm.HS256)
            .compact();
  }

  public static Long parseUserId(String token) {
    try {
      Claims claims = Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(token).getBody();
      String sub = claims.getSubject();
      return sub != null ? Long.parseLong(sub) : null;
    } catch (Exception e) {
      return null;
    }
  }
}