package com.ydskingdom.bank.web;

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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class UserControllerTest extends DummyObject {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        userRepository.save(newUser("ssar", "쌀"));
    }

    @Test
    void join_test() throws Exception {
        //given
        UserReqDto.JoinReqDto joinReqDto = new UserReqDto.JoinReqDto();
        joinReqDto.setUsername("love");
        joinReqDto.setPassword("1234");
        joinReqDto.setEmail("love@nate.com");
        joinReqDto.setFullname("러브");

        String requestBody = objectMapper.writeValueAsString(joinReqDto);
        System.out.println("테스트 requestBody = " + requestBody);

        //when
        ResultActions resultActions = mvc.perform(post("/api/join").content(requestBody).contentType(MediaType.APPLICATION_JSON));
        String contentAsString = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 contentAsString = " + contentAsString);

        //then
        resultActions.andExpect(status().isCreated());
    }

    @Test
    void join_fail_test() throws Exception {
        //given
        UserReqDto.JoinReqDto joinReqDto = new UserReqDto.JoinReqDto();
        joinReqDto.setUsername("ssar");
        joinReqDto.setPassword("1234");
        joinReqDto.setEmail("ssar@nate.com");
        joinReqDto.setFullname("쌀");

        String requestBody = objectMapper.writeValueAsString(joinReqDto);
        System.out.println("테스트 requestBody = " + requestBody);

        //when
        ResultActions resultActions = mvc.perform(post("/api/join").content(requestBody).contentType(MediaType.APPLICATION_JSON));
        String contentAsString = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 contentAsString = " + contentAsString);

        //then
        resultActions.andExpect(status().isBadRequest());
    }
}