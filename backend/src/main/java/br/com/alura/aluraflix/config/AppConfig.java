package br.com.alura.aluraflix.config;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.provider.error.DefaultWebResponseExceptionTranslator;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

@Configuration
public class AppConfig {

	@Value("${jwt.client.secret}")
	private String jwtClientSecret;

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public JwtAccessTokenConverter accessTokenConverter() {
		JwtAccessTokenConverter tokenConverter = new JwtAccessTokenConverter();
		tokenConverter.setSigningKey(this.jwtClientSecret);
		return tokenConverter;
	}

	@Bean
	public JwtTokenStore tokenStore() {
		return new JwtTokenStore(this.accessTokenConverter());
	}
	
	@Bean
	public MessageSource messageSource() {
	    ReloadableResourceBundleMessageSource messageSource
	      = new ReloadableResourceBundleMessageSource();
	    
	    messageSource.setBasename("classpath:messages");
	    messageSource.setDefaultEncoding("UTF-8");
	    return messageSource;
	}

	@Bean
	public WebResponseExceptionTranslator<OAuth2Exception> oauth2ResponseExceptionTranslator() {
        return new DefaultWebResponseExceptionTranslator() {

            @Override
            public ResponseEntity<OAuth2Exception> translate(Exception e) throws Exception {

                ResponseEntity<OAuth2Exception> responseEntity = super.translate(e);

                OAuth2Exception body = responseEntity.getBody();

                HttpStatus statusCode = responseEntity.getStatusCode();

                if (e instanceof InvalidGrantException) {

                	String message = messageSource().getMessage("user.name.not.found", null, null);

                	body.addAdditionalInformation("timestamp", LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC).toString());
                	body.addAdditionalInformation("message", message);
                }

                HttpHeaders headers = new HttpHeaders();
                headers.setAll(responseEntity.getHeaders().toSingleValueMap());

                return new ResponseEntity<>(body, headers, statusCode);
            }
        };
    }	
}