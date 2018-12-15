package com.a9ski.ldap.collective.validators;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class BoundValidator extends Validator {
	@JsonProperty("value")
	@JacksonXmlProperty(isAttribute = true)
	private double value;
	
	@JsonProperty("exclusive")
	@JacksonXmlProperty(isAttribute = true)
	private boolean exclusive = false;
	
}
