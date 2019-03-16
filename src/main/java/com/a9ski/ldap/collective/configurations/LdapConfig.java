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
	@Value("ldap.forumsys.com")
	private String host;
	
	@Value("389")
	private int port;
	
	@Value("cn=read-only-admin,dc=example,dc=com")
	private String bindDn;
	
	@Value("password")
	private String password;
	
//	@Value("ou=users,dc=a9ski,dc=com")
	@Value("dc=example,dc=com")
	private String userBaseDn;
	
//	private String userSearchByLoginFilter;
	
	private Set<String> userObjectClasses = new TreeSet<>(Arrays.asList("inetOrgPerson"));
	
//	@Value("ou=groups,dc=a9ski,dc=com")
	@Value("ou=mathematicians,dc=example,dc=com")
	private String groupBaseDn;
//	private String groupSearchFilter;
	
	@Value("classpath:definitions/user.xml")
	private String userMappingFile;
	
	@Value("classpath:definitions/group.xml")
	private String groupMappingFile;
}
