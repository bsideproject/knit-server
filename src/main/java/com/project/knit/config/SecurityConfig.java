package com.project.knit.config;

import com.project.knit.jwt.JwtAuthenticationFilter;
import com.project.knit.jwt.JwtTokenProvider;
import com.project.knit.utils.enums.Role;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@AllArgsConstructor
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void configure(WebSecurity webSecurity) throws Exception {
//        webSecurity.ignoring()
//                .antMatchers("/**")
//                .anyRequest();

        webSecurity.ignoring()
                .antMatchers("/v1/home/**", "/v1/user/login");
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
        // 필터 등록
        http
                .httpBasic().disable() // REST API만을 고려, 기본 설정 해제
                .csrf().disable() // csrf 사용 X
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                // 토큰 기반 인증이므로 세션도 사용 X
                .and()
                .authorizeRequests() // 요청에 대한 사용권한 체크
//                .antMatchers("/admin/**").hasRole("ADMIN")
//                .antMatchers("/user/**", "/thread/**").hasRole("USER")
                .antMatchers("/**").hasRole("USER")
                .anyRequest().permitAll() // 나머지 요청은 누구나 접근 가능
                .and()
                .formLogin().disable()
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider),
                        UsernamePasswordAuthenticationFilter.class);
        // JwtAuthenticationFilter는
        // UsernamePasswordAuthenticationFilter 전에 넣음
    }
}
