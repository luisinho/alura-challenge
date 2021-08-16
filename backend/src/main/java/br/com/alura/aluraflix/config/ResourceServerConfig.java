package br.com.alura.aluraflix.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import br.com.alura.aluraflix.auth.AuthExceptionEntryPoint;

@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

	@Autowired
	private JwtTokenStore tokenStore;
	
	private static final String[] PUBLIC = { "/oauth/token" };

	private static final String SAVE_AND_FIND_ALL_USER = "/usuarios/**";

	@Override
	public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
		resources.tokenStore(this.tokenStore);
	}

	@Override
	public void configure(HttpSecurity http) throws Exception {

		http.authorizeRequests()
		.antMatchers(PUBLIC).permitAll()
		.antMatchers(HttpMethod.POST, SAVE_AND_FIND_ALL_USER).permitAll()
		.antMatchers(HttpMethod.GET, SAVE_AND_FIND_ALL_USER).permitAll()		
		.anyRequest().authenticated()
		.and().exceptionHandling().authenticationEntryPoint(new AuthExceptionEntryPoint());
	}
}