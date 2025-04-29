package org.producr.api.config.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.producr.api.data.domain.user.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class JwtTokenUtil {
  @Value("${app.jwt.secret}")
  private String jwtSecret;

  @Value("${app.jwt.expiration}")
  private int jwtExpirationMs;

  public String generateToken(UserDetails userDetails) {
    Map<String, Object> claims = new HashMap<>();

    // Add roles to claims
    Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
    if (!authorities.isEmpty()) {
      claims.put("roles",
          authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));
    }

    if (userDetails instanceof UserPrincipal) {
      UserPrincipal userPrincipal = (UserPrincipal) userDetails;
      claims.put("userId", userPrincipal.getId());
      claims.put("email", userPrincipal.getEmail());
    }

    return doGenerateToken(claims, userDetails.getUsername());
  }

  public String generateTokenFromUser(User user) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("roles", Collections.singletonList("ROLE_" + user.getRole().name()));
    claims.put("userId", user.getId());
    claims.put("email", user.getEmail());

    return doGenerateToken(claims, user.getEmail());
  }

  private String doGenerateToken(Map<String, Object> claims, String subject) {
    return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
        .signWith(getSignKey(), SignatureAlgorithm.HS256).compact();
  }

  private Key getSignKey() {
    byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
    return Keys.hmacShaKeyFor(keyBytes);
  }

  public String getUsernameFromToken(String token) {
    return getClaimFromToken(token, Claims::getSubject);
  }

  public Date getExpirationDateFromToken(String token) {
    return getClaimFromToken(token, Claims::getExpiration);
  }

  public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = getAllClaimsFromToken(token);
    return claimsResolver.apply(claims);
  }

  private Claims getAllClaimsFromToken(String token) {
    return Jwts.parserBuilder().setSigningKey(getSignKey()).build().parseClaimsJws(token).getBody();
  }

  public Boolean validateToken(String token, UserDetails userDetails) {
    final String username = getUsernameFromToken(token);
    return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
  }

  private Boolean isTokenExpired(String token) {
    final Date expiration = getExpirationDateFromToken(token);
    return expiration.before(new Date());
  }
}
