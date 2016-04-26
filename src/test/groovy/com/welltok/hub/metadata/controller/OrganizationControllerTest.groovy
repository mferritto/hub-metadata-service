package com.welltok.hub.metadata.controller

import static org.junit.Assert.*
import static org.junit.matchers.JUnitMatchers.*
import static org.mockito.Mockito.*
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*
import groovy.json.JsonBuilder

import org.junit.Test
import org.springframework.http.MediaType

import com.welltok.hub.metadata.model.Organization
import com.welltok.hub.metadata.service.OrganizationServiceImpl
import com.welltok.hub.metadata.util.RecordDigest
import com.welltok.hub.metadata.validator.OrganizationValidator

class OrganizationControllerTest extends BaseControllerTestClass {

	@Test
	public void getOrganizations() throws Exception {
		def mockOrganizationService = mock(OrganizationServiceImpl.class)
		when(mockOrganizationService.getOrganizations()).thenReturn([
			new Organization(orgName: "testname"),
			new Organization(orgName: "testname2")
		])
		wac.getBean(OrganizationController.class).setOrgService(mockOrganizationService)

		this.mockMvc.perform(get("/organizations").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType("application/json;charset=UTF-8"))
			.andExpect(content().string(containsString('"orgName" : "testname"')))
			.andExpect(content().string(containsString('"orgName" : "testname2"')))
	}
	
	@Test
	public void getOrganizationsByName() throws Exception {
		def mockOrganizationService = mock(OrganizationServiceImpl.class)
		when(mockOrganizationService.getOrganizationsByName(anyObject())).thenReturn([
			new Organization(orgName: "testname", orgType: "CLIENT"),
			new Organization(orgName: "testname", orgType: "SUBCLIENT")
		])
		wac.getBean(OrganizationController.class).setOrgService(mockOrganizationService)

		this.mockMvc.perform(get("/organizations/name/testname").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType("application/json;charset=UTF-8"))
			.andExpect(content().string(containsString('"orgName" : "testname"')))
			.andExpect(content().string(containsString('"orgType" : "CLIENT"')))
			.andExpect(content().string(containsString('"orgType" : "SUBCLIENT"')))
	}
	
	@Test
	public void getOrganizationsByType() throws Exception {
		def mockOrganizationService = mock(OrganizationServiceImpl.class)
		when(mockOrganizationService.getOrganizationsByType(anyObject())).thenReturn([
			new Organization(orgName: "testname", orgType: "CLIENT"),
			new Organization(orgName: "testname2", orgType: "CLIENT")
		])
		wac.getBean(OrganizationController.class).setOrgService(mockOrganizationService)

		this.mockMvc.perform(get("/organizations/type/CLIENT").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType("application/json;charset=UTF-8"))
			.andExpect(content().string(containsString('"orgName" : "testname"')))
			.andExpect(content().string(containsString('"orgName" : "testname2"')))
			.andExpect(content().string(containsString('"orgType" : "CLIENT"')))
	}
	
	@Test
	public void getOrganizationByNameAndType() throws Exception {
		def mockOrganizationService = mock(OrganizationServiceImpl.class)
		when(mockOrganizationService.getOrganizationByNameAndType(anyObject(),anyObject())).thenReturn(new Organization(orgName: "testname", orgType: "CLIENT"))
		wac.getBean(OrganizationController.class).setOrgService(mockOrganizationService)

		this.mockMvc.perform(get("/organizations/testname/CLIENT").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType("application/json;charset=UTF-8"))
			.andExpect(content().string(containsString('"orgName" : "testname"')))
			.andExpect(content().string(containsString('"orgType" : "CLIENT"')))
	}
	
	@Test
	public void getSubclients() throws Exception {
		def mockOrganizationService = mock(OrganizationServiceImpl.class)
		when(mockOrganizationService.getSubclients(anyObject())).thenReturn([
			new Organization(orgName: "subclient1", orgType: "SUBCLIENT"),
			new Organization(orgName: "subclient2", orgType: "SUBCLIENT")
		])
		wac.getBean(OrganizationController.class).setOrgService(mockOrganizationService)

		this.mockMvc.perform(get("/organizations/testname/subclients").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType("application/json;charset=UTF-8"))
			.andExpect(content().string(containsString('"orgName" : "subclient1"')))
			.andExpect(content().string(containsString('"orgName" : "subclient2"')))
			.andExpect(content().string(containsString('"orgType" : "SUBCLIENT"')))
	}
	
