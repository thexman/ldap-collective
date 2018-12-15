package com.a9ski.ldap.collective.ldap;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class LdapObjectClass {
	@JsonProperty("optional")
	@JacksonXmlProperty(isAttribute = true)
	private boolean optional = false;
	
	@JsonProperty("name")
	@JacksonXmlText
	private String name;
}
