package com.project.knit.config;

import com.project.knit.config.jwt.JwtTokenProvider;
import com.project.knit.service.CustomUserDetailService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@AllArgsConstructor
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final JwtTokenProvider jwtTokenProvider;

    private final CustomUserDetailService userDetailsService;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Override
    public void configure(WebSecurity webSecurity) {
        webSecurity.ignoring()
                .antMatchers("/v1/home/**", "/v1/user/login", "/v1/auth/**", "/actuator/health", "/upload", "/tags", "/categories", "/v1/admin/**", "/v1/auth/**", "/v1/threads/list", "/thread/**");
    }

//    @Override
//    public void configure(HttpSecurity httpSecurity) throws Exception {
//        httpSecurity.authorizeRequests()
//                .antMatchers("/**").permitAll();
//
//        httpSecurity.csrf().disable()
//                .authorizeRequests()
//                .antMatchers("/**").permitAll()
//                .anyRequest().authenticated()
//                .and()
//                .formLogin().disable();
//    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // return new BCryptPasswordEncoder();
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // ?????? ??????
        http
                .httpBasic().disable() // REST API?????? ??????, ?????? ?????? ??????
                .csrf().disable() // csrf ?????? X
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                // ?????? ?????? ??????????????? ????????? ?????? X
                .and()
                .authorizeRequests() // ????????? ?????? ???????????? ??????
                .antMatchers("/admin/**").hasRole("ADMIN")
                .antMatchers("/v1/user/**", "/thread/**", "/v1/threads/**").permitAll().anyRequest().hasRole("USER")
//                .antMatchers("/**").permitAll().anyRequest().hasRole("USER")
//                .anyRequest().permitAll() // ????????? ????????? ????????? ?????? ??????
                .and()
                .formLogin().disable();
//                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider),
//                        UsernamePasswordAuthenticationFilter.class);
        // JwtAuthenticationFilter???
        // UsernamePasswordAuthenticationFilter ?????? ??????
    }
}
