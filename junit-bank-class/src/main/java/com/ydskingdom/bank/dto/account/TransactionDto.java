package com.ydskingdom.bank.dto.account;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ydskingdom.bank.domain.transaction.Transaction;
import com.ydskingdom.bank.util.CustomDateUtil;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TransactionDto {
    private Long id;
    private String gubun;
    private String sender;
    private String reciver;
    private Long amount;
    private String tel;
    private String createdAt;
    @JsonIgnore
    private Long depositAccountBalance; // 클라이언트에게 전달X -> 서비스단에서 테스트 용도

    public TransactionDto(Transaction transaction) {
        this.id = transaction.getId();
        this.gubun = transaction.getGubun().getValue();
        this.sender = transaction.getSender();
        this.reciver = transaction.getReceiver();
        this.amount = transaction.getAmount();
        this.depositAccountBalance = transaction.getDepositAccountBalance();
        this.tel = transaction.getTel();
        this.createdAt = CustomDateUtil.toStringFormat(transaction.getCreatedAt());
    }
}
