package com.jpmc.midascore;


import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.TransactionRecordRepository;
import com.jpmc.midascore.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class TransactionRecordService {
    private final TransactionRecordRepository transactionRecordRepository;
    private final UserRepository userRepository;

    public TransactionRecordService(TransactionRecordRepository transactionRecordRepository, UserRepository userRepository) {
        this.transactionRecordRepository = transactionRecordRepository;
        this.userRepository = userRepository;
    }


    @Transactional
    void save(Transaction kafkaTransaction) {
        if (userRepository.existsById(kafkaTransaction.getRecipientId()) && userRepository.existsById(kafkaTransaction.getSenderId()) &&
                (userRepository.findById(kafkaTransaction.getSenderId())).getBalance()>=kafkaTransaction.getAmount()
        ){

            //sender+ receiver
            UserRecord sender= userRepository.findById(kafkaTransaction.getSenderId());
            UserRecord recipient = userRepository.findById(kafkaTransaction.getRecipientId());
            //making transaction record to save it
            TransactionRecord transactionRecord = new TransactionRecord();
            transactionRecord.setSender(sender);
            transactionRecord.setRecipient(recipient);
            transactionRecord.setAmount(kafkaTransaction.getAmount());
            System.out.println("the amount is " + kafkaTransaction.getAmount());
            //updating sender
            System.out.println("balance of sender before: "+ sender.getBalance());
            sender.setBalance(sender.getBalance() - kafkaTransaction.getAmount());
            System.out.println("balance of sender after: "+ sender.getBalance());
            userRepository.save(sender);
            //updating the receiver
            System.out.println("balance of receiver before: " + recipient.getBalance());
            recipient.setBalance(recipient.getBalance()+kafkaTransaction.getAmount());
            userRepository.save(recipient);
            System.out.println("balance after receiver after : " + recipient.getBalance());
            //saving the transaction in database
            transactionRecordRepository.save(transactionRecord);
            System.out.println("Transaction record saved successfully");
            System.out.println(sender.toString());
            System.out.println(recipient.toString());
        }

    }

}
