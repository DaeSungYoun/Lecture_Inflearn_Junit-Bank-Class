package com.ydskingdom.bank.web;

import com.ydskingdom.bank.config.auth.LoginUser;
import com.ydskingdom.bank.dto.ResponseDto;
import com.ydskingdom.bank.dto.account.AccountListResDto;
import com.ydskingdom.bank.dto.account.AccountReqDto;
import com.ydskingdom.bank.dto.account.AccountResDto;
import com.ydskingdom.bank.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequestMapping("/api")
@RequiredArgsConstructor
@RestController
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/s/account/login-user")
    public ResponseEntity<?> findUserAccount(@AuthenticationPrincipal LoginUser loginUser) {
        AccountListResDto accountListResDto = accountService.accsountListByUserId(loginUser.getUser().getId());
        return new ResponseEntity<>(new ResponseDto<>(1, "계좌목록보기_유저별 성공", accountListResDto), HttpStatus.OK);
    }

    @PostMapping("/s/account")
    public ResponseEntity<?> saveAccount(
            @RequestBody @Valid AccountReqDto accountReqDto,
            BindingResult bindingResult,
            @AuthenticationPrincipal LoginUser loginUser) {
        AccountResDto accountResDto = accountService.accountSave(accountReqDto, loginUser.getUser().getId());

        return new ResponseEntity<>(new ResponseDto<>(1, "계좌등록 성공", accountResDto), HttpStatus.CREATED);
    }

    @DeleteMapping("/s/account/{accountNumber}")
    public ResponseEntity<?> deleteAccount(@PathVariable Long accountNumber, @AuthenticationPrincipal LoginUser loginUser) {

        accountService.accountDelete(accountNumber, loginUser.getUser().getId());

        return new ResponseEntity<>(new ResponseDto<>(1, "계좌 삭제 완료", null), HttpStatus.OK);
    }
}
