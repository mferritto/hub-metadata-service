package com.welltok.hub.metadata.swagger

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean

import com.mangofactory.swagger.configuration.SpringSwaggerConfig
import com.mangofactory.swagger.models.dto.ApiInfo
import com.mangofactory.swagger.plugin.SwaggerSpringMvcPlugin

class MySpringSwaggerConfig {
	@Autowired SpringSwaggerConfig springSwaggerConfig
	
	@Bean
	public SwaggerSpringMvcPlugin customImplementation(){
	   new SwaggerSpringMvcPlugin(this.springSwaggerConfig)
		  .includePatterns(".*")
		  .apiInfo(apiInfo())
	}
	
	private ApiInfo apiInfo() {
		new ApiInfo(
				"HUB Scheduling Services",
				"Rest api's for managing HUB jobs.",
				"",
				"aaron.doyle@welltok.com",
				"",
				""
		  )
	  }
}
