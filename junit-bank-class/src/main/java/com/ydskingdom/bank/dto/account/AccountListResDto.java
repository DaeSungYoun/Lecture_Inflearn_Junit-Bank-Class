package com.ydskingdom.bank.dto.account;

import com.ydskingdom.bank.domain.account.Account;
import com.ydskingdom.bank.domain.user.User;

import java.util.List;
import java.util.stream.Collectors;

public class AccountListResDto {
    private String fullName;

    private List<AccountDto> accounts;

    public AccountListResDto(User user, List<Account> accounts) {
        this.fullName = user.getFullname();
        this.accounts = accounts.stream().map(account -> new AccountDto(account)).collect(Collectors.toList());
    }
}