	@Test
	public void createOrganization() throws Exception {
		def mockOrganizationService = mock(OrganizationServiceImpl.class)
		when(mockOrganizationService.createOrganization(anyObject())).thenReturn(new Organization(orgName: "testname"))
		wac.getBean(OrganizationController.class).setOrgService(mockOrganizationService)

		def mockOrgValidator = mock(OrganizationValidator.class)
		when(mockOrgValidator.validate(anyObject(),anyObject(),anyObject())).thenReturn(true)
		wac.getBean(OrganizationController.class).setOrgValidator(mockOrgValidator)

		def org = new Organization(orgName: "testname")
		def orgJson = new JsonBuilder(org).toString()

		this.mockMvc.perform(post("/organizations").accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON).content(orgJson))
			.andExpect(status().isOk())
			.andExpect(content().contentType("application/json;charset=UTF-8"))
			.andExpect(content().string(containsString('"orgName" : "testname"')))
	}
	
	
	/*
	@Test
	public void createOrganization_Invalid() throws Exception {
		def mockOrganizationService = mock(OrganizationServiceImpl.class)
		when(mockOrganizationService.createOrganization(anyObject())).thenReturn(new Organization(orgName: "testname"))
		wac.getBean(OrganizationController.class).setOrgService(mockOrganizationService)

		def mockOrgValidator = mock(OrganizationValidator.class)
		when(mockOrgValidator.validate(anyObject(),anyObject(),anyObject())).thenReturn(false)
		wac.getBean(OrganizationController.class).setOrgValidator(mockOrgValidator)

		def org = new Organization(orgName: "testname")
		def orgJson = new JsonBuilder(org).toString()

		this.mockMvc.perform(post("/organizations").accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON).content(orgJson))
			.andExpect(status().isUnprocessableEntity())
			.andExpect(content().contentType("application/json"))
	}*/
	
	@Test
	public void updateOrganization() throws Exception {
		def mockOrganizationService = mock(OrganizationServiceImpl.class)
		when(mockOrganizationService.getOrganizationByNameAndType(anyObject(),anyObject())).thenReturn(new Organization(orgName:"testname", parentOrgName:"client1"))
		when(mockOrganizationService.updateOrganization(anyObject())).thenReturn(new Organization(orgName: "testname", parentOrgName:"client2"))
		wac.getBean(OrganizationController.class).setOrgService(mockOrganizationService)

		def mockRecordDigest = mock(RecordDigest.class)
		when(mockRecordDigest.getHexSha1Digest(contains("parentOrgName>client1<"))).thenReturn("testDigest1")
		when(mockRecordDigest.getHexSha1Digest(contains("parentOrgName>client2<"))).thenReturn("testDigest")
		wac.getBean(OrganizationController.class).setRecordDigest(mockRecordDigest)

		def mockOrgValidator = mock(OrganizationValidator.class)
		when(mockOrgValidator.validate(anyObject(),anyObject(),anyObject())).thenReturn(true)
		wac.getBean(OrganizationController.class).setOrgValidator(mockOrgValidator)

		def org = new Organization(orgName: "testname", parentOrgName:"client2")
		def orgJson = new JsonBuilder(org).toString()

		this.mockMvc.perform(put("/organizations/testname/CLIENT").accept(MediaType.APPLICATION_JSON).header("recordDigest","testDigest1").contentType(MediaType.APPLICATION_JSON).content(orgJson))
			.andExpect(status().isOk())
			.andExpect(content().contentType("application/json;charset=UTF-8"))
			.andExpect(content().string(containsString('"orgName" : "testname"')))
			.andExpect(content().string(containsString('"parentOrgName" : "client2"')))
	}
	
	/*
	@Test
	public void updateOrganization_NotFoundException() throws Exception {
		def mockOrganizationService = mock(OrganizationServiceImpl.class)
		when(mockOrganizationService.getOrganizationByNameAndType(anyObject(),anyObject())).thenReturn(null)
		when(mockOrganizationService.updateOrganization(anyObject())).thenReturn(new Organization(orgName: "testname", parentOrgName:"client2"))
		wac.getBean(OrganizationController.class).setOrgService(mockOrganizationService)

		def mockRecordDigest = mock(RecordDigest.class)
		when(mockRecordDigest.getHexSha1Digest(contains("parentOrgName>client1<"))).thenReturn("testDigest1")
		when(mockRecordDigest.getHexSha1Digest(contains("parentOrgName>client2<"))).thenReturn("testDigest")
		wac.getBean(OrganizationController.class).setRecordDigest(mockRecordDigest)

		def mockOrgValidator = mock(OrganizationValidator.class)
		when(mockOrgValidator.validate(anyObject(),anyObject(),anyObject())).thenReturn(true)
		wac.getBean(OrganizationController.class).setOrgValidator(mockOrgValidator)

		def org = new Organization(orgName: "testname", parentOrgName:"client2")
		def orgJson = new JsonBuilder(org).toString()

		this.mockMvc.perform(put("/organizations/name/testname/type/CLIENT").accept(MediaType.APPLICATION_JSON).header("recordDigest","testDigest1").contentType(MediaType.APPLICATION_JSON).content(orgJson))
			.andExpect(status().isNotFound())
			.andExpect(content().contentType("application/json"))
	}*/
	
	@Test
	public void deleteOrganization() throws Exception {
		def mockOrganizationService = mock(OrganizationServiceImpl.class)
		when(mockOrganizationService.getOrganizationByNameAndType(anyObject(),anyObject())).thenReturn(new Organization(orgName:"testname"))
		doNothing().when(mockOrganizationService).deleteOrganizationByNameAndType(anyObject(),anyObject())
		wac.getBean(OrganizationController.class).setOrgService(mockOrganizationService)

		this.mockMvc.perform(delete("/organizations/testname/CLIENT").accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType("application/json;charset=UTF-8"))
				.andExpect(content().string(containsString('"success" : true')))
	}
}
