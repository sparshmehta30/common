package com.common.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class UserAuthDto implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;

	private String name;

	private String email;

	private Integer role;

	private Integer userType;

	private String password;

	private Boolean isEmailVerified;

	private Boolean isPlanActive;

}
