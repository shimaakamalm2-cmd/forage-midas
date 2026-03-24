package com.jpmc.midascore;


import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.TransactionRecordRepository;
import com.jpmc.midascore.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;


@Service
public class TransactionRecordService {
    private final TransactionRecordRepository transactionRecordRepository;
    private final UserRepository userRepository;
    final RestTemplate restTemplate;

    public TransactionRecordService(TransactionRecordRepository transactionRecordRepository, UserRepository userRepository, RestTemplate restTemplate) {
        this.transactionRecordRepository = transactionRecordRepository;
        this.userRepository = userRepository;
        this.restTemplate = restTemplate;
    }


    @Transactional
    void save(Transaction kafkaTransaction) {
        if (userRepository.existsById(kafkaTransaction.getRecipientId()) && userRepository.existsById(kafkaTransaction.getSenderId()) &&
                (userRepository.findById(kafkaTransaction.getSenderId())).getBalance()>=kafkaTransaction.getAmount()
        ){
            Incentive incentive = restTemplate.postForObject("http://localhost:8080/incentive", kafkaTransaction, Incentive.class);
            float bonus = Objects.requireNonNull(incentive).getAmount();
            // getting sender+ receiver
            UserRecord sender= userRepository.findById(kafkaTransaction.getSenderId());
            UserRecord recipient = userRepository.findById(kafkaTransaction.getRecipientId());

            //updating sender
            sender.setBalance(sender.getBalance() - kafkaTransaction.getAmount());
            userRepository.save(sender);
            //updating the receiver
            recipient.setBalance(recipient.getBalance() + kafkaTransaction.getAmount() + bonus);
            userRepository.save(recipient);
            //saving transaction in database
            TransactionRecord transactionRecord = new TransactionRecord();
            transactionRecord.setSender(sender);
            transactionRecord.setRecipient(recipient);
            transactionRecord.setAmount(kafkaTransaction.getAmount());
            transactionRecord.setIncentive(bonus);
            transactionRecordRepository.save(transactionRecord);
            //saving the transaction in database
            //making transaction record to save it
        }

    }

}
