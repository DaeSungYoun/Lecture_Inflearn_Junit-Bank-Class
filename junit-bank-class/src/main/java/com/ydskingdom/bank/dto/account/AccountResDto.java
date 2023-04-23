package com.ydskingdom.bank.dto.account;

import com.ydskingdom.bank.domain.account.Account;
import lombok.Getter;
import lombok.Setter;

@Getter
public class AccountResDto {
    private Long id;
    private Long number;
    private Long balance;

    public AccountResDto(Account account) {
        this.id = account.getId();
        this.number = account.getNumber();
        this.balance = account.getBalance();
    }
}
