package com.welltok.hub.metadata.service

import static org.mockito.Mockito.*

import org.springframework.data.cassandra.core.CassandraOperations

import spock.lang.Specification

import com.welltok.hub.metadata.model.InputDefinition
import com.welltok.hub.metadata.model.OrganizationInput

class InputDefinitionServiceImplTest extends Specification {

	def "get all input definitions"() {
		when:
			def input1 = new InputDefinition(inputId: UUID.randomUUID())
			def input2 = new InputDefinition(inputId: UUID.randomUUID())

			def mockCassandraOps = mock(CassandraOperations.class)
			when(mockCassandraOps.select(anyObject(),anyObject())).thenReturn([input1, input2])
			
			def inputService = new InputDefinitionServiceImpl()
			inputService.cassandraOps = mockCassandraOps
			
			def inputs = inputService.getInputDefinitions()
		
		then:
			inputs.size() == 2
			inputs[0].inputId == input1.inputId
			inputs[1].inputId == input2.inputId
	}
	
	def "get all input definitions by input id"() {
		when:
		
			def inputId = UUID.randomUUID()
			def input1 = new InputDefinition(inputId: inputId)
			def input2 = new InputDefinition(inputId: inputId)

			def mockCassandraOps = mock(CassandraOperations.class)
			when(mockCassandraOps.select(anyObject(),anyObject())).thenReturn([input1, input2])
			
			def inputService = new InputDefinitionServiceImpl()
			inputService.cassandraOps = mockCassandraOps
			
			def inputs = inputService.getInputDefinitionsById(inputId)
		
		then:
			inputs.size() == 2
			inputs[0].inputId == inputId
			inputs[1].inputId == inputId
	}
	
	def "get all input definitions before date"() {
		when:
			def date = Date.parse("yyyy-MM-dd hh:mm:ss", "2016-05-01 00:00:00")
			def effectiveDate = Date.parse("yyyy-MM-dd hh:mm:ss", "2016-04-01 00:00:00")
			def inputId = UUID.randomUUID()
			def input1 = new InputDefinition(inputId: inputId, effectiveDate: effectiveDate)
			def input2 = new InputDefinition(inputId: inputId, effectiveDate: effectiveDate)

			def mockCassandraOps = mock(CassandraOperations.class)
			when(mockCassandraOps.select(anyObject(),anyObject())).thenReturn([input1, input2])
			
			def inputService = new InputDefinitionServiceImpl()
			inputService.cassandraOps = mockCassandraOps
			
			def inputs = inputService.getInputDefinitionsByIdBeforeDate(inputId, date)
		
		then:
			inputs.size() == 2
			inputs[0].inputId == inputId
			inputs[1].inputId == inputId
	}
	
	def "get all input definitions after date"() {
		when:
			def date = Date.parse("yyyy-MM-dd hh:mm:ss", "2016-04-01 00:00:00")
			def effectiveDate = Date.parse("yyyy-MM-dd hh:mm:ss", "2016-05-01 00:00:00")
			def inputId = UUID.randomUUID()
			def input1 = new InputDefinition(inputId: inputId, effectiveDate: effectiveDate)
			def input2 = new InputDefinition(inputId: inputId, effectiveDate: effectiveDate)

			def mockCassandraOps = mock(CassandraOperations.class)
			when(mockCassandraOps.select(anyObject(),anyObject())).thenReturn([input1, input2])
			
			def inputService = new InputDefinitionServiceImpl()
			inputService.cassandraOps = mockCassandraOps
			
			def inputs = inputService.getInputDefinitionsByIdAfterDate(inputId, date)
		
		then:
			inputs.size() == 2
			inputs[0].inputId == inputId
			inputs[1].inputId == inputId
	}
	
