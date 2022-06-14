package com.example.cinema.config.security.jwt;

import com.example.cinema.config.security.UserDetailsImpl;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.crypto.SecretKey;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
@PropertySource("classpath:security.properties")
public class JwtUtils {
    private static String secret;
    private static long tokenValidityTimeInDays;

    public static String buildToken(Authentication authentication) {
        SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes());
        UserDetailsImpl user = (UserDetailsImpl) authentication.getPrincipal();

        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim("authorities", authentication.getAuthorities())
                .setIssuedAt(new Date())
                .setExpiration(java.sql.Date.valueOf(LocalDate.now().plusDays(tokenValidityTimeInDays)))
                .signWith(secretKey)
                .compact();
    }

    public static Jws<Claims> parse(String token){
        SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes());
        JwtParser parser = Jwts.parserBuilder().setSigningKey(secretKey).build();
        return parser.parseClaimsJws(token);
    }

    public static Authentication tokenToAuthenticationMapper(String token){
        Jws<Claims> claimsJws = parse(token);

        Claims body = claimsJws.getBody();
        String username = body.getSubject();

        var authorities = (List<Map<String, String>>) body.get("authorities");
        List<GrantedAuthority> grantedAuthorities = authorities.stream()
                .map(Map::values)
                .flatMap(Collection::stream)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        return new UsernamePasswordAuthenticationToken(username, null, grantedAuthorities);
    }

    @Value("${jwt.secret}")
    public void setSecret(String secret) {
        JwtUtils.secret = secret;
    }

    @Value("${jwt.token_validity_in_days}")
    public void setValidity(int validity) {
        JwtUtils.tokenValidityTimeInDays = validity;
    }
}
