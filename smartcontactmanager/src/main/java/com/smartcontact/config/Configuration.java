package com.smartcontact.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@org.springframework.context.annotation.Configuration
@EnableWebSecurity
public class Configuration extends WebSecurityConfigurerAdapter {

	@Bean
	public UserDetailsService getUserDetailService() {
		return new UserDetailsServiceImpl();
	}

	@Bean
	public BCryptPasswordEncoder passEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public DaoAuthenticationProvider authenticationProvider() {

		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
		authenticationProvider.setUserDetailsService(this.getUserDetailService());
		authenticationProvider.setPasswordEncoder(passEncoder());

		return authenticationProvider;
	}

	// Overriding Configuration methods
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {

		auth.authenticationProvider(authenticationProvider());
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests().antMatchers("/admin/**").hasRole("ADMIN").antMatchers("/user/**").hasRole("USER")
				.antMatchers("/**").permitAll().and()
				.formLogin()
				.loginPage("/signin")
				.loginProcessingUrl("/dologin")
				.defaultSuccessUrl("/user/user_dashboard")
				.and()
				.csrf().disable();

	}

}
