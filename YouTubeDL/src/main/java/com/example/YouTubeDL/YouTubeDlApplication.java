package com.example.YouTubeDL;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import me.paulschwarz.springdotenv.DotenvPropertySource;

@SpringBootApplication
public class YouTubeDlApplication {

	public static void main(String[] args) {
		AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();

		// Add DotenvPropertySource to environment before registering components
		DotenvPropertySource.addToEnvironment(applicationContext.getEnvironment());
		applicationContext.refresh();

		SpringApplication.run(YouTubeDlApplication.class, args);

		// Free up resources when run terminates
		applicationContext.close();
	}

}
