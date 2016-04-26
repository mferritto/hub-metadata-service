package com.welltok.hub.metadata.service

import static org.mockito.Mockito.*

import org.springframework.data.cassandra.core.CassandraOperations

import spock.lang.Specification

import com.welltok.hub.metadata.model.Organization

class OrganizationServiceImplTest extends Specification {

	def "get all organizations"() {
		when:
			def org1 = new Organization(orgName: "testname")
		
			def mockCassandraOps = mock(CassandraOperations.class)
			when(mockCassandraOps.select(anyObject(), anyObject())).thenReturn([org1])
			
			def orgService = new OrganizationServiceImpl()
			orgService.cassandraOps = mockCassandraOps
			
			def orgs = orgService.getOrganizations()
		
		then:
			orgs.size() == 1
			orgs[0].orgName == "testname"
	}
	
	def "get organizations by name"() {
		when:
			def org1 = new Organization(orgName: "testname", orgType: "CLIENT")
			def org2 = new Organization(orgName: "testname", orgType: "SUBCLIENT")
		
			def mockCassandraOps = mock(CassandraOperations.class)
			when(mockCassandraOps.select(anyObject(), anyObject())).thenReturn([org1, org2])
			
			def orgService = new OrganizationServiceImpl()
			orgService.cassandraOps = mockCassandraOps
			
			def orgs = orgService.getOrganizationsByName("testname")
		
		then:
			orgs.size() == 2
			orgs[0].orgName == "testname"
			orgs[0].orgType == "CLIENT"
			orgs[1].orgName == "testname"
			orgs[1].orgType == "SUBCLIENT"
	}
	
	def "get organizations by type"() {
		when:
			def org1 = new Organization(orgName: "testname1", orgType: "CLIENT")
			def org2 = new Organization(orgName: "testname2", orgType: "CLIENT")
		
			def mockCassandraOps = mock(CassandraOperations.class)
			when(mockCassandraOps.select(anyObject(), anyObject())).thenReturn([org1, org2])
			
			def orgService = new OrganizationServiceImpl()
			orgService.cassandraOps = mockCassandraOps
			
			def orgs = orgService.getOrganizationsByType("CLIENT")
		
		then:
			orgs.size() == 2
			orgs[0].orgName == "testname1"
			orgs[0].orgType == "CLIENT"
			orgs[1].orgName == "testname2"
			orgs[1].orgType == "CLIENT"
	}
	
	def "get single organization by name and type"() {
		when:
			def org1 = new Organization(orgName: "testname", orgType: "CLIENT")
		
			def mockCassandraOps = mock(CassandraOperations.class)
			when(mockCassandraOps.selectOne(anyObject(), anyObject())).thenReturn(org1)
			
			def orgService = new OrganizationServiceImpl()
			orgService.cassandraOps = mockCassandraOps
			
			def org = orgService.getOrganizationByNameAndType("testname", "CLIENT")
		
		then:
			org.orgName == "testname"
			org.orgType == "CLIENT"
	}
	
	def "get subclients"() {
		when:
			def org1 = new Organization(orgName: "testname1", orgType: "SUBCLIENT", parentOrgName: "testname", parentOrgType: "CLIENT")
			def org2 = new Organization(orgName: "testname2", orgType: "SUBCLIENT", parentOrgName: "testname", parentOrgType: "CLIENT")
		
			def mockCassandraOps = mock(CassandraOperations.class)
			when(mockCassandraOps.select(anyObject(), anyObject())).thenReturn([org1, org2])
			
			def orgService = new OrganizationServiceImpl()
			orgService.cassandraOps = mockCassandraOps
			
			def orgs = orgService.getSubclients("testname")
		
		then:
			orgs.size() == 2
			orgs[0].orgName == "testname1"
			orgs[0].orgType == "SUBCLIENT"
			orgs[1].orgName == "testname2"
			orgs[1].orgType == "SUBCLIENT"
	}
	
	def "create organization"() {
		when:
			def org = new Organization(orgName: "testname", orgType: "CLIENT")
			
			def mockCassandraOps = mock(CassandraOperations.class)
			when(mockCassandraOps.insert(anyObject())).thenReturn(org)
			
			def orgService = new OrganizationServiceImpl()
			orgService.cassandraOps = mockCassandraOps
			
			def createdOrg = orgService.createOrganization(org)
		
		then:
			createdOrg.orgName == "testname"
			createdOrg.orgType == "CLIENT"
					
	}
	
	def "update organization"() {
		when:
			def oldOrg = new Organization(orgName: "testname", orgType: "CLIENT", parentOrgName: "testname1")
			def newOrg = new Organization(orgName: "testname", orgType: "CLIENT", parentOrgName: "testname2")
					
			def mockCassandraOps = mock(CassandraOperations.class)
			when(mockCassandraOps.update(anyObject())).thenReturn(newOrg)
			when(mockCassandraOps.execute(anyObject())).thenReturn(null)
			
			def orgService = new OrganizationServiceImpl()
			orgService.cassandraOps = mockCassandraOps
			
			def updatedOrg = orgService.updateOrganization(oldOrg)
		
		then:
			updatedOrg.orgName == "testname"
			updatedOrg.orgType == "CLIENT"
			updatedOrg.parentOrgName == "testname2"
	}
	
	def "delete organization by name and type"() {
		when:
			def org1 = new Organization(orgName: "testname1", orgType: "SUBCLIENT", parentOrgName: "testname", parentOrgType: "CLIENT")
			def org2 = new Organization(orgName: "testname2", orgType: "SUBCLIENT", parentOrgName: "testname", parentOrgType: "CLIENT")
	
			def mockCassandraOps = mock(CassandraOperations.class)
			when(mockCassandraOps.selectOne(anyObject(), anyObject())).thenReturn(org1)
			when(mockCassandraOps.select(anyObject(), anyObject())).thenReturn([org1, org2])
			
			doNothing().when(mockCassandraOps).delete(anyObject())
			when(mockCassandraOps.execute(anyObject())).thenReturn(null)
			
			def orgService = new OrganizationServiceImpl()
			orgService.cassandraOps = mockCassandraOps
			
			orgService.deleteOrganizationByNameAndType("testname", "CLIENT")
		
		then:
			true
	}
	
	def "get single organization history"() {
		when:
			def mockCassandraOps = mock(CassandraOperations.class)
			when(mockCassandraOps.select(anyObject(), anyObject())).thenReturn([new Organization(orgName: "testname", orgType: "CLIENT")])
	
			
			def orgService = new OrganizationServiceImpl()
			orgService.cassandraOps = mockCassandraOps
			
			def orgs = orgService.getOrganizationHistory("testname", "CLIENT")
		
		then:
			orgs.size() == 1
			orgs[0].orgName == "testname"
			orgs[0].orgType == "CLIENT"
			
	}
}
