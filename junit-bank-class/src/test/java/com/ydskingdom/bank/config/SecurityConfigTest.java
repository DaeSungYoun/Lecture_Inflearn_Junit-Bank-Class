package com.ydskingdom.bank.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void authentication_test() throws Exception {
        //given

        //when
        ResultActions resultActions = mockMvc.perform(get("/api/s/hello"));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        int status = resultActions.andReturn().getResponse().getStatus();
        System.out.println("테스트 : " + responseBody);
        System.out.println("테스트 : " + status);

        //then
        assertThat(status).isEqualTo(401);
    }

    @Test
    public void authorization_test() throws Exception {
        //given

        //when
        ResultActions resultActions = mockMvc.perform(get("/api/admin/hello"));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        int status = resultActions.andReturn().getResponse().getStatus();
        System.out.println("테스트 : " + responseBody);
        System.out.println("테스트 : " + status);

        //then
        assertThat(status).isEqualTo(401);
    }
}