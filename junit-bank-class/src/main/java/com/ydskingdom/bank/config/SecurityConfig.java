package com.ydskingdom.bank.config;

import com.ydskingdom.bank.config.jwt.JwtAuthenticationFilter;
import com.ydskingdom.bank.config.jwt.JwtAuthorizationFilter;
import com.ydskingdom.bank.domain.user.UserEnum;
import com.ydskingdom.bank.util.CustomResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Slf4j
@Configuration
public class SecurityConfig {

    @Bean // Ioc 컨테이너에 BCryptPasswordEncoder() 객체가 등록됨.
    public BCryptPasswordEncoder passwordEncoder() {
        log.debug("디버그 : BCryptPasswordEncoder 빈 등록됨");
        return new BCryptPasswordEncoder();
    }

    // JWT 필터 등록이 필요함
    public class CustomSecurityFilterManager extends AbstractHttpConfigurer<CustomSecurityFilterManager, HttpSecurity> {
        @Override
        public void configure(HttpSecurity builder) throws Exception {
            log.info("dsTest CustomSecurityFilterManager configure 필터 등록");
            AuthenticationManager authenticationManager = builder.getSharedObject(AuthenticationManager.class);

            // /api/login 호출될 때 탐
            builder.addFilter(new JwtAuthenticationFilter(authenticationManager));

            // 모든 주소에서 동작함
            builder.addFilter(new JwtAuthorizationFilter(authenticationManager));
            super.configure(builder);
        }
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        log.debug("디버그 : filterChain 빈 등록됨");
        http.headers().frameOptions().disable(); // iframe 허용 하지 않음

        http.csrf().disable(); // enable()이면 postman 작동하지 않음

        http.cors().configurationSource(configurationSource());

        // jSessionId를 서버쪽에서 관리하지 않겠다는 뜻
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // react, 앱으로만 요청 받을 예정
        http.formLogin().disable();

        // httpBasic()은 브라우저가 팝업창을 이용해서 사용자 인증을 진행한다.
        http.httpBasic().disable();

        //필터 적용
        http.apply(new CustomSecurityFilterManager());

        //인증 실패
        http.exceptionHandling().authenticationEntryPoint((request, response, e) -> CustomResponseUtil.fail(response, "로그인을 진행해 주세요", HttpStatus.UNAUTHORIZED));

        http.exceptionHandling().accessDeniedHandler((request, response, e) -> CustomResponseUtil.fail(response, "권한이 없습니다", HttpStatus.FORBIDDEN));

        http.authorizeRequests()
                .antMatchers("/api/s/**").authenticated()
                .antMatchers("/api/admin/**").hasRole("" + UserEnum.ADMIN)
                .anyRequest().permitAll();

        return http.build();
    }

    public CorsConfigurationSource configurationSource() {
        log.debug("디버그 : configurationSource cors 설정이 SecurityFilterChain에 등록됨");
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*"); // GET, POST, PUT, DELETE (Javascript 요청 허용)
        configuration.addAllowedOriginPattern("*"); // 모든 IP 주소 허용 (프론트 앤드 IP만 허용 react)
        configuration.setAllowCredentials(true); // 클라이언트에서 쿠키 요청 허용
        configuration.addExposedHeader("Authorization"); // 옛날에는 디폴트 였다. 지금은 아닙니다.
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
