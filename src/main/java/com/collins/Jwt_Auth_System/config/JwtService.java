package com.collins.Jwt_Auth_System.config;

import com.collins.Jwt_Auth_System.dtos.requests.LoginRequest;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    private final String secretKey = "QWERTYUIOPASDFGHJKLZXCVBNM1234567890abcdef=";

    public String generateToken(LoginRequest loginRequest){
        Map<String,Object> claims = new HashMap<>();
        claims.put("email",loginRequest.getEmail());
        claims.put("role","User");

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(loginRequest.getEmail())
                .setIssuer("Jwt_Auth_system")
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + (10 * 60 * 1000))) // 10mins
                .signWith(generateKey())
                .compact();
    }

    private SecretKey generateKey(){
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }


    public String extractUserName(String token) {
        return extractClaims(token, Claims::getSubject);
    }

    private<T> T extractClaims(String token, Function<Claims,T> claimsResolver){
        Claims claims = extractClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractClaims(String token) {
        return Jwts.parserBuilder().
                setSigningKey(generateKey()).
                build().
                parseClaimsJws(token).
                getBody();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUserName(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaims(token, Claims::getExpiration);
    }
}