	def "get latest input definition by id"() {
		when:
			def input1 = new InputDefinition(inputId: UUID.randomUUID())
		
			def mockCassandraOps = mock(CassandraOperations.class)
			when(mockCassandraOps.select(anyObject(),anyObject())).thenReturn([input1])
			
			def inputService = new InputDefinitionServiceImpl()
			inputService.cassandraOps = mockCassandraOps
			
			def input = inputService.getLatestInputDefinitionById(input1.inputId)
		
		then:
			input.inputId == input1.inputId
	}
	
	def "create input definition"() {
		when:
			def input = new InputDefinition(inputId: UUID.randomUUID())
			
			def mockCassandraOps = mock(CassandraOperations.class)
			when(mockCassandraOps.insert(anyObject())).thenReturn(input)
			
			def inputService = new InputDefinitionServiceImpl()
			inputService.cassandraOps = mockCassandraOps
			
			def createdInput = inputService.createInputDefinition(input)
		
		then:
			createdInput.inputId == input.inputId	
	}
	
	def "update input definition"() {
		when:
			def inputId = UUID.randomUUID()
			def oldInput = new InputDefinition(inputId: inputId, isEncrypted: false)
			def newInput = new InputDefinition(inputId: inputId, isEncrypted: true)
					
			def mockCassandraOps = mock(CassandraOperations.class)
			when(mockCassandraOps.update(anyObject())).thenReturn(newInput)
			when(mockCassandraOps.execute(anyObject())).thenReturn(null)
			
			def inputService = new InputDefinitionServiceImpl()
			inputService.cassandraOps = mockCassandraOps
			
			def updatedInput = inputService.updateInputDefinition(oldInput)
		
		then:
			updatedInput.inputId == newInput.inputId
			updatedInput.isEncrypted == true
	}
	
	def "delete input definitions by name by input id"() {
		when:
			def inputId = UUID.randomUUID()
			def input1 = new InputDefinition(inputId: inputId, clientOrgName: "testclient", subclientOrgName: "testsubclient")
			def input2 = new InputDefinition(inputId: inputId, clientOrgName: "testclient", subclientOrgName: "testsubclient")
			def orgInput1 = new OrganizationInput(orgName: "testclient", orgType: "CLIENT", inputIds: [inputId])
			def orgInput2 = new OrganizationInput(orgName: "testsubclient", orgType: "SUBCLIENT", inputIds: [inputId])
	
			def mockCassandraOps = mock(CassandraOperations.class)
			when(mockCassandraOps.select(anyObject(),anyObject())).thenReturn([input1, input2]).thenReturn([orgInput1]).thenReturn([orgInput2])
			doNothing().when(mockCassandraOps).delete(anyObject())
			when(mockCassandraOps.execute(anyObject())).thenReturn(null)
			
			def inputService = new InputDefinitionServiceImpl()
			inputService.cassandraOps = mockCassandraOps
			
			inputService.deleteInputDefintions(inputId)
		
		then:
			true
	}
	
	def "delete input definition by name by input id and date"() {
		when:
			def inputId = UUID.randomUUID()
			def effectiveDate = new Date()
			def input1 = new InputDefinition(inputId: inputId, effectiveDate: effectiveDate, clientOrgName: "testclient", subclientOrgName: "testsubclient")
			def orgInput1 = new OrganizationInput(orgName: "testclient", orgType: "CLIENT", inputIds: [inputId])
			def orgInput2 = new OrganizationInput(orgName: "testsubclient", orgType: "SUBCLIENT", inputIds: [inputId])
	
			def mockCassandraOps = mock(CassandraOperations.class)
			when(mockCassandraOps.select(anyObject(),anyObject())).thenReturn([input1]).thenReturn([orgInput1]).thenReturn([orgInput2])
			doNothing().when(mockCassandraOps).delete(anyObject())
			when(mockCassandraOps.execute(anyObject())).thenReturn(null)
			
			def inputService = new InputDefinitionServiceImpl()
			inputService.cassandraOps = mockCassandraOps
			
			inputService.deleteInputDefinitionByIdAndDate(inputId, effectiveDate)
		
		then:
			true
	}
	
	
}