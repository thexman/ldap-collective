package com.a9ski.ldap.collective.validators;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class LengthValidator extends Validator {
	@JsonProperty("min")
	@JacksonXmlProperty(isAttribute = true)
	private Integer min;
	
	@JsonProperty("max")
	@JacksonXmlProperty(isAttribute = true)
	private Integer max;

	private boolean isValidLength(CharSequence s) {
		return (min == null || s.length() >= min) && (max == null || s.length() <= max);
	}
	
	@Override
	public boolean isValidSyntax(Object value) {
		return (value instanceof CharSequence) &&  isValidLength((CharSequence)value);   
	}
}
