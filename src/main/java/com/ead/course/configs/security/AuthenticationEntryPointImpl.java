package com.ead.course.configs.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint{

	private Logger log = LogManager.getLogger(AuthenticationEntryPointImpl.class);
	
	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {
		log.error("Erro de não autorização (Unauthorized error): {}", authException.getMessage());
		response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
		
	}

}
