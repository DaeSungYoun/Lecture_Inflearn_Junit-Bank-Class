package com.ydskingdom.bank.service;

import com.ydskingdom.bank.domain.user.User;
import com.ydskingdom.bank.domain.user.UserEnum;
import com.ydskingdom.bank.domain.user.UserRepository;
import com.ydskingdom.bank.handler.exception.CustomApiException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Transactional // 메서드가 시작 될 때 트랜잭션이 시작, 메서드가 종료 될 떄 트랜잭션 종료
    public JoinResDto 회원가입(JoinReqDto joinReqDto) {
        // 1. 동일 유저 네임 존재 체크
        Optional<User> userOptional = userRepository.findByUsername(joinReqDto.username);
        if (userOptional.isPresent()) {
            throw new CustomApiException("동일한 username이 존재합니다");
        }

        // 2. 패스워드 인코딩
        User userPersistence = userRepository.save(joinReqDto.toEntity(bCryptPasswordEncoder));

        // 3. dto 응답
        return new JoinResDto(userPersistence);
    }

    @Getter
    @Setter
    public static class JoinReqDto{
        private String username;
        private String password;
        private String email;
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
