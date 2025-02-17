package com.sandeep.demojwt.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.sandeep.demojwt.security.JwtAuthenticationEntryPoint;
import com.sandeep.demojwt.security.JwtAuthenticationFilter;

import jakarta.servlet.Filter;

@Configuration
public class SecurityConfig {
	
	@Autowired
	private JwtAuthenticationEntryPoint point;
	
	@Autowired
	private JwtAuthenticationFilter filter;
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		
		http.csrf(csrf->csrf.disable())
		         .cors(cors->cors.disable())
		         .authorizeHttpRequests(auth -> auth.requestMatchers("/home/**")//.hasRole("ADMIN")
		        		 .authenticated().requestMatchers("/auth/login")
		        		 .permitAll().anyRequest().authenticated())
		         .exceptionHandling(ex->ex.authenticationEntryPoint(point))
		         .sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
    
		http.addFilterBefore(filter,(Class<? extends Filter>) UsernamePasswordAuthenticationFilter.class);		
		         
		
		return http.build();
	}

}
