package com.ydskingdom.bank.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ydskingdom.bank.config.dummy.DummyObject;
import com.ydskingdom.bank.domain.account.Account;
import com.ydskingdom.bank.domain.account.AccountRepository;
import com.ydskingdom.bank.domain.transaction.Transaction;
import com.ydskingdom.bank.domain.transaction.TransactionRepository;
import com.ydskingdom.bank.domain.user.User;
import com.ydskingdom.bank.domain.user.UserRepository;
import com.ydskingdom.bank.dto.ResponseDto;
import com.ydskingdom.bank.dto.account.AccountDepositReqDto;
import com.ydskingdom.bank.dto.account.AccountReqDto;
import com.ydskingdom.bank.dto.account.AccountTransferReqDto;
import com.ydskingdom.bank.dto.account.AccountWithdrawReqDto;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Sql("classpath:db/teardown.sql")
@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class AccountControllerTest extends DummyObject {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private EntityManager em;

    @BeforeEach
    public void setUp() {
        dataSetting();
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

    @DisplayName("계좌 출금 테스트")
    @WithUserDetails(value = "ssar", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void withdrawAccount_test() throws Exception {
        // given
        Long withdrawAccountNumber = 1111L;
        Long withdrawAccountpassword = 1234L;
        Long withdrawAmount = 100L;
        AccountWithdrawReqDto accountWithdrawReqDto = newMockAccountWithdrawReqDto(withdrawAccountNumber, withdrawAccountpassword, withdrawAmount);

        String requestBody = objectMapper.writeValueAsString(accountWithdrawReqDto);
        System.out.println("테스트 : " + requestBody);

        // when
        ResultActions resultActions = mockMvc
                .perform(post("/api/s/account/withdraw").content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(status().isCreated());
    }

    @DisplayName("계좌 이체 테스트")
    @WithUserDetails(value = "ssar", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void transferAccount_test() throws Exception {
        // given
        Long withdrawAccountNumber = 1111L;
        Long depositAccountNumber = 2222L;
        Long withdrawAccountpassword = 1234L;
        Long transferAmount = 200L;
        AccountTransferReqDto accountTransferReqDto = newMockAccountTransferReqDto(withdrawAccountNumber, depositAccountNumber, withdrawAccountpassword, transferAmount);

        String requestBody = objectMapper.writeValueAsString(accountTransferReqDto);
        System.out.println("테스트 : " + requestBody);

        // when
        ResultActions resultActions = mockMvc
                .perform(post("/api/s/account/transfer").content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(status().isCreated());
    }



    @WithUserDetails(value = "ssar", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void findDetailAccount_test() throws Exception {
        // given
        Long number = 1111L;
        String page = "0";

        // when
        ResultActions resultActions = mockMvc
                .perform(get("/api/s/account/" + number).param("page", page));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // then
        resultActions.andExpect(jsonPath("$.data.transactions[0].balance").value(900L));
        resultActions.andExpect(jsonPath("$.data.transactions[1].balance").value(800L));
        resultActions.andExpect(jsonPath("$.data.transactions[2].balance").value(700L));
        resultActions.andExpect(jsonPath("$.data.transactions[3].balance").value(800L));
    }

    private void dataSetting() {
        User ssar = userRepository.save(newUser("ssar", "쌀"));
        User cos = userRepository.save(newUser("cos", "코스,"));
        User love = userRepository.save(newUser("love", "러브"));
        User admin = userRepository.save(newUser("admin", "관리자"));

        Account ssarAccount1 = accountRepository.save(newAccount(1111L, ssar));
        Account cosAccount = accountRepository.save(newAccount(2222L, cos));
        Account loveAccount = accountRepository.save(newAccount(3333L, love));
        Account ssarAccount2 = accountRepository.save(newAccount(4444L, ssar));

        Transaction withdrawTransaction1 = transactionRepository
                .save(newWithdrawTransaction(ssarAccount1, accountRepository));
        Transaction depositTransaction1 = transactionRepository
                .save(newDepositTransaction(cosAccount, accountRepository));
        Transaction transferTransaction1 = transactionRepository
                .save(newTransferTransaction(ssarAccount1, cosAccount, accountRepository));
        Transaction transferTransaction2 = transactionRepository
                .save(newTransferTransaction(ssarAccount1, loveAccount, accountRepository));
        Transaction transferTransaction3 = transactionRepository
                .save(newTransferTransaction(cosAccount, ssarAccount1, accountRepository));
    }
}