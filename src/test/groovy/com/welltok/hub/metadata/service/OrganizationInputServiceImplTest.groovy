package com.welltok.hub.metadata.service

import static org.mockito.Mockito.*

import org.springframework.data.cassandra.core.CassandraOperations

import spock.lang.Specification

import com.welltok.hub.metadata.model.OrganizationInput

class OrganizationInputServiceImplTest extends Specification {
	
	def "getOrganizationInput"() {
		when:
			def orgInput1 = new OrganizationInput(orgName: "testname", orgType: "CLIENT")
		
			def mockCassandraOps = mock(CassandraOperations.class)
			when(mockCassandraOps.select(anyObject(), anyObject())).thenReturn([orgInput1])
			
			def orgInputService = new OrganizationInputServiceImpl()
			orgInputService.cassandraOps = mockCassandraOps
			
			def orgInput = orgInputService.getOrganizationInput("testname", "CLIENT")
		
		then:
			orgInput.orgName == "testname"
			orgInput.orgType == "CLIENT"
	}
	
	def "updateOrganizationInput"() {
		when:
			def oldOrgInput = new OrganizationInput(orgName: "testname", orgType: "CLIENT", inputIds: [UUID.randomUUID()])
			def newOrgInput = new OrganizationInput(orgName: "testname", orgType: "CLIENT", inputIds: [UUID.randomUUID(), UUID.randomUUID()])
					
			def mockCassandraOps = mock(CassandraOperations.class)
			when(mockCassandraOps.update(anyObject())).thenReturn(newOrgInput)
			when(mockCassandraOps.execute(anyObject())).thenReturn(null)
			
			def orgInputService = new OrganizationInputServiceImpl()
			orgInputService.cassandraOps = mockCassandraOps
			
			def updatedOrgInput = orgInputService.updateOrganizationInput(oldOrgInput)
		
		then:
			updatedOrgInput.orgName == "testname"
			updatedOrgInput.orgType == "CLIENT"
			updatedOrgInput.inputIds.size() == 2
	}
	
	def "deleteOrganizationInput"() {
		when:
			def orgInput = new OrganizationInput(orgName: "testname", orgType: "CLIENT", inputIds: [UUID.randomUUID()])

			def mockCassandraOps = mock(CassandraOperations.class)		
			doNothing().when(mockCassandraOps).delete(anyObject())
			when(mockCassandraOps.execute(anyObject())).thenReturn(null)
			
			def orgInputService = new OrganizationInputServiceImpl()
			orgInputService.cassandraOps = mockCassandraOps
			
			orgInputService.deleteOrganizationInput(orgInput)
		
		then:
			true
	}
}
