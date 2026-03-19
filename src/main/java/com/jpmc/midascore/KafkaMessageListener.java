package com.jpmc.midascore;
import com.jpmc.midascore.foundation.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;



@Component
 class KafkaMessageListener {
    private final Logger logger = LoggerFactory.getLogger(KafkaMessageListener.class);

    @KafkaListener(
            topics = "${general.kafka-topic}",
            id = "trader-101",
            properties = {"spring.json.value.default.type=com.jpmc.midascore.foundation.Transaction", "auto.offset.reset=earliest"}
    )
    public void consume(Transaction transaction) {
        logger.info("Consuming transaction {}", transaction.getAmount());
    }
}

