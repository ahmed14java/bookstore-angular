package com.bookstore;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.bookstore.config.SecurityUtility;
import com.bookstore.domain.User;
import com.bookstore.domain.security.Role;
import com.bookstore.domain.security.UserRole;
import com.bookstore.service.UserService;

@SpringBootApplication
public class BookstoreAngularApplication implements CommandLineRunner{
	
	@Autowired
	private UserService userService ;

	public static void main(String[] args) {
		SpringApplication.run(BookstoreAngularApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		User user1 = new User();
		user1.setFirstName("Ahmed");
		user1.setLastName("Hassan");
		user1.setUsername("ahmed14java");
		user1.setPhone("01013509917");
		user1.setPassword(SecurityUtility.passwordEncoder().encode("Rehan2015"));
		user1.setEmail("ahmed14java@gmail.com");
		Set<UserRole> userRoles = new HashSet<>();
		Role role1 = new Role();
		role1.setRoleId(1);
		role1.setName("ROLE_USER");
		userRoles.add(new UserRole(role1, user1));
		userService.createUser(user1, userRoles);
		userRoles.clear();
		
		User user2 = new User();
		user2.setFirstName("Omar");
		user2.setLastName("Hosny");
		user2.setUsername("omar");
		user2.setPassword(SecurityUtility.passwordEncoder().encode("Rehan2015"));
		user2.setEmail("omar@gmail.com");
		Role role2 = new Role();
		role2.setRoleId(0);
		role2.setName("ROLE_ADMIN");
		userRoles.add(new UserRole(role2, user2));
		userService.createUser(user2, userRoles);
		userRoles.clear();
	}
}
