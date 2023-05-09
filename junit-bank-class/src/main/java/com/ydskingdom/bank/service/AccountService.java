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

        return new AccountListResDto(userPS, accountListPS);
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

    @Transactional
    public AccountWithdrawResDto accountWithdraw(AccountWithdrawReqDto accountWithdrawReqDto, Long userId) {
        // 출금 금액 0원 체크
        if (accountWithdrawReqDto.getAmount() <= 0L) {
            throw new CustomApiException("0원 이하의 금액을 출금할 수 없습니다");
        }

        // 출금 계좌가 유효한지 체크
        Account withdrawAccountPS = accountRepository.findByNumber(accountWithdrawReqDto.getNumber()).orElseThrow(() -> new CustomApiException("계좌를 찾을 수 없습니다"));

        // 출금 소유자 확인(로그인한 사람과 동일한지)
        withdrawAccountPS.checkOwner(userId);

        // 출금계좌 비밀번호 확인
        withdrawAccountPS.checkSamePassword(accountWithdrawReqDto.getPassword());

        // 출금계좌 잔액 확인
        withdrawAccountPS.checkBalance(accountWithdrawReqDto.getAmount());

        // 출금하기
        withdrawAccountPS.withdraw(accountWithdrawReqDto.getAmount());

        // 거래내역 남기기(내 계좌에서 ATM으로 출금)
        Transaction transaction = Transaction.builder()
                .withdrawAccount(withdrawAccountPS)
                .depositAccount(null)
                .withdrawAccountBalance(withdrawAccountPS.getBalance())
                .depositAccountBalance(null)
                .amount(accountWithdrawReqDto.getAmount())
                .gubun(TransactionEnum.WITHDRAW)
                .sender(accountWithdrawReqDto.getNumber() + "")
                .receiver("ATM")
                .build();

        Transaction transactionPS = transactionRepository.save(transaction);

        // DTO 응답
        return new AccountWithdrawResDto(withdrawAccountPS, transactionPS);
    }

    @Transactional
    public AccountTransferRespDto accountTransfer(AccountTransferReqDto accountTransferReqDto, Long userId) {

        // 출금계좌와 입금계좌가 동일하면 안됨
        if (accountTransferReqDto.getWithdrawNumber().longValue() == accountTransferReqDto.getDepositNumber()
                .longValue()) {
            throw new CustomApiException("입출금계좌가 동일할 수 없습니다");
        }

        // 0원 체크
        if (accountTransferReqDto.getAmount() <= 0L) {
            throw new CustomApiException("0원 이하의 금액을 입금할 수 없습니다");
        }

        // 출금계좌 확인
        Account withdrawAccountPS = accountRepository.findByNumber(accountTransferReqDto.getWithdrawNumber())
                .orElseThrow(
                        () -> new CustomApiException("출금계좌를 찾을 수 없습니다"));

        // 입금계좌 확인
        Account depositAccountPS = accountRepository.findByNumber(accountTransferReqDto.getDepositNumber())
                .orElseThrow(
                        () -> new CustomApiException("입금계좌를 찾을 수 없습니다"));

        // 출금 소유자 확인 (로그인한 사람과 동일한지)
        withdrawAccountPS.checkOwner(userId);

        // 출금계좌 비빌번호 확인
        withdrawAccountPS.checkSamePassword(accountTransferReqDto.getWithdrawPassword());

        // 출금계좌 잔액 확인
        withdrawAccountPS.checkBalance(accountTransferReqDto.getAmount());

        // 이체하기
        withdrawAccountPS.withdraw(accountTransferReqDto.getAmount());
        depositAccountPS.deposit(accountTransferReqDto.getAmount());

        // 거래내역 남기기 (내 계좌에서 ATM으로 출금)
        Transaction transaction = Transaction.builder()
                .withdrawAccount(withdrawAccountPS)
                .depositAccount(depositAccountPS)
                .withdrawAccountBalance(withdrawAccountPS.getBalance())
                .depositAccountBalance(depositAccountPS.getBalance())
                .amount(accountTransferReqDto.getAmount())
                .gubun(TransactionEnum.TRANSFER)
                .sender(accountTransferReqDto.getWithdrawNumber() + "")
                .receiver(accountTransferReqDto.getDepositNumber() + "")
                .build();

        Transaction transactionPS = transactionRepository.save(transaction);

        // DTO응답
        return new AccountTransferRespDto(withdrawAccountPS, transactionPS);
    }


    public AccountDetailResDto 계좌상세보기(Long number, Long userId, Integer page) {
        // 1. 구분값, 페이지고정
        String gubun = "ALL";

        Account accountPS = accountRepository.findByNumber(number).orElseThrow(
                () -> new CustomApiException("계좌를 찾을 수 없습니다"));

        // 3. 계좌 소유자 확인
        accountPS.checkOwner(userId);

        // 4. 입출금목록보기
        List<Transaction> transactionList = transactionRepository.findTransactionList(accountPS.getId(), gubun, page);
        return new AccountDetailResDto(accountPS, transactionList);
    }
}
