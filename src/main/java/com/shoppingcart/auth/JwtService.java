package com.shoppingcart.auth;

import com.shoppingcart.exception.TokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {
    public String generateAccessToken(Long id, String email) {
        return Jwts.builder()
                .subject(email)
                .claim("id", id)
                .expiration(new Date(System.currentTimeMillis() + 30000))
                .issuedAt(new Date(System.currentTimeMillis()))
                .signWith(getKey())
                .compact();
    }

    public String generateRefreshToken(Long id, String email) {
        return Jwts.builder()
                .subject(email)
                .claim("id", id)
                .claim("type", "refresh")
                .issuedAt(new Date(System.currentTimeMillis()))
                .signWith(getKey())
                .compact();
    }

    public Claims verifyToken(String jws) throws TokenException {
        try {
            return Jwts.parser()
                    .verifyWith(getKey())
                    .build()
                    .parseSignedClaims(jws)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            throw new TokenException("Token Expired");
        } catch (Exception e) {
            throw new TokenException(e.getMessage());
        }
    }

    private SecretKey getKey() {
        String SECRET = "95ae09b4ff3417129aaad48f403542afa294df2393df1020e6c7246d3c58b6c7";
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET));
    }
}


