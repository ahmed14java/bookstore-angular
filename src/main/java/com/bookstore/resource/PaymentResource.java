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
import com.bookstore.domain.UserBilling;
import com.bookstore.domain.UserPayment;
import com.bookstore.service.UserPaymentService;
import com.bookstore.service.UserService;

@RestController
@RequestMapping("/payment")
public class PaymentResource {

	@Autowired
	private UserService userService;

	@Autowired
	private UserPaymentService userPaymentService;

	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public ResponseEntity addNewCreditCard(@RequestBody UserPayment userPayment, Principal principal) {

		User user = userService.findByUsername(principal.getName());
		UserBilling userBilling = userPayment.getUserBilling();

		userService.updateUserBilling(userBilling, userPayment, user);

		return new ResponseEntity<>("Payment Add(Updated) Successfully!", HttpStatus.OK);
	}

	@RequestMapping(value = "/remove/{id}", method = RequestMethod.POST)
	public ResponseEntity removePayment(@PathVariable Long id, Principal principal) {

		// User user = userSer
		userPaymentService.removeById(id);
		return new ResponseEntity<>("Payment Removed Successfully!", HttpStatus.OK);
	}

	@RequestMapping(value = "/setDefault/{id}", method = RequestMethod.POST)
	public ResponseEntity setDefaultPayment(@PathVariable Long id) {
		//User user = new User();
		//if(null != principal) {
		//user = userService.findByUsername(principal.getName());
		userService.setDefaultPayment(id);
		return new ResponseEntity<>("Payment Set default Successfully!", HttpStatus.OK);
		//}else {
			//return new ResponseEntity<>("field set default!", HttpStatus.BAD_REQUEST);
		//}
	}

	@RequestMapping(value = "/getUserPaymentList", method = RequestMethod.GET)
	public List<UserPayment> getUserPaymentList(Principal principal) {
		User user = userService.findByUsername(principal.getName());
		List<UserPayment> userPaymentList = user.getUserPaymentList();
		return userPaymentList;
	}

}
