package com.a9ski.ldap.collective.ldap;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

@Service
public class LdapDefinitionFactory {
    private final XmlMapper mapper = new XmlMapper();

    private final Map<String, LdapEntryDefinition> definitions = new TreeMap<>();

    public LdapDefinitionFactory() {
        super();
        mapper.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS);
    }

    public LdapEntryDefinition getDefinition(String name) {
        if (!definitions.containsKey(name)) {
            synchronized (definitions) {
                if (!definitions.containsKey(name)) {
                    definitions.put(name, readDefinition(name));
                }
            }
        }
        return definitions.get(name);
    }

    private LdapEntryDefinition readDefinition(String name) {
        final LdapEntryDefinition m;
        try {
            m = mapper.readValue(getClass().getResourceAsStream(toResourceName(name)), LdapEntryDefinition.class);
        } catch (final IOException ex) {
            throw new RuntimeException(ex);
        }
        return m;
    }

    private String toResourceName(String name) {
        return String.format("/definitions/%s.xml", name);
    }


}
