package nl.uva.alexandria;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableAdminServer
@SpringBootApplication
public class AlexandriaApplication {

    public static void main(String[] args) {
        SpringApplication.run(AlexandriaApplication.class, args);
    }
}
