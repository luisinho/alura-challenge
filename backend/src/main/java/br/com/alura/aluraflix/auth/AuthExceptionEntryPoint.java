package br.com.alura.aluraflix.auth;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AuthExceptionEntryPoint implements AuthenticationEntryPoint {

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {

		final Map<String, Object> mapBodyException = new HashMap<>();

		mapBodyException.put("timestamp", LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC).toString());

        if (authException instanceof InsufficientAuthenticationException) {

        	final String message = "Não autorizado ou Credenciais inválidas!";

        	byte[] messageBytes = message.getBytes();

        	mapBodyException.put("error_description", authException.getMessage());
        	mapBodyException.put("error", HttpStatus.UNAUTHORIZED.toString().toLowerCase());
        	mapBodyException.put("status", HttpStatus.UNAUTHORIZED.value());
        	mapBodyException.put("message", new String(messageBytes, StandardCharsets.UTF_8));
        	mapBodyException.put("path", request.getRequestURI());

        }

        response.setContentType("application/json");

        final ObjectMapper mapper = new ObjectMapper() ;
        mapper.writeValue(response.getOutputStream(), mapBodyException);
	}
}