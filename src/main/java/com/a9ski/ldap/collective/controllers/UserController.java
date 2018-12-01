package com.a9ski.ldap.collective.controllers;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.a9ski.ldap.collective.model.User;

@RestController
@RequestMapping("/api/users")
public class UserController {
	@RequestMapping(value="{login}", method=RequestMethod.GET)
	public User findByLogin(@PathVariable(value="login") String login) {
		return User.builder().commonName("John Doe").surname("Doe").login(login).build();
	}
}
