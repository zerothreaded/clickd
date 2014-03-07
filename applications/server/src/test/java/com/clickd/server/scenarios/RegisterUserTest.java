package com.clickd.server.scenarios;

import org.junit.Before;

import com.clickd.server.services.AbstractResourceTest;
import com.clickd.server.services.choices.ChoiceResource;
import com.clickd.server.services.users.UserResource;

public class RegisterUserTest extends AbstractResourceTest {

	private UserResource userResource;
	private ChoiceResource choiceResource;
	
	@Before
	public void setup() {
		super.setup();
		userResource = (UserResource) applicationContext.getBean("userResource");
		choiceResource = (ChoiceResource) applicationContext.getBean("choiceResource");
	}
	
}
