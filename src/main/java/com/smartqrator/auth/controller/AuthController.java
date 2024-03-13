package com.smartqrator.auth.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smartqrator.auth.dto.model.Role;
import com.smartqrator.auth.dto.model.User;
import com.smartqrator.auth.dto.model.UserRoleEnum;
import com.smartqrator.auth.dto.request.LoginRequest;
import com.smartqrator.auth.dto.request.SignupRequest;
import com.smartqrator.auth.dto.response.MessageResponse;
import com.smartqrator.auth.dto.response.UserInfoResponse;
import com.smartqrator.auth.jwt.JwtUtil;
import com.smartqrator.auth.repository.RoleRepository;
import com.smartqrator.auth.repository.UserRepository;
import com.smartqrator.auth.service.UserDetailsImpl;

import jakarta.validation.Valid;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {

	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	UserRepository userRepository;

	@Autowired
	RoleRepository roleRepository;

	@Autowired
	PasswordEncoder encoder;

	@Autowired
	JwtUtil jwtUtil;

	/**
	 * Sign Up/ Registration
	 * 
	 * Check existing user/email Create new User with user role Save User to
	 * Database
	 * 
	 * @return
	 */
	@PostMapping("/signup")
	public ResponseEntity<MessageResponse> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
		if (userRepository.existsByUsername(signUpRequest.getUsername())) {
		      return ResponseEntity
		          .badRequest()
		          .body(new MessageResponse("Error: Username is already taken!"));
		    }

		    if (userRepository.existsByEmail(signUpRequest.getEmail())) {
		      return ResponseEntity
		          .badRequest()
		          .body(new MessageResponse("Error: Email is already in use!"));
		    }

		    User user = new User(signUpRequest.getUsername(), 
		                         signUpRequest.getEmail(),
		                         encoder.encode(signUpRequest.getPassword()));

		    Set<String> strRoles = signUpRequest.getRoles();
		    Set<Role> roles = new HashSet<>();

		    if (strRoles == null) {
		      Role userRole = roleRepository.findByType(UserRoleEnum.ROLE_USER)
		          .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
		      roles.add(userRole);
		    } else {
		      strRoles.forEach(role -> {
		        switch (role) {
		        case "admin":
		          Role adminRole = roleRepository.findByType(UserRoleEnum.ROLE_ADMIN)
		              .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
		          roles.add(adminRole);

		          break;
		        case "mod":
		          Role modRole = roleRepository.findByType(UserRoleEnum.ROLE_MODERATOR)
		              .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
		          roles.add(modRole);

		          break;
		        default:
		          Role userRole = roleRepository.findByType(UserRoleEnum.ROLE_USER)
		              .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
		          roles.add(userRole);
		        }
		      });
		    }

		    user.setRoles(roles);
		    userRepository.save(user);

		    return ResponseEntity.ok(
		    		new MessageResponse("User registered successfully!"));
		  }

	/**
	 * Sign In/ Login
	 * 
	 * Authenticate username & password update SecurityContext using Authentication
	 * Object generated JWT get User details from authentication Object response
	 * contains JWT in Cookies and User Details Data
	 * 
	 * @return
	 */
	@PostMapping("/signin")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(authentication);

		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

		ResponseCookie jwtCookie = jwtUtil.generateJwtCookie(userDetails);

		List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority())
				.collect(Collectors.toList());

		return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
				.body(new UserInfoResponse(userDetails.getId(), 
						userDetails.getUsername(), 
						userDetails.getEmail(), roles));

	}

	/**
	 * Sign Out/ Logout Clears cookies
	 * TODO
	 * @return
	 */
	@PostMapping("/signout")
	public ResponseEntity<?> logoutUser() {
		return ResponseEntity
		        .ok()
		        .header(HttpHeaders.SET_COOKIE, jwtUtil.getCleanJwtCookie().toString())
		        .build();

	}

}
