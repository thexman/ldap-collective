package com.a9ski.ldap.collective.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
import com.unboundid.ldap.sdk.LDAPSearchException;
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
	public LdapService(final LdapConfig ldapConfig, final LdapUserFields userFields) {
		this.ldapConfig = ldapConfig;
		this.userFields = userFields;
		this.userSearchFilter = createUserSearchFilter(ldapConfig.getUserObjectClasses());
		this.serverSet = new SingleServerSet(ldapConfig.getHost(), ldapConfig.getPort());
	}
	
	private String createUserSearchFilter(Set<String> userObjectClasses) {
		if (ExtCollectionUtils.isNotEmpty(userObjectClasses)) {
			return "(&" + userObjectClasses.stream()
				.map(c -> "(objectClass=" + c + ")")
				.collect(Collectors.joining()) + ")";
		} else {
			return "";
		}
	}

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
	
	private List<User> findUsers(final Filter filter) throws LDAPException, LDAPSearchException  {
		logger.debug("Searching for LDAP users with filter: " + filter.toString());
		final List<User> users = new ArrayList<>();
		final LDAPConnection ldapConnection = getBindedConnection();		
		
		try {			
			final SearchRequest searchRequest = new SearchRequest(ldapConfig.getUserBaseDn(), SearchScope.SUB, filter);
			final SearchResult r = ldapConnection.search(searchRequest);			
			for(final SearchResultEntry e : r.getSearchEntries()) {
				final User user = toUser(ldapConnection, e, true);
				users.add(user);
			}
			
//			for(final User u : users) {
//				u.getGroups().addAll(findUserGroups(ldapConnection, u));
//			}
			
			return users;
		} finally {
			ldapConnection.close();
		}
	}
	
	private User toUser(LDAPConnection ldapConnection, final ReadOnlyEntry e, boolean loadUserGroups) throws LDAPException {
		return User.builder()
			.commonName(e.getAttributeValue(userFields.getCommonName()))
			.description(e.getAttributeValue(userFields.getDescription()))
			.displayName(e.getAttributeValue(userFields.getDisplayName()))
			.email(e.getAttributeValue(userFields.getEmail()))
			.givenName(e.getAttributeValue(userFields.getGivenName()))
			.login(e.getAttributeValue(userFields.getLogin()))
			.surname(e.getAttributeValue(userFields.getSurname()))
			.telephoneNumber(e.getAttributeValue(userFields.getTelephoneNumber()))
			.build();
	}
	
	private LDAPConnection getBindedConnection() throws LDAPException {
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
