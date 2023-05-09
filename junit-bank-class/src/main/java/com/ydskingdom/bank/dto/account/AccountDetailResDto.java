package com.ydskingdom.bank.dto.account;

import com.ydskingdom.bank.domain.account.Account;
import com.ydskingdom.bank.domain.transaction.Transaction;
import com.ydskingdom.bank.util.CustomDateUtil;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class AccountDetailResDto {private Long id; // 계좌 ID
    private Long number; // 계좌번호
    private Long balance; // 그 계좌의 최종 잔액
    private List<TransactionDto> transactions = new ArrayList<>();

    public AccountDetailResDto(Account account, List<Transaction> transactions) {
        this.id = account.getId();
        this.number = account.getNumber();
        this.balance = account.getBalance();
        this.transactions = transactions.stream()
                .map((transaction) -> new TransactionDto(transaction, account.getNumber()))
                .collect(Collectors.toList());
    }

    @Getter
    @Setter
    public class TransactionDto {

        private Long id;
        private String gubun;
        private Long amount;

        private String sender;
        private String reciver;

        private String tel;
        private String createdAt;
        private Long balance;

        public TransactionDto(Transaction transaction, Long accountNumber) {
            this.id = transaction.getId();
            this.gubun = transaction.getGubun().getValue();
            this.amount = transaction.getAmount();
            this.sender = transaction.getSender();
            this.reciver = transaction.getReceiver();
            this.createdAt = CustomDateUtil.toStringFormat(transaction.getCreatedAt());
            this.tel = transaction.getTel() == null ? "없음" : transaction.getTel();

            if (transaction.getDepositAccount() == null) {
                this.balance = transaction.getWithdrawAccountBalance();
            } else if (transaction.getWithdrawAccount() == null) {
                this.balance = transaction.getDepositAccountBalance();
            } else {
                if (accountNumber.longValue() == transaction.getDepositAccount().getNumber().longValue()) {
                    this.balance = transaction.getDepositAccountBalance();
                } else {
                    this.balance = transaction.getWithdrawAccountBalance();
                }
            }

        }
    }
}
