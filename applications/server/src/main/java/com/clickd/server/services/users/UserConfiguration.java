package com.clickd.server.services.users;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yammer.dropwizard.config.Configuration;
import com.yammer.dropwizard.config.ServerFactory;

public class UserConfiguration extends Configuration {
	@NotEmpty
	@JsonProperty
	private String template;

	@NotEmpty
	@JsonProperty
	private String defaultName = "Stranger";

//	@Valid
//    @NotNull
//    private ServerFactory server = new ServerFactory(http, defaultName);


	public String getTemplate() {
		return template;
	}

	public String getDefaultName() {
		return defaultName;
	}

    
}