package com.lulobank.otp.starter.v3.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Profile("dev")
@Configuration
@EnableWebSecurity
@Slf4j
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        log.info("***Security is in Mock mode****");
        http.csrf().disable().cors().and()
                .authorizeRequests(authorizeRequests -> authorizeRequests.antMatchers("/swagger-ui.html").permitAll()
                        .antMatchers("/swagger-resources/**").permitAll()
                        .antMatchers("/otp/**").permitAll()
                        .antMatchers("/actuator/**").permitAll()
                )
        ;
    }


}