package team.themoment.readygsmserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class ReadygsmServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReadygsmServerApplication.class, args);
    }

}
