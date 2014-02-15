package com.clickd.server.services;

import com.yammer.metrics.core.HealthCheck;

public class ApplicationHealthCheck extends HealthCheck {

	protected ApplicationHealthCheck(String name) {
		super(name);
	}

	@Override
	protected Result check() throws Exception {
		if (1 == 1) {
			return Result.healthy();
		} else {
			return Result.unhealthy("BAD HEALTH CHECK");
		}
	}
}
