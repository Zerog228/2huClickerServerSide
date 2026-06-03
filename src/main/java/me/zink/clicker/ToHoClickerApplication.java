package me.zink.clicker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashSet;
import java.util.Set;

@SpringBootApplication()
public class ToHoClickerApplication {
	public static final Set<String> PASSWORDS = new HashSet<>();

	public static void main(String[] args) {
		try(BufferedReader reader = new BufferedReader(new FileReader(new File(ToHoClickerApplication.class.getClassLoader().getResource("static/200_popular_passwords.txt").toURI())))){
			String password;
			while((password = reader.readLine()) != null){
				PASSWORDS.add(password.toUpperCase());
			}
		}catch (Exception e){
			e.printStackTrace();
		}

		SpringApplication.run(ToHoClickerApplication.class, args);
	}

}
