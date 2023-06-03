package org.salesianas.transferg.config;

import org.salesianas.transferg.models.ERole;
import org.salesianas.transferg.repositories.ERoleRepository;
import org.salesianas.transferg.security.filter.JWTAuthenticationFilter;
import org.salesianas.transferg.security.filter.JWTAuthorizationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.AllArgsConstructor;


@Configuration
@AllArgsConstructor
public class WebSecurityConfig {
	
	private final ERoleRepository roleRepository;
	private final UserDetailsService userDetailsService;
	private final JWTAuthorizationFilter jwtAuthorizationFilter;

	@Bean
	SecurityFilterChain filterChain( HttpSecurity http, AuthenticationManager authManager) throws Exception {
		
	    JWTAuthenticationFilter jwtAuthenticationFilter = new JWTAuthenticationFilter();
	    jwtAuthenticationFilter.setAuthenticationManager(authManager);
	    jwtAuthenticationFilter.setFilterProcessesUrl("/login");

	    return http
	    		.csrf().disable().cors().and()
	            .authorizeRequests()
	            .antMatchers("/register").permitAll()
	            .antMatchers("/login").permitAll()
	            .anyRequest().authenticated()
	            .and()
	            .httpBasic()
	            .and()
	            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
	            .and()
	            .addFilter(jwtAuthenticationFilter)
	            .addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class)
	            .build();
	}	
	
	@Bean
	AuthenticationManager authManager(HttpSecurity http) throws Exception {
		return http.getSharedObject(AuthenticationManagerBuilder.class)
				.userDetailsService(userDetailsService)
				.passwordEncoder(passwordEncoder())
				.and()
				.build();
	}
	
	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	

    @Bean
    public void createRoles() {
        if (roleRepository.findByName("ADMIN") == null) {
            ERole adminRole = new ERole("ADMIN");
            roleRepository.save(adminRole);
        }

        if (roleRepository.findByName("USER") == null) {
            ERole userRole = new ERole("USER");
            roleRepository.save(userRole);
        }
    }
}
