package com.ydskingdom.bank.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ydskingdom.bank.config.dummy.DummyObject;
import com.ydskingdom.bank.domain.account.Account;
import com.ydskingdom.bank.domain.account.AccountRepository;
import com.ydskingdom.bank.domain.user.User;
import com.ydskingdom.bank.domain.user.UserRepository;
import com.ydskingdom.bank.dto.account.AccountListResDto;
import com.ydskingdom.bank.dto.account.AccountReqDto;
import com.ydskingdom.bank.dto.account.AccountResDto;
import com.ydskingdom.bank.handler.exception.CustomApiException;
import org.assertj.core.api.Assertions;
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
    public void 계좌목록보기_유저별_test() {
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
    public void 계좌삭제_test1() {
        // given
        Long number = 1111L;
        Long userId = 2L;

        // when
        assertThatThrownBy(() -> accountService.accountDelete(number, userId)).isExactlyInstanceOf(CustomApiException.class)
                .hasMessage("계좌를 찾을 수 없습니다");
    }



    @Test
    public void 계좌삭제_test2()  {
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

}