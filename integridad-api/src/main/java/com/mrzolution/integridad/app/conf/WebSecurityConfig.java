package com.mrzolution.integridad.app.conf;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	
	
	@Override
	public void configure(AuthenticationManagerBuilder auth) throws Exception {
	    auth.inMemoryAuthentication()
	      .withUser("dan").password("12345").roles("USER", "ADMIN");
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
	      .httpBasic().and()
	      .authorizeRequests()
	        .antMatchers(HttpMethod.POST, "/integridad/**").hasRole("ADMIN")
	        .antMatchers(HttpMethod.PUT, "/integridad/**").hasRole("ADMIN")
	        .antMatchers(HttpMethod.PATCH, "/integridad/**").hasRole("ADMIN").and()
	      .csrf().disable();
	}

}