package com.smartqrator.auth.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;

import com.smartqrator.auth.jwt.AuthTokenFilter;
import com.smartqrator.auth.service.AuthenticationEntryPointImpl;
import com.smartqrator.auth.service.UserDetailsServiceImpl;

@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = { WebSecurityConfig.class })
public class WebSecurityConfigTest {

	@InjectMocks
	WebSecurityConfig webSecurityConfig;

	@Mock
	UserDetailsServiceImpl userDetailsService;

	@Mock
	AuthenticationEntryPointImpl unauthorizedHandler;

	@Mock
	AuthTokenFilter authTokenFilter;

	@Mock
	AuthenticationConfiguration authConfig;

	@Mock
	HttpSecurity httpSecurity;

	@Test
	public void passwordEncoderTest() throws Exception {
		final PasswordEncoder encoder = webSecurityConfig.passwordEncoder();
		final String encodedPassword = encoder.encode("password"); 
		assertNotNull(encodedPassword);
		assertTrue(encoder.matches("password", encodedPassword));
	}

	@Test
	public void authenticationProviderTest() {
		assertNotNull(webSecurityConfig.authenticationProvider());
	}

	@Test
	public void authenticationManagerTest() throws Exception {
		AuthenticationManager authManager = webSecurityConfig.authenticationManager(authConfig);
		assertEquals(authConfig.getAuthenticationManager(), authManager);
	}

}
