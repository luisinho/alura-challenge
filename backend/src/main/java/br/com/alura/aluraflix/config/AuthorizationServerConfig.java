package br.com.alura.aluraflix.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig  extends AuthorizationServerConfigurerAdapter {

	private static final String METHOD_PERMIT_ALL = "permitAll()";

	private static final String METHOD_IS_AUTHENTICATED = "isAuthenticated()";

	private static final String SCOPES [] = {"read", "write"};

	private static final String GRANT_TYPES [] = {"password"};

	@Value("${security.oauth2.client.client-id}")
	private String clientId;

	@Value("${security.oauth2.client.client-secret}")
	private String clientSecret;

	@Value("${jwt.duration}")
	private Integer jwtDuration;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Autowired
	private JwtAccessTokenConverter accessTokenConverter;

	@Autowired
	private JwtTokenStore tokenStore;

	@Autowired
	private AuthenticationManager authenticationManager;	

	@Autowired
	private WebResponseExceptionTranslator<OAuth2Exception> oauth2ResponseExceptionTranslator;

	@Override
	public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
		security.tokenKeyAccess(METHOD_PERMIT_ALL).checkTokenAccess(METHOD_IS_AUTHENTICATED);
	}

	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		clients.inMemory()
		 .withClient(this.clientId)
		 .secret(this.passwordEncoder.encode(this.clientSecret))
		 .scopes(SCOPES)
		 .authorizedGrantTypes(GRANT_TYPES)
		 .accessTokenValiditySeconds(this.jwtDuration);
	}

	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {

		endpoints.authenticationManager(this.authenticationManager)
		.tokenStore(this.tokenStore)
		.accessTokenConverter(this.accessTokenConverter)
		.exceptionTranslator(this.oauth2ResponseExceptionTranslator);

	}
}