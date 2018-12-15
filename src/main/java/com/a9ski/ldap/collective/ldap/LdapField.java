package com.a9ski.ldap.collective.ldap;

import java.util.List;

import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.api.ldap.model.schema.LdapSyntax;
import org.apache.directory.api.ldap.model.schema.SyntaxChecker;
import org.apache.directory.api.ldap.schema.manager.impl.DefaultSchemaManager;

import com.a9ski.ldap.collective.validators.Validator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class LdapField {
	public static final DefaultSchemaManager schemaManager = new DefaultSchemaManager();
	
	@JsonProperty("name")
	@JacksonXmlProperty(isAttribute = true)
	private String name;
	
	@JsonProperty("type")
	@JacksonXmlProperty(isAttribute = true)
	private LdapFieldType type;
	
	@JsonProperty("oid")
	@JacksonXmlProperty(isAttribute = true)
	private String oid;
	
	@JsonProperty("validators")
	private List<Validator> validators;
	
	public boolean isValid(Object value) {
		if (type == LdapFieldType.OID) {
			try {
				final LdapSyntax syntax = schemaManager.lookupLdapSyntaxRegistry("1.3.6.1.4.1.1466.115.121.1.53");
				if (syntax != null) {
					final SyntaxChecker checker = syntax.getSyntaxChecker();
					if (checker != null && !checker.isValidSyntax(value)) {
						return false;
					}
				}
			} catch (final LdapException ex) {
				throw new IllegalStateException(ex);
			}
		}
		
		return validators == null || validators.stream().filter(v -> !v.isValidSyntax(value)).findFirst().isPresent();
	}
	
}
