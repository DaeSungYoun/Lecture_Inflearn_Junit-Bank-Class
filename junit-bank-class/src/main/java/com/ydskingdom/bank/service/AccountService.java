package com.ydskingdom.bank.service;

import com.ydskingdom.bank.domain.account.Account;
import com.ydskingdom.bank.domain.account.AccountRepository;
import com.ydskingdom.bank.domain.transaction.Transaction;
import com.ydskingdom.bank.domain.transaction.TransactionEnum;
import com.ydskingdom.bank.domain.transaction.TransactionRepository;
import com.ydskingdom.bank.domain.user.User;
import com.ydskingdom.bank.domain.user.UserRepository;
import com.ydskingdom.bank.dto.account.*;
import com.ydskingdom.bank.handler.exception.CustomApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class AccountService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public AccountListResDto accsountListByUserId(Long userId) {
        User userPS = userRepository.findById(userId).orElseThrow(() -> new CustomApiException("유저를 찾을 수 없습니다."));

        List<Account> accountListPS = accountRepository.findByUser_Id(userId);

        AccountListResDto accountListResDto = new AccountListResDto(userPS, accountListPS);
        return accountListResDto;
    }

    @Transactional
    public AccountResDto accountSave(AccountReqDto accountReqDto, Long userId) {
        User userPS = userRepository.findById(userId).orElseThrow(() -> new CustomApiException("유저를 찾을 수 없습니다."));

        Optional<Account> accountOP = accountRepository.findByNumber(accountReqDto.getNumber());

        if (accountOP.isPresent()) {
            throw new CustomApiException("해당 계좌가 이미 존재합니다");
        }

        Account accountPS = accountRepository.save(accountReqDto.toEntity(userPS));

        return new AccountResDto(accountPS);
    }

    @Transactional
    public void accountDelete(Long accountNumber, Long userId) {
        Account accountPS = accountRepository.findByNumber(accountNumber).orElseThrow(() -> new CustomApiException("계좌를 찾을 수 없습니다"));

        accountPS.checkOwner(userId);

        accountRepository.deleteById(accountPS.getId());
    }

    @Transactional
    public AccountDepositResDto accountDeposit(AccountDepositReqDto accountDepositReqDto) {
        if (accountDepositReqDto.getAmount() <= 0L) {
            throw new CustomApiException("0원 이하의 금액을 입금할 수 없습니다");
        }

        Account depositAccountPS = accountRepository.findByNumber(accountDepositReqDto.getNumber()).orElseThrow(() -> new CustomApiException("계좌를 찾을 수 없습니다"));

        depositAccountPS.deposit(accountDepositReqDto.getAmount());

        Transaction transaction = Transaction.builder()
                .withdrawAccount(null)
                .depositAccount(depositAccountPS)
                .withdrawAccountBalance(null)
                .depositAccountBalance(depositAccountPS.getBalance())
                .amount(accountDepositReqDto.getAmount())
                .gubun(TransactionEnum.DEPOSIT)
                .sender("ATM")
                .receiver(accountDepositReqDto.getNumber() + "")
                .tel(accountDepositReqDto.getTel())
                .build();

        Transaction transactionPS = transactionRepository.save(transaction);

        return new AccountDepositResDto(depositAccountPS, transactionPS);
    }
}
