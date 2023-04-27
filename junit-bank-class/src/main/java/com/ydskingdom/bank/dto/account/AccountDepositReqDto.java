package com.ydskingdom.bank.dto.account;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Setter
@Getter
public class AccountDepositReqDto {
    @NotNull
    @Digits(integer = 4, fraction = 4)
    private Long number;

    @NotNull
    private Long amount; // 0원 유효성 검사

    @NotEmpty
    @Pattern(regexp = "DEPOSIT")
    private String gubun; // DEPOSIT

    @NotEmpty
    @Pattern(regexp = "^[0-9]{11}")
    private String tel;
}
