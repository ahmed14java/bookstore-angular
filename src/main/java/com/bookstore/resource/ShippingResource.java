package com.bookstore.resource;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.bookstore.domain.User;
import com.bookstore.domain.UserShipping;
import com.bookstore.service.UserService;
import com.bookstore.service.UserShippingService;

@RestController
@RequestMapping("/shipping")
public class ShippingResource {

	@Autowired
	private UserService userService;

	@Autowired
	private UserShippingService userShippingService;

	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public ResponseEntity addNewUserShippingPost(@RequestBody UserShipping userShipping, Principal principal) {

		User user = userService.findByUsername(principal.getName());
		if (user != null) {
			userService.updateUserShipping(userShipping, user);
		}
		return new ResponseEntity<>("Shipping Added(update) Successfully!", HttpStatus.OK);
	}

	@RequestMapping("/getUserShippingList")
	public List<UserShipping> getUserShippingList(Principal principal) {
		List<UserShipping> userShippingList = null;
		User user = userService.findByUsername(principal.getName());
		if (user != null) {
			userShippingList = user.getUserShippingList();
		}
		return userShippingList;
	}

	@RequestMapping(value = "/remove/{id}", method = RequestMethod.POST)
	public ResponseEntity removeUserShippingPost(@PathVariable Long id, Principal principal) {
		userShippingService.removeById(id);
		return new ResponseEntity<>("Shipping remove successfully", HttpStatus.OK);
	}

	@RequestMapping(value = "/setDefault/{id}", method = RequestMethod.POST)
	public ResponseEntity setDefaultUserShippingPost(@PathVariable Long id) {
		try {
			userService.setDefaultShipping(id);
			return new ResponseEntity<>("Set default Shipping successfully", HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
		
		
	}
}
