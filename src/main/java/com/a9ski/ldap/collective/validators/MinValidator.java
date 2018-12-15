package com.a9ski.ldap.collective.validators;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MinValidator extends BoundValidator {
	public boolean isValidSyntax(Object value) {
		return (value instanceof Number) && Double.compare(((Number)value).doubleValue(), getValue()) >= 0; 
	}
}
