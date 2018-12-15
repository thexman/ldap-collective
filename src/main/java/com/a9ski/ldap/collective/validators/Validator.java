package com.a9ski.ldap.collective.validators;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property="type")
//@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
@JsonSubTypes({
	@JsonSubTypes.Type(name="regex", value = RegExValidator.class),
	@JsonSubTypes.Type(name="length", value = LengthValidator.class),
	@JsonSubTypes.Type(name="min", value = MinValidator.class),
	@JsonSubTypes.Type(name="max", value = MaxValidator.class)
})
public abstract class Validator {
	@JsonProperty("type")
	private String type;
	
	public abstract boolean isValidSyntax(Object value);
}
