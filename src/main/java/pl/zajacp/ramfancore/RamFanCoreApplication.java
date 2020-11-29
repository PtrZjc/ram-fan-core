package pl.zajacp.ramfancore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "pl.zajacp.ramfancore")
public class RamFanCoreApplication {

	public static void main(String[] args) {
		SpringApplication.run(RamFanCoreApplication.class, args);
	}

}
