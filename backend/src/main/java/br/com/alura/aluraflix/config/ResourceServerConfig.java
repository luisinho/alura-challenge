package br.com.alura.aluraflix.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import br.com.alura.aluraflix.auth.AuthExceptionEntryPoint;

@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

	@Autowired
	private Environment env;

	@Autowired
	private JwtTokenStore tokenStore;
	
	private static final String[] PUBLIC = { "/oauth/token", "/h2-console/**", "/swagger-ui.html", "/actuator/**" };

	private static final String SAVE_AND_FIND_ALL_USER = "/usuarios/**";

	@Override
	public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
		resources.tokenStore(this.tokenStore);
	}

	@Override
	public void configure(HttpSecurity http) throws Exception {

		// Libera os frames da url /h2-console da interface do banco H2
		if (Arrays.asList(this.env.getActiveProfiles()).contains("test")) {
			http.headers().frameOptions().disable();
		}

		http.authorizeRequests()
		.antMatchers(PUBLIC).permitAll()
		.antMatchers(HttpMethod.POST, SAVE_AND_FIND_ALL_USER).permitAll()
		.antMatchers(HttpMethod.GET, SAVE_AND_FIND_ALL_USER).permitAll()		
		.anyRequest().authenticated()
		.and().exceptionHandling().authenticationEntryPoint(new AuthExceptionEntryPoint());

		http.cors().configurationSource(this.corsConfigurationSource());
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration corsConfig = new CorsConfiguration();
		corsConfig.setAllowedOriginPatterns(Arrays.asList("*"));
		corsConfig.setAllowedMethods(Arrays.asList("POST", "GET", "PUT", "DELETE", "PATCH"));
		corsConfig.setAllowCredentials(true);
		corsConfig.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", corsConfig);
		return source;
	}

	@Bean
	public FilterRegistrationBean<CorsFilter> corsFilter() {
		FilterRegistrationBean<CorsFilter> bean
			= new FilterRegistrationBean<>(new CorsFilter(corsConfigurationSource()));
		bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
		return bean;
	}
}