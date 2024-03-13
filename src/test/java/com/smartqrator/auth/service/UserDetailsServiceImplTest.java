package com.smartqrator.auth.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import com.smartqrator.auth.dto.model.User;
import com.smartqrator.auth.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class UserDetailsServiceImplTest {

	@InjectMocks
	UserDetailsServiceImpl test;

	@Mock
	UserRepository userRepository;

	@Test
	public void loadUserByUsernameTest() {
		Optional<User> user = Optional.of(new User("dummy", "dummy@email.com", "dummypass"));
		when(userRepository.findByUsername("dummy")).thenReturn(user);
		UserDetails userDetails = test.loadUserByUsername("dummy");
		assertEquals("dummy", userDetails.getUsername());
	}

}
