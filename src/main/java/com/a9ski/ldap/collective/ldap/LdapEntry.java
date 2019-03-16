package com.a9ski.ldap.collective.ldap;

import lombok.Data;

import java.util.Map;

@Data
public class LdapEntry {
    private final LdapEntryDefinition definition;
    private final Map<String, String> values;
}
