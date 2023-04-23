package com.ydskingdom.bank.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ydskingdom.bank.config.dummy.DummyObject;
import com.ydskingdom.bank.domain.account.Account;
import com.ydskingdom.bank.domain.account.AccountRepository;
import com.ydskingdom.bank.domain.user.User;
import com.ydskingdom.bank.domain.user.UserRepository;
import com.ydskingdom.bank.dto.account.AccountReqDto;
import com.ydskingdom.bank.dto.account.AccountResDto;
import com.ydskingdom.bank.handler.exception.CustomApiException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

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

}