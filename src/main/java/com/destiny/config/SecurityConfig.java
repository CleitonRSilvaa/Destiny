package com.destiny.config;

import com.destiny.component.CustomAccessDeniedHandler;
import com.destiny.component.CustomAuthenticationFailureHandler;
import com.destiny.component.CustomAuthenticationSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Autowired
    private CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

    @Autowired
    private CustomAuthenticationFailureHandler customAuthenticationFailureHandler;

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new CustomAccessDeniedHandler();
    }

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .headers(headers -> headers.frameOptions().disable())
                .exceptionHandling(handling -> handling.accessDeniedHandler(accessDeniedHandler()))
                .authorizeRequests(requests -> requests
                        .antMatchers("/h2-console/**", "/imagens/**", "/css/**", "/js/**", "/jquery/**", "*/img/**",
                                "/", "*/produto/informacao/**")
                        .permitAll()
                        .antMatchers("/admin/**").hasAnyAuthority("ROLE_ADMIN") // Adicionado o prefixo 'ROLE_'
                        .antMatchers("/usuario/**").hasAnyAuthority("ROLE_ADMIN")
                        .antMatchers("/estoque/**").hasAnyAuthority("ROLE_ESTOQUISTA", "ROLE_ADMIN")
                        .anyRequest().authenticated())
                .formLogin(login -> login.loginPage("/login")
                        .failureHandler(customAuthenticationFailureHandler)
                        .successHandler(customAuthenticationSuccessHandler)
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll());

    }

}
