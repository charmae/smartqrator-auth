package com.smartqrator.auth.service;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.AuthenticationException;

import jakarta.servlet.ServletException;

@ExtendWith(MockitoExtension.class)
public class AuthenticationEntryPointTest {

	@InjectMocks
	AuthenticationEntryPointImpl test;

	@Mock
	AuthenticationException authException;

	@Test
	public void commenceTest() throws IOException, ServletException {

		MockHttpServletResponse response = new MockHttpServletResponse();
		test.commence(null, response, authException);
		assertTrue(response.getErrorMessage().contains("Error: Unauthorized"));
	}

}
