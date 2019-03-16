package com.a9ski.ldap.collective.ldap;

import java.util.*;
import java.util.stream.Collectors;

import com.a9ski.utils.ExtCollectionUtils;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AccessLevel;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.api.ldap.model.schema.AttributeType;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class LdapEntryDefinition {
	@Setter(AccessLevel.NONE)
	@JsonProperty("fields")
	private List<LdapField> fields = new ArrayList<>();

	@Setter(AccessLevel.NONE)
	@JsonProperty("objectClasses")
	private List<LdapObjectClass> objectClasses = new ArrayList<>();

	@Setter(AccessLevel.NONE)
	private Map<String, LdapField> fieldsMap = new TreeMap<>();

	@Setter(AccessLevel.NONE)
	private Set<String> publicFieldNames;

	@Setter(AccessLevel.NONE)
	private Set<String> searchableFieldNames;

	@Setter(AccessLevel.NONE)
	private Set<String> requiredClasses;
	
	public LdapEntryDefinition() {
		super();
	}

	@JsonCreator
	public LdapEntryDefinition(@JsonProperty("fields") final List<LdapField> fields, @JsonProperty("objectClasses") final List<LdapObjectClass> objectClasses) {
		this.fields = ExtCollectionUtils.copy(ExtCollectionUtils.defaultList(fields));
		this.objectClasses = ExtCollectionUtils.copy(ExtCollectionUtils.defaultList(objectClasses));
		constructMap();
		constructFieldNameSets();
		constructClassesSets();
	}

	private void constructClassesSets() {
		final Set<String> requiredClasses = objectClasses.stream()
				.filter(c -> !c.isOptional())
				.map(c -> c.getName())
				.collect(Collectors.toSet());
		this.requiredClasses = Collections.unmodifiableSet(requiredClasses);
	}

	private void constructFieldNameSets() {
		final Set<String> publicFields = fieldsMap.entrySet().stream()
				.filter(e -> LdapFieldVisibility.PUBLIC.equals(e.getValue().getVisibility()))
				.map(e -> e.getKey())
				.collect(Collectors.toSet());
		this.publicFieldNames = Collections.unmodifiableSet(publicFields);

		final Set<String> searchableFields = fieldsMap.entrySet().stream()
				.filter(e -> LdapFieldVisibility.PUBLIC.equals(e.getValue().getVisibility()) && e.getValue().isSearchable())
				.map(e -> e.getKey())
				.collect(Collectors.toSet());
		this.searchableFieldNames = Collections.unmodifiableSet(searchableFields);
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
