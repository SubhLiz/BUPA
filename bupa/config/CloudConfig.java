package com.incture.bupa.config;

import com.sap.cloud.security.xsuaa.XsuaaServiceConfiguration;
import com.sap.cloud.security.xsuaa.token.TokenAuthenticationConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.Jwt;
@Configuration
@EnableWebSecurity
public class CloudConfig extends WebSecurityConfigurerAdapter{

	@Autowired
	XsuaaServiceConfiguration xsuaaServiceConfiguration;


//	@Autowired
//	XsuaaTokenFlows xsuaaTokenFlows;


	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// @formatter:off
		try {
			System.err.println("SecurityFilterChain try: ");
			http
					.csrf().disable()
					.sessionManagement()
					.sessionCreationPolicy(SessionCreationPolicy.STATELESS) // session is created by approuter
					.and()
					.authorizeRequests()
					.antMatchers("/swagger-ui.html#/**").permitAll()
					.anyRequest().authenticated()
					.and()
					.oauth2ResourceServer()
					.jwt()
					.jwtAuthenticationConverter(getJwtAuthenticationConverter());
		}catch(Exception e) {
			System.err.println("SecurityFilterChain: "+e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Customizes how GrantedAuthority are derived from a Jwt
	 */
	Converter<Jwt, AbstractAuthenticationToken> getJwtAuthenticationConverter() {
		TokenAuthenticationConverter converter = new TokenAuthenticationConverter(xsuaaServiceConfiguration);
		converter.setLocalScopeAsAuthorities(true);
		return converter;
	}

	@Override
	public void configure(WebSecurity web) throws Exception
	{
		web.ignoring().mvcMatchers(HttpMethod.OPTIONS, "/**");
		web.ignoring().mvcMatchers("/swagger-ui.html/**", "/configuration/**", "/swagger-resources/**",
				"/v2/api-docs", "/webjars/**");
	}

}