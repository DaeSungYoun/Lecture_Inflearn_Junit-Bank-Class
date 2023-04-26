package com.ydskingdom.bank.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ydskingdom.bank.config.dummy.DummyObject;
import com.ydskingdom.bank.domain.user.UserRepository;
import com.ydskingdom.bank.dto.account.AccountReqDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class AccountControllerTest extends DummyObject {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;


    @BeforeEach
    public void setUp() {
        userRepository.save(newUser("ssar", "쌀"));
    }

    // jwt token -> 인증필터 -> 시큐리티 세션생성
    // setupBefore=TEST_METHOD (setUp 메서드 실행전에 수행)
    // setupBefore=TEST_EXECUTION (saveAccount_test 메서드 실행전에 수행)
    @WithUserDetails(value = "ssar", setupBefore = TestExecutionEvent.TEST_EXECUTION) // 디비에서 username=ssar 조회를 해서
    // 세션에
    // 담아주는 어노테이션!!

    @Test
    public void saveAccount_test() throws Exception {
        // given
        AccountReqDto accountReqDto = new AccountReqDto();
        accountReqDto.setNumber(9999L);
        accountReqDto.setPassword(1234L);

        String requestBody = objectMapper.writeValueAsString(accountReqDto);
        System.out.println("테스트 : " + requestBody);

        // when
        ResultActions resultActions = mockMvc
                .perform(post("/api/s/account").content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(status().isCreated());
    }
}