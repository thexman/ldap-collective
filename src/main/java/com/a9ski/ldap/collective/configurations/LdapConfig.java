package com.a9ski.ldap.collective.configurations;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Configuration
@ConfigurationProperties(prefix = "ldap")
@Data
public class LdapConfig {
	@Value("127.0.0.1")
	private String host;
	
	@Value("389")
	private int port;
	
	@Value("uid=john.doe,ou=users,dc=a9ski,dc=com")
	private String bindDn;
	
	@Value("secret")
	private String password;
	
	@Value("ou=users,dc=a9ski,dc=com")
	private String userBaseDn;
//	private String userSearchByLoginFilter;
	private Set<String> userObjectClasses = new TreeSet<>(Arrays.asList("inetOrgPerson"));
	
	@Value("ou=groups,dc=a9ski,dc=com")
	private String groupBaseDn;
//	private String groupSearchFilter;
}
