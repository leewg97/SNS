package com.wg.sns.config;

import com.wg.sns.config.filter.JwtTokenFilter;
import com.wg.sns.exception.CustomAuthenticationEntryPoint;
import com.wg.sns.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class JwtSecurityConfig {

    private final UserService userService;
    @Value("${jwt.secret-key}")
    private String key;

    public JwtSecurityConfig(UserService userService) {
        this.userService = userService;
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring().regexMatchers("^(?!/api/).*")
                .antMatchers("/api/*/users/join", "/api/*/users/login");
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf().disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                .and()
                .authorizeRequests()
                .antMatchers("/api/**")
                .authenticated()

                .and()
                .addFilterBefore(new JwtTokenFilter(userService, key), UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling()
                .authenticationEntryPoint(new CustomAuthenticationEntryPoint())

                .and()
                .build();
    }
}
