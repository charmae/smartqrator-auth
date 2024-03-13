package com.smartqrator.auth.dto.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.smartqrator.auth.dto.UserRoleEnum;

@Document(collection = "roles")
public class Role {

	@Id
	private String id;

	private UserRoleEnum type;

	public Role() {

	}

	public Role(UserRoleEnum type) {
		super();
		this.type = type;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public UserRoleEnum getType() {
		return type;
	}

	public void setType(UserRoleEnum type) {
		this.type = type;
	}

}
