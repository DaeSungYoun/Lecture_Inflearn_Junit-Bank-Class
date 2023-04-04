package com.ydskingdom.bank.dto.user;

import com.ydskingdom.bank.domain.user.User;
import com.ydskingdom.bank.domain.user.UserEnum;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.validation.constraints.NotEmpty;

public class UserReqDto {
    @Getter
    @Setter
    public static class JoinReqDto{
        @NotEmpty
        private String username;

        @NotEmpty
        private String password;

        @NotEmpty
        private String email;

        @NotEmpty
        private String fullname;

        public User toEntity(BCryptPasswordEncoder bCryptPasswordEncoder) {
            return User.builder()
                    .username(username)
                    .password(bCryptPasswordEncoder.encode(password))
                    .email(email)
                    .fullname(fullname)
                    .role(UserEnum.CUSTOMER)
                    .build();
        }
    }
}
