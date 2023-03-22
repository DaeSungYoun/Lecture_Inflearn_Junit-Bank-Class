package com.ydskingdom.bank.service;

import com.ydskingdom.bank.domain.user.User;
import com.ydskingdom.bank.domain.user.UserRepository;
import com.ydskingdom.bank.dto.user.UserReqDto;
import com.ydskingdom.bank.dto.user.UserResDto;
import com.ydskingdom.bank.handler.exception.CustomApiException;
import lombok.RequiredArgsConstructor;
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
    public UserResDto.JoinResDto 회원가입(UserReqDto.JoinReqDto joinReqDto) {
        // 1. 동일 유저 네임 존재 체크
        Optional<User> userOptional = userRepository.findByUsername(joinReqDto.getUsername());
        if (userOptional.isPresent()) {
            throw new CustomApiException("동일한 username이 존재합니다");
        }

        // 2. 패스워드 인코딩
        User userPersistence = userRepository.save(joinReqDto.toEntity(bCryptPasswordEncoder));

        // 3. dto 응답
        return new UserResDto.JoinResDto(userPersistence);
    }


}
