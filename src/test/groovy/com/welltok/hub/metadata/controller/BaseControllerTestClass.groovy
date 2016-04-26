package com.welltok.hub.metadata.controller

import static org.junit.matchers.JUnitMatchers.*
import static org.mockito.Mockito.*
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*

import org.junit.Before
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.cassandra.core.CassandraOperations
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.web.context.WebApplicationContext

//import com.github.fakemongo.Fongo
//import com.mongodb.Mongo


@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration("classpath:metadata-test-servlet.xml")
class BaseControllerTestClass {
	
	// fake out cassandra for the controller unit tests. only need this to bootstrap the setup
	@Configuration
	static class TestConfiguration {
		
		@Bean CassandraOperations cassandraOperations() {
			return mock(CassandraOperations.class)
		}
	 }

	@Autowired
	protected WebApplicationContext wac;

	protected MockMvc mockMvc;

	@Before
	public void setup() {
		// init the application
		this.mockMvc = webAppContextSetup(this.wac).build();
	}
}

