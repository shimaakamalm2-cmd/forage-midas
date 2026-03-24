package com.jpmc.midascore;

import com.jpmc.midascore.entity.UserRecord;

import com.jpmc.midascore.foundation.Balance;
import com.jpmc.midascore.repository.UserRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class MidasController {
    UserRepository userRepository;

    public MidasController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/balance")
    public Balance getBalance(@RequestParam Long userId) {

           float amount = userRepository.findById(userId)
                   .map(UserRecord::getBalance)
                   .orElse(0.0f);
        System.out.println((new Balance(amount)).toString());
           return new Balance(amount);
    }
}
