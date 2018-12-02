package com.a9ski.ldap.collective.configurations;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Configuration
@ConfigurationProperties(prefix = "ldap.fields.user")
@Data
public class LdapUserFields {
	@Value("uid")
	private String uid = "uid";

	@Value("uid")
	private String login = "uid";

	@Value("sn")
	private String surname = "sn";

	@Value("givenname")
	private String givenName = "givenname";

	@Value("mail")
	private String email = "mail";

	@Value("displayName")
	private String displayName = "displayName";

	@Value("cn")
	private String commonName = "cn";
	
	@Value("description")
	private String description = "description";

	@Value("telephoneNumber ")
	private String telephoneNumber = "telephoneNumber ";

	
	@Value("userPassword")
	private String password = "userPassword";

}
