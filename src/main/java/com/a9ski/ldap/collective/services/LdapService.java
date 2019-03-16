package com.a9ski.ldap.collective.services;

import java.util.*;
import java.util.stream.Collectors;

import com.a9ski.ldap.collective.ldap.LdapEntry;
import com.a9ski.ldap.collective.ldap.LdapEntryDefinition;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.a9ski.ldap.collective.configurations.LdapConfig;
import com.a9ski.ldap.collective.configurations.LdapUserFields;
import com.a9ski.ldap.collective.model.User;
import com.a9ski.utils.ExtCollectionUtils;
import com.unboundid.ldap.sdk.BindRequest;
import com.unboundid.ldap.sdk.Filter;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ReadOnlyEntry;
import com.unboundid.ldap.sdk.SearchRequest;
import com.unboundid.ldap.sdk.SearchResult;
import com.unboundid.ldap.sdk.SearchResultEntry;
import com.unboundid.ldap.sdk.SearchScope;
import com.unboundid.ldap.sdk.SimpleBindRequest;
import com.unboundid.ldap.sdk.SingleServerSet;


@Service
public class LdapService {
	private final Logger logger = LoggerFactory.getLogger(LdapService.class);
	
	private final LdapConfig ldapConfig;
	private final LdapUserFields userFields;
	
	private final SingleServerSet serverSet;
	
	private final String userSearchFilter;
	
	
	
	@Autowired
	public LdapService(@NonNull final LdapConfig ldapConfig, final LdapUserFields userFields) {
		this.ldapConfig = ldapConfig;
		this.userFields = userFields;
		this.userSearchFilter = createObjectClassesFilter(ldapConfig.getUserObjectClasses());
		this.serverSet = new SingleServerSet(ldapConfig.getHost(), ldapConfig.getPort());
	}
	
	private String createObjectClassesFilter(Set<String> objectClasses) {
		if (ExtCollectionUtils.isNotEmpty(objectClasses)) {
			return "(&" + objectClasses.stream()
				.map(c -> "(objectClass=" + c + ")")
				.collect(Collectors.joining()) + ")";
		} else {
			return "";
		}
	}

	public List<LdapEntry> search(@NonNull final LdapEntryDefinition definition, @NonNull final String search) throws LDAPException {
		final String additionlFilter = createObjectClassesFilter(definition.getRequiredClasses());
		return search(definition, search, additionlFilter);
	}

	public List<LdapEntry> search(@NonNull final LdapEntryDefinition definition, @NonNull final String search, @NonNull final String andFilter) throws LDAPException {
		List<LdapEntry> result = null;
		if (StringUtils.isNotBlank(search)) {
			final String s = Filter.encodeValue(search);
			final String orFilter = "(|" + definition.getSearchableFieldNames().stream()
					.map(f -> String.format("(%s=*%s*)", f, s))
					.collect(Collectors.joining()) + ")";
			final String query;
			if (StringUtils.isAnyBlank(andFilter, orFilter)) {
				query = StringUtils.defaultIfBlank(orFilter, andFilter);
			} else {
				query = String.format("(&%s%s)", orFilter, andFilter);
			}
			if (StringUtils.isNotBlank(query)) {
				result = searchByLdapFilter(definition, query);
			}
		}
		return result;
	}

	public List<LdapEntry> searchByLdapFilter(final LdapEntryDefinition definition, String filter) throws LDAPException {
		logger.debug("Searching for LDAP entities with filter: " + filter);
		final List<LdapEntry> entries = new ArrayList<>();

		try (final LDAPConnection ldapConnection = getConnection()) {
			final SearchRequest searchRequest = new SearchRequest(ldapConfig.getUserBaseDn(), SearchScope.SUB, filter);
			final SearchResult r = ldapConnection.search(searchRequest);
			for (final SearchResultEntry e : r.getSearchEntries()) {
				final LdapEntry entry = toEntry(e, definition);
				entries.add(entry);
			}

			return entries;
		}
	}

	@Deprecated
	public List<User> searchUsers(String search) throws LDAPException {
		final List<User> users = new ArrayList<>();
		if (StringUtils.isNotBlank(search)) {
			final String login = "(" + userFields.getLogin() + "=*" + Filter.encodeValue(search) + "*)";
			final String displayName = "(" + userFields.getDisplayName() + "=*" + Filter.encodeValue(search) + "*)";
			final String commonName = "(" + userFields.getCommonName() + "=*" + Filter.encodeValue(search) + "*)";
			final String or = "(|" + login + displayName + commonName + ")";
			final String query = "(&" + or + userSearchFilter + ")";
			users.addAll(findUsers(Filter.create(query)));
		}
		return users;
	}

	@Deprecated
	private List<User> findUsers(final Filter filter) throws LDAPException {
		logger.debug("Searching for LDAP users with filter: " + filter.toString());
		final List<User> users = new ArrayList<>();

		try (final LDAPConnection ldapConnection = getConnection()) {
			final SearchRequest searchRequest = new SearchRequest(ldapConfig.getUserBaseDn(), SearchScope.SUB, filter);
			final SearchResult r = ldapConnection.search(searchRequest);			
			for(final SearchResultEntry e : r.getSearchEntries()) {
				final User user = toUser(e);
				users.add(user);
			}
			
//			for(final User u : users) {
//				u.getGroups().addAll(findUserGroups(ldapConnection, u));
//			}
			
			return users;
		}
	}

	private LdapEntry toEntry(final ReadOnlyEntry e, final LdapEntryDefinition d) {
		final Map<String, String> values = new TreeMap<>();
		for(final String f : d.getPublicFieldNames()) {
			values.put(f, e.getAttributeValue(f));
		}
		return new LdapEntry(d, values);
	}

	@Deprecated
	private User toUser(final ReadOnlyEntry e) {
		return User.builder()
			.commonName(e.getAttributeValue(userFields.getCommonName()))
			.displayName(e.getAttributeValue(userFields.getDisplayName()))
			.email(e.getAttributeValue(userFields.getEmail()))
			.givenName(e.getAttributeValue(userFields.getGivenName()))
			.login(e.getAttributeValue(userFields.getLogin()))
			.surname(e.getAttributeValue(userFields.getSurname()))
			.build();
	}
	
	private LDAPConnection getConnection() throws LDAPException {
		final BindRequest bindRequest = new SimpleBindRequest(ldapConfig.getBindDn(), ldapConfig.getPassword());
		final LDAPConnection ldapConnection = serverSet.getConnection();
		
		try {
			ldapConnection.bind(bindRequest);
			ldapConnection.setConnectionName(String.format("ldap-connection-to-%s:%d", ldapConfig.getHost(), ldapConfig.getPort()));
		} catch(LDAPException ex) {
			ldapConnection.close();
			throw ex;
		}
		return ldapConnection;
	}
}
