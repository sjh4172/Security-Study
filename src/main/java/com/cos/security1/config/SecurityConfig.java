package com.cos.security1.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.cos.security1.config.auth.PrincipalDetailsService;
import com.cos.security1.config.oauth.PrincipalOauth2UserService;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity // Spring Security 활성화
@EnableMethodSecurity // 메서드 기반 보안 활성화 (@PreAuthorize 등 사용 가능)
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true) // secured 어노테이션 활성화, preAuthorize 어노테이션 활성화
public class SecurityConfig {

	@Autowired
	private PrincipalDetailsService principalDetailsService;
	
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Autowired
    private PrincipalOauth2UserService principalOauth2UserService;
    
	@Bean
	public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception{
		AuthenticationManagerBuilder auth = http.getSharedObject(AuthenticationManagerBuilder.class);
		auth.userDetailsService(principalDetailsService).passwordEncoder(passwordEncoder());
		return auth.build();
	}
	
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // CSRF 비활성화
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/user/**").authenticated() // 인증 필요
                .requestMatchers("/admin/**").hasRole("ADMIN") // ADMIN 권한 필요
                .anyRequest().permitAll() // 그 외 모든 요청 허용
            )
            .formLogin(form -> form
                .loginPage("/loginForm") // 사용자 정의 로그인 페이지
                .loginProcessingUrl("/loginProc") // 로그인 처리 URL
                .defaultSuccessUrl("/") // 로그인 성공 후 이동할 페이지
            )
            .oauth2Login(oauth2 -> oauth2 
                .loginPage("/loginForm") // OAuth2 로그인 페이지
                .userInfoEndpoint(userInfo -> userInfo
                    .userService(principalOauth2UserService) // OAuth2 사용자 정보 서비스
                )
            ); 

        return http.build();
    }
}
