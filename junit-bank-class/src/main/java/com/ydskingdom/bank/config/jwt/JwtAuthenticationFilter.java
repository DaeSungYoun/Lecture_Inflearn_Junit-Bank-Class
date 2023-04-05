package com.ydskingdom.bank.config.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ydskingdom.bank.config.auth.LoginUser;
import com.ydskingdom.bank.dto.user.UserReqDto;
import com.ydskingdom.bank.dto.user.UserResDto;
import com.ydskingdom.bank.util.CustomResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
        setFilterProcessesUrl("/api/login");
        this.authenticationManager = authenticationManager;
    }

    // Post, /login api호출하면 여기로 옴
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        log.info("dsTest JwtAuthenticationFilter attemptAuthentication start");
        ObjectMapper om = new ObjectMapper();
        try {
            UserReqDto.LoginReqDto loginReqDto = om.readValue(request.getInputStream(), UserReqDto.LoginReqDto.class);

            //강제 로그인
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                    loginReqDto.getUsername(), loginReqDto.getPassword()
            );

            // UserDetailsService의 loadUserByUsername
            Authentication authenticate = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
            return authenticate;
        } catch (Exception e) {
            log.info("dsTest JwtAuthenticationFilter catch start");
            //InternalAuthenticationServiceException이 터져야 SecurityConfig에 설정한 http.exceptionHandling().authenticationEntryPoint에 걸림
            throw new InternalAuthenticationServiceException(e.getMessage());
        }
    }

    // 위에 있는 attemptAuthentication() 메서드에서 정상적으로 return authenticate;되면 successfulAuthentication가 되면 호출됨
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) {
        log.info("dsTest JwtAuthenticationFilter successfulAuthentication start");
        LoginUser loginUser = (LoginUser) authResult.getPrincipal();
        String jwtToken = JwtProcess.create(loginUser);
        response.addHeader(JwtVO.HEADER, jwtToken);

        UserResDto.LoginRespDto loginRespDto = new UserResDto.LoginRespDto(loginUser.getUser());

        CustomResponseUtil.success(response, loginRespDto);
    }
}

