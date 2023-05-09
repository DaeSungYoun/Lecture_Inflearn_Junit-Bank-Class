package com.ydskingdom.bank.dto.account;

import com.ydskingdom.bank.domain.account.Account;
import com.ydskingdom.bank.domain.transaction.Transaction;
import com.ydskingdom.bank.util.CustomDateUtil;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AccountWithdrawResDto {
    private Long id; // 계좌 ID
    private Long number; // 계좌번호
    private Long balance; // 잔액
    private TransactionDto transaction;

    public AccountWithdrawResDto(Account account, Transaction transaction) {
        this.id = account.getId();
        this.number = account.getNumber();
        this.balance = account.getBalance();
        this.transaction = new TransactionDto(transaction);
    }

    @Setter
    @Getter
    public class TransactionDto {
        private Long id;
        private String gubun;
        private String sender;
        private String reciver;
        private Long amount;
        private String createdAt;

        public TransactionDto(Transaction transaction) {
            this.id = transaction.getId();
            this.gubun = transaction.getGubun().getValue();
            this.sender = transaction.getSender();
            this.reciver = transaction.getReceiver();
            this.amount = transaction.getAmount();
            this.createdAt = CustomDateUtil.toStringFormat(transaction.getCreatedAt());
        }
    }
}
