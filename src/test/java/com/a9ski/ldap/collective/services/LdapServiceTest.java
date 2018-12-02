package com.a9ski.ldap.collective.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.a9ski.ldap.collective.model.User;
import com.unboundid.ldap.sdk.LDAPException;

@RunWith(SpringRunner.class)
@SpringBootTest//(properties = "spring.main.web-application-type=reactive"))
public class LdapServiceTest {

	@Autowired
	private LdapService ldapService;
	
	@Test
	public void testSearchUsersForAlbertEinstein() throws LDAPException {
		final List<User> users =ldapService.searchUsers("einstein");
		assertFalse(users.isEmpty());
		final User einstein = users.get(0);
		assertEquals("Albert Einstein", einstein.getCommonName());
		assertEquals("Einstein", einstein.getSurname());
		assertEquals("einstein@ldap.forumsys.com", einstein.getEmail());
		assertEquals("314-159-2653", einstein.getTelephoneNumber());
		assertEquals("einstein", einstein.getLogin());
	}
	
	@Test
	public void testSearchUsersForTest() throws LDAPException {
		final List<User> users =ldapService.searchUsers("test");
		assertFalse(users.isEmpty());
		final User einstein = users.get(0);
		assertEquals("Test", einstein.getDisplayName());
		assertEquals("Test", einstein.getGivenName());
	}

}
