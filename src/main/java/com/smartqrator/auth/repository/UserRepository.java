package com.smartqrator.auth.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.smartqrator.auth.dto.model.User;

public interface UserRepository extends MongoRepository<User, String> {

	Boolean existsByUsername(String username);

	Boolean existsByEmail(String email);

	Optional<User> findByUsername(String username);

}
