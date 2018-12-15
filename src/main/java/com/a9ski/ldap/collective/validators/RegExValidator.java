package com.a9ski.ldap.collective.validators;

import java.util.regex.Pattern;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class RegExValidator extends Validator {
	@JsonProperty("value")
	@JacksonXmlProperty(localName="value", isAttribute = true)
	private String patternValue;

	private Pattern pattern;

	@Override
	public boolean isValidSyntax(Object value) {
		if (pattern == null) {
			pattern = Pattern.compile(patternValue);
		}
		return (value instanceof CharSequence) && pattern.matcher((CharSequence) value).matches();
	}
	
	
	
}
