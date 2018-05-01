package com.bookstore.resource;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.security.Principal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.bookstore.config.SecurityConfig;
import com.bookstore.config.SecurityUtility;
import com.bookstore.domain.Book;
import com.bookstore.domain.User;
import com.bookstore.domain.security.Role;
import com.bookstore.domain.security.UserRole;
import com.bookstore.service.UserService;
import com.bookstore.utility.MailConstructor;

@RestController
@RequestMapping("/user")
public class UserResource {

	@Autowired
	private UserService userService;

	@Autowired
	private MailConstructor mailConstructor;

	@Autowired
	private JavaMailSender mailSender;
	
	@PostMapping("/uploadFile")
	public ResponseEntity fileUpload(MultipartHttpServletRequest multipartRequest) {
		try {			
			Iterator<String> itr = multipartRequest.getFileNames();
		    MultipartFile multipart = multipartRequest.getFile(itr.next());
		    java.io.File convFile = new java.io.File( multipart.getOriginalFilename());
		        convFile.createNewFile(); 
		        FileOutputStream fos = new FileOutputStream("src/main/resources/static/image/book/" +convFile); 
		        fos.write(multipart.getBytes());
		        fos.close();
		        
			return new ResponseEntity<>("Upload successfully", HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>("Upload field", HttpStatus.BAD_REQUEST);
		}
	}

	/*
	@PostMapping("/uploadFile")
	public ResponseEntity fileUpload(HttpServletResponse response,
			HttpServletRequest request) {
		try {
			MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
			Iterator<String> it = multipartRequest.getFileNames();
			MultipartFile multipartFile = multipartRequest.getFile(it.next());
			String fileName = multipartFile.getOriginalFilename();
			byte[] bytes = multipartFile.getBytes();
			BufferedOutputStream stream = new BufferedOutputStream(
				  new FileOutputStream(new File("src/main/resources/static/image/book/" + fileName)));
			stream.write(bytes);
			stream.close();
			return new ResponseEntity<>("Upload successfully", HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>("Upload field", HttpStatus.BAD_REQUEST);
		}
	}
*/
	
	@RequestMapping(value = "/newUser", method = RequestMethod.POST)
	public ResponseEntity newUser(HttpServletRequest request, @RequestBody HashMap<String, String> mapper)
			throws Exception {
		String username = mapper.get("username");
		String email = mapper.get("email");

		if (userService.findByUsername(username) != null) {
			return new ResponseEntity<>("usernameExists", HttpStatus.BAD_REQUEST);
		}
		if (userService.findByEmail(email) != null) {
			return new ResponseEntity<>("emailExists", HttpStatus.BAD_REQUEST);
		}

		User user = new User();
		user.setUsername(username);
		user.setEmail(email);

		String password = SecurityUtility.randomPassword();
		String encryptedPassword = SecurityUtility.passwordEncoder().encode(password);

		user.setPassword(encryptedPassword);

		Role role = new Role();
		role.setRoleId(1);
		role.setName("ROLE_USER");

		Set<UserRole> userRoles = new HashSet<>();
		userRoles.add(new UserRole(role, user));

		userService.createUser(user, userRoles);

		SimpleMailMessage mail = mailConstructor.constructNewUserEmail(user, password);
		mailSender.send(mail);

		return new ResponseEntity<>("User added successfully!", HttpStatus.OK);

	}

	@RequestMapping(value = "/forgetPassword", method = RequestMethod.POST)
	public ResponseEntity forgetPassword(HttpServletRequest request, @RequestBody HashMap<String, String> mapper)
			throws Exception {
		User user = userService.findByEmail(mapper.get("email"));

		if (user == null) {
			return new ResponseEntity<>("Email not found", HttpStatus.BAD_REQUEST);
		}

		String password = SecurityUtility.randomPassword();
		String encryptedPassword = SecurityUtility.passwordEncoder().encode(password);

		user.setPassword(encryptedPassword);
		userService.save(user);
		SimpleMailMessage newEmail = mailConstructor.constructNewUserEmail(user, password);
		mailSender.send(newEmail);

		return new ResponseEntity<>("Email sent!", HttpStatus.OK);

	}

	@RequestMapping(value = "/updateUserInfo", method = RequestMethod.POST)
	public ResponseEntity profileInfo(@RequestBody HashMap<String, Object> mapper) throws Exception {

		int id = (Integer) mapper.get("id");
		String email = (String) mapper.get("email");
		String username = (String) mapper.get("username");
		String firstName = (String) mapper.get("firstName");
		String lastName = (String) mapper.get("lastName");
		String newPassword = (String) mapper.get("newPassword");
		String currentPassword = (String) mapper.get("currentPassword");

		User currentUser = userService.findById(Long.valueOf(id));

		if (currentUser == null) {
			throw new Exception("User not found");
		}

		if (userService.findByEmail(email) != null) {
			if (userService.findByEmail(email).getId() != currentUser.getId()) {
				return new ResponseEntity<>("Email not found!", HttpStatus.BAD_REQUEST);
			}
		}

		if (userService.findByUsername(username) != null) {
			if (userService.findByUsername(username).getId() != currentUser.getId()) {
				return new ResponseEntity<>("Username not found!", HttpStatus.BAD_REQUEST);
			}
		}

		SecurityConfig securityConfig = new SecurityConfig();

		BCryptPasswordEncoder passwordEncoder = SecurityUtility.passwordEncoder();
		String dbPassword = currentUser.getPassword();

		if (null != currentPassword)
			if (passwordEncoder.matches(currentPassword, dbPassword)) {
				if (newPassword != null && !newPassword.isEmpty() && !newPassword.equals("")) {
					currentUser.setPassword(passwordEncoder.encode(newPassword));
				}
				currentUser.setEmail(email);
			} else {
				return new ResponseEntity<>("Incorrect current password!", HttpStatus.BAD_REQUEST);
			}

		currentUser.setFirstName(firstName);
		currentUser.setLastName(lastName);
		currentUser.setUsername(username);

		userService.save(currentUser);
		return new ResponseEntity<>("Update Success", HttpStatus.OK);
	}

	@GetMapping("/getCurrentUser")
	public User getCurrentUser(Principal principal) {
		User user = new User();
		if(null != principal) {
			System.out.println(principal);
		user = userService.findByUsername(principal.getName());
		}
		return user;
	}

}
