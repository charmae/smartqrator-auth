package com.smartqrator.auth.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.smartqrator.auth.dto.model.Role;
import com.smartqrator.auth.dto.model.UserRoleEnum;

public interface RoleRepository extends MongoRepository<Role, String> {
	  Optional<Role> findByType(UserRoleEnum type);
	}