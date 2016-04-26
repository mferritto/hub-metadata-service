package com.welltok.hub.metadata.config

import javax.annotation.PostConstruct;

class JavaSecurityConfig {

	@PostConstruct
	void configureSecurity() {
		java.security.Security.setProperty("networkaddress.cache.ttl", "60")
	}
}
