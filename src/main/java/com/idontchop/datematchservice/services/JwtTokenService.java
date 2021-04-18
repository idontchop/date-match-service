package com.idontchop.datematchservice.services;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Order(1)
@Component
public class JwtTokenService {
	static final long EXPIRATIONTIME = 864000000;	// 1 day
	
	
	static String SIGNINGKEY;
	
	static final String PREFIX = "Bearer";
	
	static Logger logger = LoggerFactory.getLogger(JwtTokenService.class);
	

	// Get username from request
	static public String getAuthentication( HttpServletRequest req ) {
		
		String token = req.getHeader("Authorization");
		return getAuthenticationFromString(token);

	}
	
	static public String getAuthenticationFromString (String token) {
		if ( token != null && StringUtils.hasText(token) && token.startsWith("Bearer ")) {
			
			logger.info("Found Auth Token");
			String user = Jwts.parser()
					.setSigningKey(SIGNINGKEY)
					.parseClaimsJws(token.replace(PREFIX, ""))
					.getBody()
					.getSubject();
			
			return user;
		}
		
		logger.info("Unauthenticated Request");
		return null;
	}
	
	/**
	 * Supplies a JWT Token for the given user.
	 * 
	 * Sets subject to username
	 * 
	 * Sets one claim: 
	 * 		1) Expiration time from properties
	 * 		
	 * 
	 * @param name
	 * @return the token string
	 */
	public String buildToken ( String name ) {
		
		LocalDateTime ldt = LocalDateTime.now();
		ldt = ldt.plusHours( EXPIRATIONTIME );
		
		return Jwts.builder().setSubject(name)		
			.setExpiration( Date.from( ldt.atZone( ZoneId.systemDefault() ).toInstant()  ))
			.signWith(SignatureAlgorithm.HS512, SIGNINGKEY)
			.compact();
	}

	public  String getSIGNINGKEY() { 
		return SIGNINGKEY;
	}

	@Value ("${jwt.secret}")
	public void setSIGNINGKEY(String sIGNINGKEY) {
		SIGNINGKEY = sIGNINGKEY;
	}

	

}
