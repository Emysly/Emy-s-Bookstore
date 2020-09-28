package com.emysilva.bookstore;

import com.emysilva.bookstore.model.User;
import com.emysilva.bookstore.model.security.Role;
import com.emysilva.bookstore.model.security.UserRole;
import com.emysilva.bookstore.service.UserService;
import com.emysilva.bookstore.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.HashSet;
import java.util.Set;

@SpringBootApplication
public class BookstoreApplication implements CommandLineRunner {

	@Autowired
	private UserService userService;

	public static void main(String[] args) {
		SpringApplication.run(BookstoreApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		User user = new User();
		user.setUsername("emysilva");
		user.setEmail("emysly12@gmail.com");
		user.setFirstname("chukwuebuka");
		user.setLastname("emmanuel");
		user.setEnabled(true);
		user.setPhone("09040201098");
		user.setPassword(SecurityUtil.passwordEncoder().encode("swag4sure"));

		Set<UserRole> userRoles = new HashSet<>();

		Role role = new Role();
		role.setRoleId(1);
		role.setName("ROLE_USER");
		userRoles.add(new UserRole(user, role));

		User user1 = userService.createUser(user, userRoles);
	}
}
