package com.ydskingdom.bank.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ydskingdom.bank.config.dummy.DummyObject;
import com.ydskingdom.bank.domain.account.Account;
import com.ydskingdom.bank.domain.account.AccountRepository;
import com.ydskingdom.bank.domain.transaction.Transaction;
import com.ydskingdom.bank.domain.transaction.TransactionRepository;
import com.ydskingdom.bank.domain.user.User;
import com.ydskingdom.bank.domain.user.UserRepository;
import com.ydskingdom.bank.dto.account.*;
import com.ydskingdom.bank.handler.exception.CustomApiException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest extends DummyObject {

    @InjectMocks
    AccountService accountService;

    @Mock
    UserRepository userRepository;

    @Mock
    AccountRepository accountRepository;

    @Mock
    TransactionRepository transactionRepository;

    @Spy
    ObjectMapper objectMapper;

    @Test
    void 계좌등록_테스트() throws JsonProcessingException {
        //given
        Long userId = 1L;

        AccountReqDto accountReqDto = new AccountReqDto();
        accountReqDto.setNumber(1111L);
        accountReqDto.setPassword(1234L);

        //stub 1
        User user = newMockUser(userId, "ssar", "쌀");
        when(userRepository.findById(any())).thenReturn(Optional.of(user));

        //stub 2
        when(accountRepository.findByNumber(any())).thenReturn(Optional.empty());

        //stbu 3
        Account account = newMockAccount(userId, 1111L, 1000L, user);
        when(accountRepository.save(any())).thenReturn(account);

        //when
        AccountResDto accountResDto = accountService.accountSave(accountReqDto, userId);
        String responseBody = objectMapper.writeValueAsString(accountResDto);
        System.out.println("responseBody = " + responseBody);

        //then
        assertThat(accountResDto.getNumber()).isEqualTo(1111L);
    }

    @Test
    void 계좌등록_유저를_찾을_수_없습니다() {
        //given
        Long userId = 1L;

        AccountReqDto accountReqDto = new AccountReqDto();
        accountReqDto.setNumber(1111L);
        accountReqDto.setPassword(1234L);

        //stub 1
        when(userRepository.findById(any())).thenThrow(new CustomApiException("유저를 찾을 수 없습니다."));

        //when

        //then
        assertThatThrownBy(() -> accountService.accountSave(accountReqDto, userId)).isInstanceOf(CustomApiException.class)
                .hasMessage("유저를 찾을 수 없습니다.");
    }

    @Test
    void 계좌등록_해당_계좌가_이미_존재합니다() {
        //given
        Long userId = 1L;

        AccountReqDto accountReqDto = new AccountReqDto();
        accountReqDto.setNumber(1111L);
        accountReqDto.setPassword(1234L);

        //stub 1
        User user = newMockUser(userId, "ssar", "쌀");
        when(userRepository.findById(any())).thenReturn(Optional.of(user));

        //stub 2
        Account account = newMockAccount(userId, 1111L, 1000L, user);
        when(accountRepository.findByNumber(any())).thenReturn(Optional.of(account));

        assertThatThrownBy(() -> accountService.accountSave(accountReqDto, userId)).isInstanceOf(CustomApiException.class)
                .hasMessage("해당 계좌가 이미 존재합니다");
    }

    @Test
    void 계좌목록보기_유저별_test() {
        // given
        Long userId = 1L;

        // stub
        User ssar = newMockUser(1L, "ssar", "쌀");
        when(userRepository.findById(userId)).thenReturn(Optional.of(ssar));

        Account ssarAccount1 = newMockAccount(1L, 1111L, 1000L, ssar);
        Account ssarAccount2 = newMockAccount(2L, 2222L, 1000L, ssar);
        List<Account> accountList = Arrays.asList(ssarAccount1, ssarAccount2);
        when(accountRepository.findByUser_Id(any())).thenReturn(accountList);

        // when
        AccountListResDto accountListRespDto = accountService.accsountListByUserId(userId);

        // then
        Assertions.assertThat(accountListRespDto.getFullName()).isEqualTo("쌀");
        Assertions.assertThat(accountListRespDto.getAccounts().size()).isEqualTo(2);
    }

    @Test
    void 계좌삭제_test1() {
        // given
        Long number = 1111L;
        Long userId = 2L;

        // when
        assertThatThrownBy(() -> accountService.accountDelete(number, userId)).isExactlyInstanceOf(CustomApiException.class)
                .hasMessage("계좌를 찾을 수 없습니다");
    }

    @Test
    void 계좌삭제_test2()  {
        // given
        Long number = 1111L;
        Long userId = 2L;

        // stub
        User ssar = newMockUser(1L, "ssar", "쌀");
        Account ssarAccount = newMockAccount(1L, 1111L, 1000L, ssar);
        when(accountRepository.findByNumber(any())).thenReturn(Optional.of(ssarAccount));

        // when
        assertThatThrownBy(() -> accountService.accountDelete(number, userId)).isExactlyInstanceOf(CustomApiException.class)
                .hasMessage("계좌 소유자가 아닙니다");
    }

    // Account -> balance 변경됐는지
    // Trasction -> balance 잘 기록됐는지
    @Test
    void 계좌입금_test() {
        // given
        AccountDepositReqDto accountDepositReqDto = new AccountDepositReqDto();
        accountDepositReqDto.setNumber(1111L);
        accountDepositReqDto.setAmount(100L);
        accountDepositReqDto.setGubun("DEPOSIT");
        accountDepositReqDto.setTel("01088887777");

        // stub 1L
        User ssar = newMockUser(1L, "ssar", "쌀"); // 실행됨
        Account ssarAccount1 = newMockAccount(1L, 1111L, 1000L, ssar); // 실행됨 - ssarAccount1 -> 1000원
        when(accountRepository.findByNumber(any())).thenReturn(Optional.of(ssarAccount1)); // 실행안됨 -> service호출후 실행됨 ->
        // 1100원

        // stub 2 (스텁이 진행될 때 마다 연관된 객체는 새로 만들어서 주입하기 - 타이밍 때문에 꼬인다)
        Account ssarAccount2 = newMockAccount(1L, 1111L, 1000L, ssar); // 실행됨 - ssarAccount1 -> 1000원
        Transaction transaction = newMockDepositTransaction(1L, ssarAccount2);
        when(transactionRepository.save(any())).thenReturn(transaction); // 실행안됨

        // when
        AccountDepositResDto accountDepositResDto = accountService.accountDeposit(accountDepositReqDto);
        System.out.println("테스트 : 트랜잭션 입금계좌 잔액 : " + accountDepositResDto.getTransaction().getDepositAccountBalance());
        System.out.println("테스트 : 계좌쪽 잔액 : " + ssarAccount1.getBalance());
        System.out.println("테스트 : 계좌쪽 잔액 : " + ssarAccount2.getBalance());

        // then
        assertThat(ssarAccount1.getBalance()).isEqualTo(1100L);
        assertThat(accountDepositResDto.getTransaction().getDepositAccountBalance()).isEqualTo(1100L);
    }

    @DisplayName("계좌출금_실패 : 0원 이하의 금액을 출금할 수 없습니다")
    @Test
    void 계좌출금_실패_test1() {
        //given
        Long loginUserId = 1L;
        Long withdrawAccountNumber = 1111L;
        Long withdrawAccountpassword = 1234L;
        Long withdrawAmount = 0L;
        AccountWithdrawReqDto accountWithdrawReqDto = newMockAccountWithdrawReqDto(withdrawAccountNumber, withdrawAccountpassword, withdrawAmount);

        //when
        //then
        assertThatThrownBy(() -> accountService.accountWithdraw(accountWithdrawReqDto, loginUserId))
                .isExactlyInstanceOf(CustomApiException.class)
                .hasMessage("0원 이하의 금액을 출금할 수 없습니다");
    }

    @DisplayName("계좌출금_실패 : 계좌를 찾을 수 없습니다")
    @Test
    void 계좌출금_실패_test2() {
        //given
        Long loginUserId = 1L;
        Long withdrawAccountNumber = 1111L;
        Long withdrawAccountpassword = 1234L;
        Long withdrawAmount = 100L;
        AccountWithdrawReqDto accountWithdrawReqDto = newMockAccountWithdrawReqDto(withdrawAccountNumber, withdrawAccountpassword, withdrawAmount);

        //when
        //then
        assertThatThrownBy(() -> accountService.accountWithdraw(accountWithdrawReqDto, loginUserId))
                .isExactlyInstanceOf(CustomApiException.class)
                .hasMessage("계좌를 찾을 수 없습니다");
    }

    @DisplayName("계좌출금_실패 : 계좌 소유자가 아닙니다")
    @Test
    void 계좌출금_실패_test3() {
        //given
        Long loginUserId = 2L;
        Long withdrawAccountNumber = 1111L;
        Long withdrawAccountpassword = 1234L;
        Long withdrawAmount = 100L;
        AccountWithdrawReqDto accountWithdrawReqDto = newMockAccountWithdrawReqDto(withdrawAccountNumber, withdrawAccountpassword, withdrawAmount);

        //when
        //stub
        User user = newMockUser(1L, "ssar", "쌀");
        Account account = newMockAccount(1L, 1111L, 1000L, user);
        when(accountRepository.findByNumber(any())).thenReturn(Optional.of(account));

        assertThatThrownBy(() -> accountService.accountWithdraw(accountWithdrawReqDto, loginUserId))
                .isExactlyInstanceOf(CustomApiException.class)
                .hasMessage("계좌 소유자가 아닙니다");
    }

    @DisplayName("계좌출금_실패 : 계좌 비밀번호 검증에 실패했습니다")
    @Test
    void 계좌출금_실패_test4() {
        //given
        Long loginUserId = 1L;
        Long withdrawAccountNumber = 1111L;
        Long withdrawAccountpassword = 1111L;
        Long withdrawAmount = 100L;
        AccountWithdrawReqDto accountWithdrawReqDto = newMockAccountWithdrawReqDto(withdrawAccountNumber, withdrawAccountpassword, withdrawAmount);

        //when
        //stub
        User user = newMockUser(1L, "ssar", "쌀");
        Account account = newMockAccount(1L, 1111L, 1000L, user);
        when(accountRepository.findByNumber(any())).thenReturn(Optional.of(account));

        assertThatThrownBy(() -> accountService.accountWithdraw(accountWithdrawReqDto, loginUserId))
                .isExactlyInstanceOf(CustomApiException.class)
                .hasMessage("계좌 비밀번호 검증에 실패했습니다");
    }

    @DisplayName("계좌출금_실패 : 계좌 잔액이 부족합니다")
    @Test
    void 계좌출금_실패_test5() {
        //given
        Long loginUserId = 1L;
        Long withdrawAccountNumber = 1111L;
        Long withdrawAccountpassword = 1234L;
        Long withdrawAmount = 1100L;
        AccountWithdrawReqDto accountWithdrawReqDto = newMockAccountWithdrawReqDto(withdrawAccountNumber, withdrawAccountpassword, withdrawAmount);

        //when
        //stub
        User user = newMockUser(1L, "ssar", "쌀");
        Account account = newMockAccount(1L, 1111L, 1000L, user);
        when(accountRepository.findByNumber(any())).thenReturn(Optional.of(account));

        assertThatThrownBy(() -> accountService.accountWithdraw(accountWithdrawReqDto, loginUserId))
                .isExactlyInstanceOf(CustomApiException.class)
                .hasMessage("계좌 잔액이 부족합니다");
    }

    @DisplayName("계좌출금_성공")
    @Test
    void 계좌출금_성공_test5() throws JsonProcessingException {
        /*
        1111 계좌에는 1000원이 들어있음
        1111 계좌번호에서 100원 출금
        1111 계좌는 900원이 남아야함
         */
        //given
        Long loginUserId = 1L;
        Long withdrawAccountNumber = 1111L;
        Long withdrawAccountpassword = 1234L;
        Long withdrawAmount = 100L;
        AccountWithdrawReqDto accountWithdrawReqDto = newMockAccountWithdrawReqDto(withdrawAccountNumber, withdrawAccountpassword, withdrawAmount);

        //when
        //stub
        User user = newMockUser(1L, "ssar", "쌀");
        Account account = newMockAccount(1L, 1111L, 1000L, user);
        when(accountRepository.findByNumber(any())).thenReturn(Optional.of(account));

        //stub
        Account account2 = newMockAccount(1L, 1111L, 1000L, user);
        Transaction transaction = newMockWithdrawTransaction(1L, account2);
        when(transactionRepository.save(any())).thenReturn(transaction);

        AccountWithdrawResDto accountWithdrawResDto = accountService.accountWithdraw(accountWithdrawReqDto, loginUserId);

        //then
        String responseBody = objectMapper.writeValueAsString(accountWithdrawResDto);
        System.out.println("responseBody = " + responseBody);
        assertThat(accountWithdrawResDto.getBalance()).isEqualTo(900L);
    }

    @DisplayName("계좌이체_실패 : 입출금계좌가 동일할 수 없습니다")
    @Test
    void 계좌이체_실패_test1() {
        //given
        Long loginUserId = 1L;
        Long withdrawAccountNumber = 1111L;
        Long depositAccountNumber = 1111L;
        Long withdrawAccountpassword = 1234L;
        Long transferAmount = 100L;
        AccountTransferReqDto accountTransferReqDto = newMockAccountTransferReqDto(withdrawAccountNumber, depositAccountNumber, withdrawAccountpassword, transferAmount);

        //when
        //then
        assertThatThrownBy(() -> accountService.accountTransfer(accountTransferReqDto, loginUserId))
                .isExactlyInstanceOf(CustomApiException.class)
                .hasMessage("입출금계좌가 동일할 수 없습니다");
    }

    @DisplayName("계좌이체_실패 : 0원 이하의 금액을 입금할 수 없습니다")
    @Test
    void 계좌이체_실패_test2() {
        //given
        Long loginUserId = 1L;
        Long withdrawAccountNumber = 1111L;
        Long depositAccountNumber = 2222L;
        Long withdrawAccountpassword = 1234L;
        Long transferAmount = 0L;
        AccountTransferReqDto accountTransferReqDto = newMockAccountTransferReqDto(withdrawAccountNumber, depositAccountNumber, withdrawAccountpassword, transferAmount);

        //when
        //then
        assertThatThrownBy(() -> accountService.accountTransfer(accountTransferReqDto, loginUserId))
                .isExactlyInstanceOf(CustomApiException.class)
                .hasMessage("0원 이하의 금액을 입금할 수 없습니다");
    }

    @DisplayName("계좌이체_실패 : 출금계좌를 찾을 수 없습니다")
    @Test
    void 계좌이체_실패_test3() {
        //given
        Long loginUserId = 1L;
        Long withdrawAccountNumber = 11111L;
        Long depositAccountNumber = 2222L;
        Long withdrawAccountpassword = 1234L;
        Long transferAmount = 100L;
        AccountTransferReqDto accountTransferReqDto = newMockAccountTransferReqDto(withdrawAccountNumber, depositAccountNumber, withdrawAccountpassword, transferAmount);

        //when
        assertThatThrownBy(() -> accountService.accountTransfer(accountTransferReqDto, loginUserId))
                .isExactlyInstanceOf(CustomApiException.class)
                .hasMessage("출금계좌를 찾을 수 없습니다");
    }

    @DisplayName("계좌이체_실패 : 입금계좌를 찾을 수 없습니다")
    @Test
    void 계좌이체_실패_test4() {
        //given
        Long loginUserId = 1L;
        Long withdrawAccountNumber = 1111L;
        Long depositAccountNumber = 22222L;
        Long withdrawAccountpassword = 1234L;
        Long transferAmount = 100L;
        AccountTransferReqDto accountTransferReqDto = newMockAccountTransferReqDto(withdrawAccountNumber, depositAccountNumber, withdrawAccountpassword, transferAmount);

        //when
        //stub
        User user = newMockUser(1L, "ssar", "쌀");
        Account withdrawAccount = newMockAccount(1L, 1111L, 1000L, user);
        when(accountRepository.findByNumber(withdrawAccountNumber)).thenReturn(Optional.of(withdrawAccount));

        assertThatThrownBy(() -> accountService.accountTransfer(accountTransferReqDto, loginUserId))
                .isExactlyInstanceOf(CustomApiException.class)
                .hasMessage("입금계좌를 찾을 수 없습니다");
    }

    @DisplayName("계좌이체_실패 : 계좌 소유자가 아닙니다")
    @Test
    void 계좌이체_실패_test5() {
        //given
        Long loginUserId = 2L;
        Long withdrawAccountNumber = 1111L;
        Long depositAccountNumber = 2222L;
        Long withdrawAccountpassword = 1234L;
        Long transferAmount = 100L;
        AccountTransferReqDto accountTransferReqDto = newMockAccountTransferReqDto(withdrawAccountNumber, depositAccountNumber, withdrawAccountpassword, transferAmount);

        //when
        //stub
        User user = newMockUser(1L, "ssar", "쌀");
        Account withdrawAccount = newMockAccount(1L, 1111L, 1000L, user);
        when(accountRepository.findByNumber(withdrawAccountNumber)).thenReturn(Optional.of(withdrawAccount));

        //stub
        User user2 = newMockUser(2L, "cos", "코스");
        Account depositAccount = newMockAccount(2L, 2222L, 1000L, user2);
        when(accountRepository.findByNumber(depositAccountNumber)).thenReturn(Optional.of(depositAccount));

        assertThatThrownBy(() -> accountService.accountTransfer(accountTransferReqDto, loginUserId))
                .isExactlyInstanceOf(CustomApiException.class)
                .hasMessage("계좌 소유자가 아닙니다");
    }

    @DisplayName("계좌이체_실패 : 계좌 비밀번호 검증에 실패했습니다")
    @Test
    void 계좌이체_실패_test6() {
        //given
        Long loginUserId = 1L;
        Long withdrawAccountNumber = 1111L;
        Long depositAccountNumber = 2222L;
        Long withdrawAccountpassword = 11234L;
        Long transferAmount = 100L;
        AccountTransferReqDto accountTransferReqDto = newMockAccountTransferReqDto(withdrawAccountNumber, depositAccountNumber, withdrawAccountpassword, transferAmount);

        //when
        //stub
        User user = newMockUser(1L, "ssar", "쌀");
        Account withdrawAccount = newMockAccount(1L, 1111L, 1000L, user);
        when(accountRepository.findByNumber(withdrawAccountNumber)).thenReturn(Optional.of(withdrawAccount));

        //stub
        User user2 = newMockUser(2L, "cos", "코스");
        Account depositAccount = newMockAccount(2L, 2222L, 1000L, user2);
        when(accountRepository.findByNumber(depositAccountNumber)).thenReturn(Optional.of(depositAccount));

        assertThatThrownBy(() -> accountService.accountTransfer(accountTransferReqDto, loginUserId))
                .isExactlyInstanceOf(CustomApiException.class)
                .hasMessage("계좌 비밀번호 검증에 실패했습니다");
    }

    @DisplayName("계좌이체_실패 : 계좌 잔액이 부족합니다")
    @Test
    void 계좌이체_실패_test7() {
        //given
        Long loginUserId = 1L;
        Long withdrawAccountNumber = 1111L;
        Long depositAccountNumber = 2222L;
        Long withdrawAccountpassword = 1234L;
        Long transferAmount = 200L;
        AccountTransferReqDto accountTransferReqDto = newMockAccountTransferReqDto(withdrawAccountNumber, depositAccountNumber, withdrawAccountpassword, transferAmount);

        //when
        //stub
        User user = newMockUser(1L, "ssar", "쌀");
        Account withdrawAccount = newMockAccount(1L, 1111L, 100L, user);
        when(accountRepository.findByNumber(withdrawAccountNumber)).thenReturn(Optional.of(withdrawAccount));

        //stub
        User user2 = newMockUser(2L, "cos", "코스");
        Account depositAccount = newMockAccount(2L, 2222L, 1000L, user2);
        when(accountRepository.findByNumber(depositAccountNumber)).thenReturn(Optional.of(depositAccount));

        assertThatThrownBy(() -> accountService.accountTransfer(accountTransferReqDto, loginUserId))
                .isExactlyInstanceOf(CustomApiException.class)
                .hasMessage("계좌 잔액이 부족합니다");
    }

    @DisplayName("계좌이체_성공")
    @Test
    void 계좌이체_성공_test() throws JsonProcessingException {
        //given
        Long loginUserId = 1L;
        Long withdrawAccountNumber = 1111L;
        Long depositAccountNumber = 2222L;
        Long withdrawAccountpassword = 1234L;
        Long transferAmount = 200L;
        AccountTransferReqDto accountTransferReqDto = newMockAccountTransferReqDto(withdrawAccountNumber, depositAccountNumber, withdrawAccountpassword, transferAmount);

        //when
        //stub
        User user = newMockUser(1L, "ssar", "쌀");
        Account withdrawAccount = newMockAccount(1L, 1111L, 1000L, user);
        when(accountRepository.findByNumber(withdrawAccountNumber)).thenReturn(Optional.of(withdrawAccount));

        //stub
        User user2 = newMockUser(2L, "cos", "코스");
        Account depositAccount = newMockAccount(2L, 2222L, 1000L, user2);
        when(accountRepository.findByNumber(depositAccountNumber)).thenReturn(Optional.of(depositAccount));

        //stub
        Transaction transaction = newMockTransferTransaction(1L, accountTransferReqDto.getAmount(), withdrawAccount, depositAccount);
        when(transactionRepository.save(any())).thenReturn(transaction);

        AccountTransferRespDto accountTransferRespDto = accountService.accountTransfer(accountTransferReqDto, loginUserId);

        //then
        String responseBody = objectMapper.writeValueAsString(accountTransferRespDto);
        System.out.println("responseBody = " + responseBody);
        assertThat(accountTransferRespDto.getBalance()).isEqualTo(800L);
    }
}