package com.jasonshi.sample.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configurable
@EnableWebSecurity
public class WebappSecurity extends WebSecurityConfigurerAdapter {
	@Autowired
	public void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.inMemoryAuthentication().withUser("jason").password("jason").roles("USER");
		auth.inMemoryAuthentication().withUser("user").password("user").roles("USER");
	}

	@Configuration
	@Order(1)
	public static class ApiWebSecurityConfig extends WebSecurityConfigurerAdapter {
		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http.csrf().disable().antMatcher("/people/**").authorizeRequests().anyRequest().hasAnyRole("USER").and()
					.httpBasic();
		}
	}

	@Configuration
	@Order(2)
	public static class FormWebSecurityConfig extends WebSecurityConfigurerAdapter {

		@Override
		public void configure(WebSecurity web) throws Exception {
			web.ignoring().antMatchers("/css/**", "/js/**", "/img/**", "/lib/**");
		}

		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http
					// disable csrf
					.csrf().disable()

					.authorizeRequests().antMatchers("/", "/home").permitAll()
					//
					.anyRequest().authenticated()

					// login
					.and().formLogin().loginPage("/login").permitAll()
					// logout
					.and().logout().permitAll();
		}
	}
}
