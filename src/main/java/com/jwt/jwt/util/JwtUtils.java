package com.jwt.jwt.util;


import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import com.jwt.jwt.entity.User;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtils {

    @Value("${jwt.secret}")
    private  String secretKey;

    private Key getSigningKey(){
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }
    //Extract username
    public String extractUsername(String token){
        return extractClaim(token, Claims::getSubject);
    }

    // Extract roles

    @SuppressWarnings("unchecked")
    public List<String> extractRoles(String token){
        return (List<String>) extractAllClaims(token).get("roles");
    }

    public  <T> T extractClaim(String token, Function<Claims, T> claimsResolver){
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public Claims extractAllClaims(String token){
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Date extractExpiration(String token){
        return extractClaim(token, Claims::getExpiration);
    }


    private boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }

    public boolean isTokenValid(String token, UserDetails userDetails){
        String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }



    public String generateToken(User user){
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, user);
    }

    // Generate refresh tokens

    public String generateRefreshToken(UserDetails userDetails){
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+ 1000 * 60 * 60 * 24 * 7)) // 7 Days
                .signWith(getSigningKey(),SignatureAlgorithm.HS256)
                .compact();
    }

    private String createToken(Map<String, Object> claims, User user){
        claims.put("roles", user.getRoles());
        claims.put("email",user.getEmail());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getUsername())
                .setId(user.getId().toString())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10 hours to expire
                .signWith(getSigningKey(),SignatureAlgorithm.HS256)
                .compact();
    }

}
