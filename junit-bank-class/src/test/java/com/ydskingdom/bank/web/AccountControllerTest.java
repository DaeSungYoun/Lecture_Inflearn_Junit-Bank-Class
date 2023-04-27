package com.ydskingdom.bank.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ydskingdom.bank.config.dummy.DummyObject;
import com.ydskingdom.bank.domain.account.Account;
import com.ydskingdom.bank.domain.account.AccountRepository;
import com.ydskingdom.bank.domain.user.User;
import com.ydskingdom.bank.domain.user.UserRepository;
import com.ydskingdom.bank.dto.ResponseDto;
import com.ydskingdom.bank.dto.account.AccountDepositReqDto;
import com.ydskingdom.bank.dto.account.AccountReqDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import javax.persistence.EntityManager;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Sql("classpath:db/teardown.sql")
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

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private EntityManager em;

    @BeforeEach
    public void setUp() {
        User ssar = userRepository.save(newUser("ssar", "쌀"));
        User cos = userRepository.save(newUser("cos", "코스,"));
        User love = userRepository.save(newUser("love", "러브"));
        User admin = userRepository.save(newUser("admin", "관리자"));

        Account ssarAccount1 = accountRepository.save(newAccount(1111L, ssar));
        Account cosAccount = accountRepository.save(newAccount(2222L, cos));
        Account loveAccount = accountRepository.save(newAccount(3333L, love));
        Account ssarAccount2 = accountRepository.save(newAccount(4444L, ssar));

        em.clear();
    }

    // jwt token -> 인증필터 -> 시큐리티 세션생성
    // setupBefore=TEST_METHOD (setUp 메서드 실행전에 수행)
    // setupBefore=TEST_EXECUTION (saveAccount_test 메서드 실행전에 수행)
    @WithUserDetails(value = "ssar", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    // 디비에서 username=ssar 조회를 해서
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

    @DisplayName("계좌 삭제 완료")
    @WithUserDetails(value = "ssar", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void deleteAccount_test1() throws Exception {
        Long accountNumber = 1111L;

        // when
        ResultActions resultActions = mockMvc
                .perform(delete("/api/s/account/" + accountNumber));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        //then
        resultActions.andExpect(status().isOk());
        ResponseDto responseDto = objectMapper.readValue(responseBody, ResponseDto.class);
        assertThat(responseDto.getMsg()).isEqualTo("계좌 삭제 완료");
        accountRepository.findByNumber(accountNumber);
    }

    @DisplayName("계좌를 찾을 수 없습니다")
    @WithUserDetails(value = "ssar", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void deleteAccount_test2() throws Exception {
        Long accountNumber = 1112L;

        // when
        ResultActions resultActions = mockMvc
                .perform(delete("/api/s/account/" + accountNumber));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();

        //then
        System.out.println("테스트 : " + responseBody);
        ResponseDto responseDto = objectMapper.readValue(responseBody, ResponseDto.class);
        assertThat(responseDto.getMsg()).isEqualTo("계좌를 찾을 수 없습니다");
    }

    @DisplayName("계좌 소유자가 아닙니다")
    @WithUserDetails(value = "ssar", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void deleteAccount_test3() throws Exception {
        Long accountNumber = 2222L;

        // when
        ResultActions resultActions = mockMvc
                .perform(delete("/api/s/account/" + accountNumber));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();

        //then
        System.out.println("테스트 : " + responseBody);
        ResponseDto responseDto = objectMapper.readValue(responseBody, ResponseDto.class);
        assertThat(responseDto.getMsg()).isEqualTo("계좌 소유자가 아닙니다");
    }

    @Test
    public void depositAccount_test() throws Exception {
        // given
        AccountDepositReqDto accountDepositReqDto = new AccountDepositReqDto();
        accountDepositReqDto.setNumber(1111L);
        accountDepositReqDto.setAmount(100L);
        accountDepositReqDto.setGubun("DEPOSIT");
        accountDepositReqDto.setTel("01088887777");

        String requestBody = objectMapper.writeValueAsString(accountDepositReqDto);
        System.out.println("테스트 : " + requestBody);

        // when
        ResultActions resultActions = mockMvc
                .perform(post("/api/account/deposit").content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(status().isCreated());
    }
}