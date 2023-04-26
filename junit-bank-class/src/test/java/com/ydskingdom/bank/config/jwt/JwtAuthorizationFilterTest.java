package com.ydskingdom.bank.config.jwt;

import com.ydskingdom.bank.config.auth.LoginUser;
import com.ydskingdom.bank.domain.user.User;
import com.ydskingdom.bank.domain.user.UserEnum;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@Sql("classpath:db/teardown.sql")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class JwtAuthorizationFilterTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void authorization_success_test() throws Exception {
        //given
        User user = User.builder().id(1L).role(UserEnum.CUSTOMER).build();
        LoginUser loginUser = new LoginUser(user);
        String jwtToken = JwtProcess.create(loginUser);
        System.out.println("jwtToken = " + jwtToken);

        //when
        ResultActions resultActions = mockMvc.perform(get("/api/s/hello").header(JwtVO.HEADER, jwtToken));

        //then
        resultActions.andExpect(status().isNotFound());
    }

    @Test
    void authorization_fail_test() throws Exception {
        //given

        //when
        ResultActions resultActions = mockMvc.perform(get("/api/s/hello"));

        //then
        resultActions.andExpect(status().isUnauthorized());
    }

    @Test
    void authorization_admin_test() throws Exception {
        //given
        User user = User.builder().id(1L).role(UserEnum.CUSTOMER).build();
        LoginUser loginUser = new LoginUser(user);
        String jwtToken = JwtProcess.create(loginUser);
        System.out.println("jwtToken = " + jwtToken);

        //when
        ResultActions resultActions = mockMvc.perform(get("/api/admin/hello").header(JwtVO.HEADER, jwtToken));

        //then
        resultActions.andExpect(status().isForbidden());
    }
}