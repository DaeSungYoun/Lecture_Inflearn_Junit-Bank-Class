package com.ydskingdom.bank.dto.user;

import com.ydskingdom.bank.domain.user.User;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

public class UserResDto {
    @ToString
    @Getter
    @Setter
    public static class JoinResDto{
        private Long id;
        private String username;
        private String fullname;

        public JoinResDto(User user) {
            this.id = user.getId();
            this.username = user.getUsername();
            this.fullname = user.getFullname();
        }
    }
}
