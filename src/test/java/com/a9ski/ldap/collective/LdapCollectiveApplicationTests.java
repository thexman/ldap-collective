package com.a9ski.ldap.collective;

import com.a9ski.ldap.collective.ldap.LdapDefinitionFactory;
import com.a9ski.ldap.collective.ldap.LdapEntryDefinition;
import com.a9ski.ldap.collective.services.LdapService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class LdapCollectiveApplicationTests {

	@Autowired
	private LdapService ldap;

	@Autowired
	private LdapDefinitionFactory definitionFactory;

	@Test
	public void testUserSearch() throws Exception {
		final LdapEntryDefinition d = definitionFactory.getDefinition("user");
		System.out.println(ldap.search(d, "Albert"));
	}

}
