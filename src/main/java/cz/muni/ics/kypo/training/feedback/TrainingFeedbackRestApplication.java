package cz.muni.ics.kypo.training.feedback;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.Properties;

@SpringBootApplication
@EnableTransactionManagement
public class TrainingFeedbackRestApplication {
    public static void main(String[] args) {
        SpringApplication.run(TrainingFeedbackRestApplication.class, args);
    }

}
