package com.aloumDaum.user.security;

import com.aloumDaum.user.exception.RestAccessDeniedHandler;
import com.aloumDaum.user.exception.RestAuthEntryPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
@EnableWebSecurity
public class WebSecurity extends WebSecurityConfigurerAdapter {

    private transient final Environment environment;

    @Autowired
    public WebSecurity(final Environment environment) {
        super();
        this.environment = environment;
    }


    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        http.csrf().disable().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.authorizeRequests().antMatchers(HttpMethod.OPTIONS).permitAll()

                .antMatchers(HttpMethod.POST, "/api/v1/user/login").permitAll()
                .antMatchers(HttpMethod.POST, "/api/v1/user/sendOtp").permitAll()
                .antMatchers(HttpMethod.POST, "/api/v1/user/setPassword").permitAll()
                .antMatchers(HttpMethod.GET, "/api/v1/health/check").permitAll()
                .antMatchers(HttpMethod.GET, "/api/v1/health/template/products").permitAll()

                .anyRequest().authenticated().and()
                .addFilter(new AuthFilter(authenticationManager(), environment));
        http.headers().frameOptions().disable();
    }
    private RestAccessDeniedHandler accessDeniedHandler() {
        return new RestAccessDeniedHandler();
    }

    private RestAuthEntryPoint authenticationEntryPoint() {
        return new RestAuthEntryPoint();
    }
}