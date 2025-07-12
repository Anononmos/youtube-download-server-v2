package com.example.YouTubeDL;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import me.paulschwarz.springdotenv.DotenvPropertySource;

@SpringBootApplication
@EnableTransactionManagement
public class YouTubeDownloaderApplication implements CommandLineRunner {

	public static void main(String[] args) {
		AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();

		// Add DotenvPropertySource to environment before registering components
		DotenvPropertySource.addToEnvironment(applicationContext.getEnvironment());
		applicationContext.refresh();

		SpringApplication.run(YouTubeDownloaderApplication.class, args);

		// Free up resources when run terminates
		applicationContext.close();
	}

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Override
	public void run(String ...strings) throws Exception {

		System.out.println("Connected database");
	}
}
