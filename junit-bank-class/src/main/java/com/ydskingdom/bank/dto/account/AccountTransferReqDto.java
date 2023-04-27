package com.ydskingdom.bank.dto.account;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Setter
@Getter
public class AccountTransferReqDto {
    @NotNull
    @Digits(integer = 4, fraction = 4)
    private Long withdrawNumber;

    @NotNull
    @Digits(integer = 4, fraction = 4)
    private Long depositNumber;

    @NotNull
    @Digits(integer = 4, fraction = 4)
    private Long withdrawPassword;

    @NotNull
    private Long amount;

    @NotEmpty
    @Pattern(regexp = "TRANSFER")
    private String gubun;
}
