package com.bookstore.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bookstore.domain.User;
import com.bookstore.domain.UserBilling;
import com.bookstore.domain.UserPayment;
import com.bookstore.domain.UserShipping;
import com.bookstore.domain.security.UserRole;
import com.bookstore.repository.RoleRepository;
import com.bookstore.repository.UserBillingRepository;
import com.bookstore.repository.UserPaymentRepository;
import com.bookstore.repository.UserRepository;
import com.bookstore.repository.UserShippingRepository;
import com.bookstore.service.UserService;

@Service
public class UserServiceImpl implements UserService {

	private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private UserBillingRepository userBillingRepository;

	@Autowired
	private UserPaymentRepository UserPaymentRepository;
	
	@Autowired
	private UserShippingRepository userShippingRepository;

	@Override
	@Transactional
	public User createUser(User user, Set<UserRole> userRoles) {

		User localUser = userRepository.findByUsername(user.getUsername());

		if (localUser != null) {
			LOG.info("User with username already exist. No thing will be done. ", user.getUsername());
		} else {
			for (UserRole ur : userRoles) {
				roleRepository.save(ur.getRole());
			}
			user.getUserRoles().addAll(userRoles);
			user.setUserPaymentList(new ArrayList<UserPayment>());

			localUser = userRepository.save(user);
		}
		return localUser;
	}

	@Override
	public User findByUsername(String username) {
		User user = userRepository.findByUsername(username);
		return user;
	}

	@Override
	public User findByEmail(String email) {
		User user = userRepository.findByEmail(email);
		return user;
	}

	@Override
	public User save(User user) {
		return userRepository.save(user);
	}

	@Override
	public User findById(Long id) {
		User user = userRepository.findOne(id);
		return user;
	}

	@Override
	public void updateUserPaymentInfo(UserBilling userBilling, UserPayment userPayment, User user) {
		save(user);
		userBillingRepository.save(userBilling);
		UserPaymentRepository.save(userPayment);
	}

	@Override
	public void updateUserBilling(UserBilling userBilling, UserPayment userPayment, User user) {
		userPayment.setUser(user);
		userPayment.setUserBilling(userBilling);
		userPayment.setDefaultPayment(true);
		userBilling.setUserPayment(userPayment);
		user.getUserPaymentList().add(userPayment);
		save(user);
	}

	@Override
	public void setDefaultPayment(Long id) {
		List<UserPayment> userPaymentList = (List<UserPayment>) UserPaymentRepository.findAll();
		for (UserPayment userPayment : userPaymentList) {
			if (userPayment.getId() == id) {
				userPayment.setDefaultPayment(true);
				UserPaymentRepository.save(userPayment);
			} else {
				userPayment.setDefaultPayment(false);
				UserPaymentRepository.save(userPayment);
			}
		}
	}

	@Override
	public void updateUserShipping(UserShipping userShipping, User user) {
		userShipping.setUser(user);
		userShipping.setUserShippingDefault(true);
		user.getUserShippingList().add(userShipping);
		save(user);
	}

	@Override
	public void setDefaultShipping(Long id) {
		List<UserShipping> userShippingList = (List<UserShipping>) userShippingRepository.findAll();
		for (UserShipping userShipping : userShippingList) {
			if(userShipping.getId() == id) {
				userShipping.setUserShippingDefault(true);
				userShippingRepository.save(userShipping);
			}else {
				userShipping.setUserShippingDefault(false);
				userShippingRepository.save(userShipping);
			}
		}
		
	}

}
