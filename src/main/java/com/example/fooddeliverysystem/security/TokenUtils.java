package com.example.fooddeliverysystem.security;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class TokenUtils {
    @Value("myXAuthSecret")
    private String secret;

    @Value("18000")
    private long expiration;

    public String getUsernameFromToken(String token) {
        String username;
        try {
            Claims claimFromToken = getClaimFromToken(token.replace("Bearer", ""));
            username = claimFromToken.getSubject();
        } catch (Exception e) {
            username = null;
        }

        return username;
    }

    public Date getExpirationFromToken(String token) {
        Date expiration;
        try {
            final Claims claims = getClaimFromToken(token.replace("Bearer", ""));
            expiration = claims.getExpiration();
        } catch (Exception e) {
            expiration = null;
        }

        return expiration;
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", userDetails.getUsername());
        claims.put("created", new Date(System.currentTimeMillis()));

        return Jwts.builder().setClaims(claims).setExpiration(new Date(System.currentTimeMillis() + expiration * 1000))
                .signWith(SignatureAlgorithm.HS512, secret).compact();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token.replace("Bearer", ""));

        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        final Date expirationDate = getExpirationFromToken(token);

        return expirationDate.before(new Date(System.currentTimeMillis()));
    }

    private Claims getClaimFromToken(String token) {
        Claims claims;
        try {
            claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
        } catch (Exception e) {
            claims = null;
        }

        return claims;
    }
}
