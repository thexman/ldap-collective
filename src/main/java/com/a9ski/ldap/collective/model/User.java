package com.a9ski.ldap.collective.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class User {
	private String givenName; // sn
	private String surname; // sn
	private String commonName; // cn
	private String displayName; // cn
	private String userPassword;
	private String login;
	private String email;
}
