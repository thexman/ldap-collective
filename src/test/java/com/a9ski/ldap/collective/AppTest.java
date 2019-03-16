package com.a9ski.ldap.collective;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.stream.Collectors;

import com.a9ski.ldap.collective.ldap.LdapFieldVisibility;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.api.ldap.model.schema.AttributeType;
import org.apache.directory.api.ldap.model.schema.LdapSyntax;
import org.apache.directory.api.ldap.model.schema.SyntaxChecker;
import org.apache.directory.api.ldap.schema.manager.impl.DefaultSchemaManager;
import org.junit.Test;

import com.a9ski.ldap.collective.ldap.LdapFieldType;
import com.a9ski.ldap.collective.ldap.LdapEntryDefinition;
import com.a9ski.ldap.collective.validators.LengthValidator;
import com.a9ski.ldap.collective.validators.MaxValidator;
import com.a9ski.ldap.collective.validators.MinValidator;
import com.a9ski.ldap.collective.validators.RegExValidator;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

public class AppTest {
	
	private final XmlMapper mapper = new XmlMapper();
	
	public AppTest() {
		mapper.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS);
	}
	
	@Test
	public void testParsingObjectClasses() throws Exception {
		final LdapEntryDefinition m = mapper.readValue(getClass().getResourceAsStream("/test.xml"), LdapEntryDefinition.class);
	    assertNotNull(m);
	    assertNotNull(m.getObjectClasses());
	    assertEquals(6, m.getObjectClasses().size());
	    assertEquals("top,person,posixAccount,inetOrgPerson,myObjectClass,pwdPolicy", m.getObjectClasses().stream().map(o -> o.getName()).collect(Collectors.joining(",")));
	    assertFalse(m.getObjectClasses().get(0).isOptional());
	    assertTrue(m.getObjectClasses().get(5).isOptional());
	}
	
	@Test
	public void testParsingFields() throws Exception {
		final LdapEntryDefinition m = mapper.readValue(getClass().getResourceAsStream("/test.xml"), LdapEntryDefinition.class);
	    assertNotNull(m);
	    assertNotNull(m.getFields());
	    assertEquals(7, m.getFields().size());
	    
	    int i = 0;
	    assertEquals(LdapFieldType.STRING, m.getFields().get(i).getType());
	    assertEquals("userPassword", m.getFields().get(i).getName());
		assertEquals(LdapFieldVisibility.PRIVATE, m.getFields().get(i).getVisibility());
	    assertNotNull(m.getFields().get(i).getValidators());	    
	    assertEquals(1, m.getFields().get(i).getValidators().size());
	    assertTrue(m.getFields().get(i).getValidators().get(0) instanceof RegExValidator);
	    assertEquals("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,10}$", ((RegExValidator)m.getFields().get(i).getValidators().get(0)).getPatternValue());
	    
	    i++;
	    assertEquals(LdapFieldType.STRING, m.getFields().get(i).getType());
	    assertEquals("cn", m.getFields().get(i).getName());
		assertEquals(LdapFieldVisibility.PUBLIC, m.getFields().get(i).getVisibility());
	    assertNotNull(m.getFields().get(i).getValidators());
	    assertEquals(2, m.getFields().get(i).getValidators().size());
	    assertTrue(m.getFields().get(i).getValidators().get(0) instanceof RegExValidator);
	    assertEquals("^[A-Z][a-zA-Z\\s]*$", ((RegExValidator)m.getFields().get(i).getValidators().get(0)).getPatternValue());
	    assertTrue(m.getFields().get(i).getValidators().get(1) instanceof LengthValidator);
	    assertEquals(Integer.valueOf(2), ((LengthValidator)m.getFields().get(i).getValidators().get(1)).getMin());
	    assertEquals(Integer.valueOf(255), ((LengthValidator)m.getFields().get(i).getValidators().get(1)).getMax());
	    
	    i++;
	    assertEquals(LdapFieldType.STRING, m.getFields().get(i).getType());
	    assertEquals("sn", m.getFields().get(i).getName());
		assertEquals(LdapFieldVisibility.PUBLIC, m.getFields().get(i).getVisibility());
	    assertNotNull(m.getFields().get(i).getValidators());
	    assertEquals(1, m.getFields().get(i).getValidators().size());
	    assertTrue(m.getFields().get(i).getValidators().get(0) instanceof LengthValidator);
	    assertEquals(Integer.valueOf(1), ((LengthValidator)m.getFields().get(i).getValidators().get(0)).getMin());
	    assertNull(((LengthValidator)m.getFields().get(i).getValidators().get(0)).getMax());
	    
	    i++;
	    assertEquals(LdapFieldType.INTEGER, m.getFields().get(i).getType());
	    assertEquals("retirementAge", m.getFields().get(i).getName());
		assertEquals(LdapFieldVisibility.PUBLIC, m.getFields().get(i).getVisibility());
	    assertNotNull(m.getFields().get(i).getValidators());
	    assertEquals(2, m.getFields().get(i).getValidators().size());
	    assertTrue(m.getFields().get(i).getValidators().get(0) instanceof MinValidator);
	    assertEquals(Double.valueOf(18), Double.valueOf(((MinValidator)m.getFields().get(i).getValidators().get(0)).getValue()));
	    assertTrue(m.getFields().get(i).getValidators().get(1) instanceof MaxValidator);
	    assertEquals(Double.valueOf(65), Double.valueOf(((MaxValidator)m.getFields().get(i).getValidators().get(1)).getValue()));
	    
	    i++;
	    assertEquals(LdapFieldType.FLOAT, m.getFields().get(i).getType());
	    assertEquals("height", m.getFields().get(i).getName());
		assertEquals(LdapFieldVisibility.PUBLIC, m.getFields().get(i).getVisibility());
	    assertNotNull(m.getFields().get(i).getValidators());
	    assertEquals(2, m.getFields().get(i).getValidators().size());
	    assertTrue(m.getFields().get(i).getValidators().get(0) instanceof MinValidator);
	    assertEquals(Double.valueOf(50.5), Double.valueOf(((MinValidator)m.getFields().get(i).getValidators().get(0)).getValue()));
	    assertTrue(m.getFields().get(i).getValidators().get(1) instanceof MaxValidator);
	    assertEquals(Double.valueOf(300.42), Double.valueOf(((MaxValidator)m.getFields().get(i).getValidators().get(1)).getValue()));
	    
	    i++;
	    assertEquals(LdapFieldType.OID, m.getFields().get(i).getType());
		assertEquals(LdapFieldVisibility.PUBLIC, m.getFields().get(i).getVisibility());
	    assertEquals("2.5.4.42", m.getFields().get(i).getOid());
	    
	    i++;
	    assertEquals(LdapFieldType.OID, m.getFields().get(i).getType());
		assertEquals(LdapFieldVisibility.PUBLIC, m.getFields().get(i).getVisibility());
	    assertEquals("1.3.6.1.4.1.42.2.27.8.1.3", m.getFields().get(i).getOid());
	    assertNotNull(m.getFields().get(i).getValidators());
	    assertEquals(2, m.getFields().get(i).getValidators().size());
	    assertTrue(m.getFields().get(i).getValidators().get(0) instanceof MinValidator);
	    assertEquals(Double.valueOf(7776000), Double.valueOf(((MinValidator)m.getFields().get(i).getValidators().get(0)).getValue()));
	    assertTrue(m.getFields().get(i).getValidators().get(1) instanceof MaxValidator);
	    assertEquals(Double.valueOf(10368000), Double.valueOf(((MaxValidator)m.getFields().get(i).getValidators().get(1)).getValue()));
	}
	
	@Test
	public void testParsingMappingToFieldMap() throws Exception {
		final LdapEntryDefinition m = mapper.readValue(getClass().getResourceAsStream("/test.xml"), LdapEntryDefinition.class);
	    assertNotNull(m);
	    assertNotNull(m.getFields());
	    assertFalse(m.getFieldsMap().isEmpty());
	    assertTrue(m.getFieldsMap().containsKey("userPassword"));
	    assertTrue(m.getFieldsMap().containsKey("cn"));
	    assertTrue(m.getFieldsMap().containsKey("sn"));
	    assertTrue(m.getFieldsMap().containsKey("retirementAge"));
	    assertTrue(m.getFieldsMap().containsKey("height"));
	    assertTrue(m.getFieldsMap().containsKey("givenName"));
	    assertTrue(m.getFieldsMap().containsKey("pwdMaxAge"));
	}
	
	
	@Test
	public void testApacheLdapSchema() throws LdapException {
		final DefaultSchemaManager m = new DefaultSchemaManager();
		
		final LdapSyntax syntax = m.lookupLdapSyntaxRegistry("1.3.6.1.4.1.1466.115.121.1.53");
		final SyntaxChecker checker = syntax.getSyntaxChecker();
		assertFalse(checker.isValidSyntax("test"));
		assertTrue(checker.isValidSyntax("9412161032Z")); // 16. Dec. 1994 16:10:32 UTC
		
		final AttributeType attr = m.lookupAttributeTypeRegistry("1.3.6.1.4.1.42.2.27.8.1.2"); // pwdMinAge (object class "pwdPolicy")
		assertTrue(attr.getSyntax().getSyntaxChecker().isValidSyntax(42));
		
	}
}
