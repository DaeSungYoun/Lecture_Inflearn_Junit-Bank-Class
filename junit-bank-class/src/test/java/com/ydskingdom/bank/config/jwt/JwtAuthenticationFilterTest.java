package com.ydskingdom.bank.config.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ydskingdom.bank.config.dummy.DummyObject;
import com.ydskingdom.bank.domain.user.UserRepository;
import com.ydskingdom.bank.dto.user.UserReqDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import javax.transaction.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class JwtAuthenticationFilterTest extends DummyObject {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.save(newUser("ssar", "쌀"));
    }

    @Test
    void successfulAuthentication_test() throws Exception {
        //given
        UserReqDto.LoginReqDto loginReqDto = new UserReqDto.LoginReqDto();
        loginReqDto.setUsername("ssar");
        loginReqDto.setPassword("1234");

        String requestBody = objectMapper.writeValueAsString(loginReqDto);
        System.out.println("requestBody = " + requestBody);

        //when
        ResultActions resultActions = mockMvc.perform(post("/api/login")
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON));

        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        String jwtToken = resultActions.andReturn().getResponse().getHeader(JwtVO.HEADER);
        System.out.println("responseBody = " + responseBody);
        System.out.println("jwtToken = " + jwtToken);

        //then
        resultActions.andExpect(status().isOk());
        assertNotNull(jwtToken);
        assertTrue(jwtToken.startsWith(JwtVO.TOKEN_PREFIX));
        resultActions.andExpect(jsonPath("$.data.username").value("ssar"));
    }

    @Test
    void unsuccessfulAuthentication_test() throws Exception {
        //given
        UserReqDto.LoginReqDto loginReqDto = new UserReqDto.LoginReqDto();
        loginReqDto.setUsername("ssar");
        loginReqDto.setPassword("123");

        String requestBody = objectMapper.writeValueAsString(loginReqDto);
        System.out.println("requestBody = " + requestBody);

        //when
        ResultActions resultActions = mockMvc.perform(post("/api/login")
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON));

        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        String jwtToken = resultActions.andReturn().getResponse().getHeader(JwtVO.HEADER);
        System.out.println("responseBody = " + responseBody);
        System.out.println("jwtToken = " + jwtToken);

        //then
        resultActions.andExpect(status().isUnauthorized());
    }
}