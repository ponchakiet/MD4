package re.eurekaserviceclient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class EurekaServiceClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(EurekaServiceClientApplication.class, args);
    }

}
