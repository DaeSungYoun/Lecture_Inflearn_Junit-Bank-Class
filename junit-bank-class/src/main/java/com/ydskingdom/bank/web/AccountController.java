package com.ydskingdom.bank.web;

import com.ydskingdom.bank.config.auth.LoginUser;
import com.ydskingdom.bank.dto.ResponseDto;
import com.ydskingdom.bank.dto.account.AccountReqDto;
import com.ydskingdom.bank.dto.account.AccountResDto;
import com.ydskingdom.bank.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RequestMapping("/api")
@RequiredArgsConstructor
@RestController
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/s/account")
    public ResponseEntity<?> saveAccount(
            @RequestBody @Valid AccountReqDto accountReqDto,
            BindingResult bindingResult,
            @AuthenticationPrincipal LoginUser loginUser) {
        AccountResDto accountResDto = accountService.accountSave(accountReqDto, loginUser.getUser().getId());

        return new ResponseEntity<>(new ResponseDto<>(1, "", accountResDto), HttpStatus.CREATED);
    }
}
