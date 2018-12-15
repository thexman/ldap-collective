package com.a9ski.ldap.collective.ldap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.api.ldap.model.schema.AttributeType;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserMapping {
	@JsonProperty("fields")
	private List<LdapField> fields = new ArrayList<>();
	
	@JsonProperty("objectClasses")
	private List<LdapObjectClass> objectClasses = new ArrayList<>();
	
	private Map<String, LdapField> fieldsMap = new TreeMap<>();
	
	public UserMapping() {
		super();
	}
	
	public void setFields(List<LdapField> fields) {
		this.fields = fields;
		constructMap();
	}

	private void constructMap() {
		final Map<String, LdapField> fieldsMap = new TreeMap<>();
		if (fields != null) {
			for(final LdapField f : fields) {
				if (LdapFieldType.OID.equals(f.getType()) && StringUtils.isBlank(f.getName())) {
					try {
						final AttributeType attr = LdapField.schemaManager.lookupAttributeTypeRegistry(f.getOid());
						fieldsMap.put(attr.getName(), f);
					} catch (final LdapException ex) {
						throw new IllegalStateException("Cannot find attribute type " + f.getOid(), ex);
					}
				} else {
					fieldsMap.put(f.getName(), f);
				}
			}
		}
		this.fieldsMap = Collections.unmodifiableMap(fieldsMap);
	}
}
