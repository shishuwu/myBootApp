package com.jasonshi.sample.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configurable
@EnableWebSecurity
public class WebappSecurity extends WebSecurityConfigurerAdapter {
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
		// disable csrf
		.csrf().disable()
		
		.authorizeRequests()
		.antMatchers("/", "/home").permitAll()
		
		// people api
		.antMatchers("/people", "/people/**").authenticated()
		
		.anyRequest().authenticated()
		
		// login
		.and()
			.formLogin().loginPage("/login").permitAll()
			
		// logout
		.and().logout().permitAll()
		
		// http basic
		.and().httpBasic();
	}

	@Autowired
	public void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.inMemoryAuthentication().withUser("jason").password("jason").roles("USER");
		auth.inMemoryAuthentication().withUser("user").password("user").roles("USER");
	}
}
