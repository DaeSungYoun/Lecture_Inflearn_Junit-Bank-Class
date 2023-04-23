package com.ydskingdom.bank.dto.account;

import com.ydskingdom.bank.domain.account.Account;
import lombok.Getter;

@Getter
public class AccountDto {
    private Long id;
    private Long number;
    private Long balance;

    public AccountDto(Account account) {
        this.id = id;
        this.number = number;
        this.balance = balance;
    }
}
