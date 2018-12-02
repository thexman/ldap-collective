package com.a9ski.ldap.collective.configurations;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Configuration
@ConfigurationProperties(prefix = "ldap.fields.common")
@Data
public class LdapCommonFields {
	@Value("objectClass")
	private String objectClass = "objectClass";
}
