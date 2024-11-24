package cz.cyberrange.platform.training.feedback;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class TrainingFeedbackRestApplication {

    public static void main(String[] args) {
        SpringApplication.run(TrainingFeedbackRestApplication.class, args);
    }
}
