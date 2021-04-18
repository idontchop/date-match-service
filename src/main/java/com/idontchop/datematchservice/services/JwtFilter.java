package com.idontchop.datematchservice.services;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

public class JwtFilter extends GenericFilterBean {
	Logger logger = LoggerFactory.getLogger(JwtFilter.class);
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		logger.info("JWT Filter");
		String username;
		if ( ( username = JwtTokenService.getAuthentication( (HttpServletRequest) request )) != null) {
			
			logger.info("Log in via JWT: " + username);

			try {
				
				UsernamePasswordAuthenticationToken auth =
						new UsernamePasswordAuthenticationToken
						( username, null, AuthorityUtils.createAuthorityList("USER") );
				
				SecurityContextHolder.getContext().setAuthentication(auth);
				
			} catch (Exception e) {
				logger.debug ("Service load user: " + e.getMessage());
			}
		} 
		
		chain.doFilter(request, response);
		
		
	}

}
