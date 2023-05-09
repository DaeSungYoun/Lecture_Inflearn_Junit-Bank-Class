package com.ydskingdom.bank.service;

import com.ydskingdom.bank.domain.account.Account;
import com.ydskingdom.bank.domain.account.AccountRepository;
import com.ydskingdom.bank.domain.transaction.Transaction;
import com.ydskingdom.bank.domain.transaction.TransactionRepository;
import com.ydskingdom.bank.dto.transaction.TransactionListResDto;
import com.ydskingdom.bank.handler.exception.CustomApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    public TransactionListResDto 입출금목록보기(Long userId, Long accountNumber, String gubun, int page) {
        Account accountPS = accountRepository.findByNumber(accountNumber)
                .orElseThrow(() -> new CustomApiException("해당 계좌를 찾을 수 없습니다"));

        accountPS.checkOwner(userId);

        List<Transaction> transactionListPS = transactionRepository.findTransactionList(accountPS.getId(), gubun, page);
        return new TransactionListResDto(transactionListPS, accountPS);
    }
}
