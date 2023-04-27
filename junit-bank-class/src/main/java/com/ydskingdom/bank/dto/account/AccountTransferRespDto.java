package com.ydskingdom.bank.dto.account;

import com.ydskingdom.bank.domain.account.Account;
import com.ydskingdom.bank.domain.transaction.Transaction;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AccountTransferRespDto {
    private Long id; // 계좌 ID
    private Long number; // 계좌번호
    private Long balance; // 출금 계좌 잔액
    private TransactionDto transaction;

    public AccountTransferRespDto(Account account, Transaction transaction) {
        this.id = account.getId();
        this.number = account.getNumber();
        this.balance = account.getBalance();
        this.transaction = new TransactionDto(transaction);
    }
}
