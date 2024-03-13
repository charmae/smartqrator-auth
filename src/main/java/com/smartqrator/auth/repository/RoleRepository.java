package com.smartqrator.auth.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.smartqrator.auth.dto.UserRoleEnum;
import com.smartqrator.auth.dto.model.Role;

public interface RoleRepository extends MongoRepository<Role, String> {
	  Optional<Role> findByType(UserRoleEnum type);
	}