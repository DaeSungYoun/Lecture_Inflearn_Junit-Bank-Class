package com.ydskingdom.bank.dto.account;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter
@Setter
public class AccountWithdrawReqDto {
    @NotNull
    @Digits(integer = 4, fraction = 4)
    private Long number;

    @NotNull
    @Digits(integer = 4, fraction = 4)
    private Long password;

    @NotNull
    private Long amount;

    @NotEmpty
    @Pattern(regexp = "WITHDRAW")
    private String gubun;
}
