package com.ydskingdom.bank.dto.account;

import com.ydskingdom.bank.domain.account.Account;
import com.ydskingdom.bank.domain.user.User;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class AccountListResDto {
    private String fullName;

    private List<AccountDto> accounts = new ArrayList<>();

    public AccountListResDto(User user, List<Account> accounts) {
        this.fullName = user.getFullname();
        this.accounts = accounts.stream().map(account -> new AccountDto(account)).collect(Collectors.toList());
    }
}
