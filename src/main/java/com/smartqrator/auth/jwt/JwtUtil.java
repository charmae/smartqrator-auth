package com.smartqrator.auth.jwt;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import com.smartqrator.auth.dto.UserDetailsDTO;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class JwtUtil {

	private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

	@Value("${auth.app.jwt.secret}")
	private String jwtSecret;

	@Value("${auth.app.jwt.expiration.ms}")
	private int jwtExpirationMs;

	@Value("${auth.app.jwt.cookie.name}")
	private String jwtCookie;

	public String getJwtFromCookies(HttpServletRequest request) {
		Cookie cookie = WebUtils.getCookie(request, jwtCookie);
		if (cookie != null) {
			return cookie.getValue();
		} else {
			return null;
		}
	}

	public ResponseCookie generateJwtCookie(UserDetailsDTO userPrincipal) {
		String jwt = generateTokenFromUsername(userPrincipal.getUsername());
		ResponseCookie cookie = ResponseCookie.from(jwtCookie, jwt).path("/api").maxAge(24 * 60 * 60).httpOnly(true)
				.build();
		return cookie;
	}

	public String getUserNameFromJwtToken(String token) {
		return Jwts.parser().verifyWith(key()).build().parseSignedClaims(token).getPayload().getSubject();
	}

	public ResponseCookie getCleanJwtCookie() {
		ResponseCookie cookie = ResponseCookie.from(jwtCookie, null).path("/api").build();
		return cookie;
	}

	private SecretKey key() {
		return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
	}

	public boolean validateJwtToken(String authToken) {
		try {
			Jwts.parser().verifyWith(key()).build().parseSignedClaims(authToken);
			return true;
		} catch (MalformedJwtException e) {
			logger.error("Invalid JWT token: {}", e.getMessage());
		} catch (ExpiredJwtException e) {
			logger.error("JWT token is expired: {}", e.getMessage());
		} catch (UnsupportedJwtException e) {
			logger.error("JWT token is unsupported: {}", e.getMessage());
		} catch (IllegalArgumentException e) {
			logger.error("JWT claims string is empty: {}", e.getMessage());
		}

		return false;
	}

	public String generateTokenFromUsername(String username) {

		Map<String, Object> payload = new HashMap<>();
		payload.put("sub", username);
		payload.put("iat", new Date());
		payload.put("exp", new Date((new Date()).getTime() + jwtExpirationMs));
		return Jwts.builder().claims(payload).signWith(key(), Jwts.SIG.HS256).compact();
	}
}
