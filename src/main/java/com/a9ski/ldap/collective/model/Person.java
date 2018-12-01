package com.a9ski.ldap.collective.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Person {
	private String uid;
	private String surname; // sn
	private String commonName; // cn
	private String userPassword;
	private String telephoneNumber;
	private String seeAlso;
	private String description;
	
}
