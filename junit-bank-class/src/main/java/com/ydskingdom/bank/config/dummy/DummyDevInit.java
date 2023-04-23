package com.ydskingdom.bank.config.dummy;

import com.ydskingdom.bank.domain.user.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class DummyDevInit extends DummyObject {

    @Profile("dev")
    @Bean
    CommandLineRunner init(UserRepository userRepository) {
        return (args -> userRepository.save(newUser("ssar", "쌀")));
    }
}
