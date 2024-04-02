package com.sandeep.demojwt.security;

import java.util.logging.Logger;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.CoWebFilterChain;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	
	private Logger logger= (Logger) LoggerFactory.getLogger(OncePerRequestFilter.class);
	
	@Autowired
	private JwtHelper jwtHelper;
	
	@Autowired 
	private UserDetailsService userDetailService;

	private Object userDetailsService;
	@Override
	protected void doFilterInternal(HttpServletRequest request,HttpServletResponse response,FilterChain filterchain) {
		String requestHeader=request.getHeader("Authorization");
		logger.info("Header : {}");
		String username=null;
		String token = null;
		
		if(requestHeader != null && requestHeader.startsWith("Bearer")) {
			//looking good
			token=requestHeader.substring(7);
			try {
				username=this.jwtHelper.getUsernameFromToken(token);
			}catch(IllegalArgumentException e) {
				logger.info("Illegal Argument while fetching the username !!");
				e.printStackTrace();
			}catch(ExpiredJwtException e) {
				logger.info("Given jwt token is expired !!");
				e.printStackTrace();
			}catch(MalformedJwtException e) {
				logger.info("Some changed has done in token !! Invalid token ");
				e.printStackTrace();
			}
		}else {
			logger.info("Invalid Header Value !!");
		}
		if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			//fetch user detail from username
			UserDetails userDetails = ((UserDetailsService) this.userDetailsService).loadUserByUsername(username);
			Boolean validateToken = this.jwtHelper.validateToken(token, userDetails);
			if (validateToken) {
				//set the Authentication
				UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
				authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(authentication);
			}else {
				logger.info("Validation Fails !!");
			}
		}
		//filterChain.doFilter(request, response);
	}
		
	}
	
