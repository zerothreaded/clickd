package com.clickd.server.services.users;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yammer.dropwizard.config.Configuration;

public class UserConfiguration extends Configuration  {
	@NotEmpty
	@JsonProperty
	private String template;

	@NotEmpty
	@JsonProperty
	private String springContextFileName;
	
	@NotEmpty
	@JsonProperty
	private String defaultName = "Stranger";

	@NotEmpty
	@JsonProperty
	private String dataFolder = "\\sandbox\\data";
	
//	@Valid
//	@NotNull
//	@JsonProperty
//	private final AssetsConfiguration assets = new AssetsConfiguration();
	
	public String getDataFolder() {
		return dataFolder;
	}

	public String getTemplate() {
		return template;
	}

	public String getDefaultName() {
		return defaultName;
	}
	
	public String getSpringContextFileName() {
		return springContextFileName;
	}

//	@Override
//	public AssetsConfiguration getAssetsConfiguration() {
//		return assets;
//	}

    
}