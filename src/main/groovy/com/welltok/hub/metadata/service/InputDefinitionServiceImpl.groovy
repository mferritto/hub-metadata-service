package com.welltok.hub.metadata.service

import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.cassandra.core.CassandraOperations

import com.welltok.hub.metadata.model.InputDefinition

class InputDefinitionServiceImpl implements InputDefinitionService {

	Logger logger = Logger.getLogger(InputDefinitionServiceImpl.class)
	
	@Autowired CassandraOperations cassandraOps
	@Autowired OrganizationService orgService
	@Autowired OrganizationInputService orgInputService
	String inputDefinitionTable = "input_definition"
	
	@Override
	public List<InputDefinition> getInputDefinitions() {
		cassandraOps.select("SELECT * FROM ${inputDefinitionTable}", InputDefinition.class)
	}
	
	@Override
	public List<InputDefinition> getInputDefinitionsByOrg(String orgName, String orgType) {
		def orgInput = orgInputService.getOrganizationInput(orgName, orgType)
		def inputDefs = []
		orgInput?.inputIds?.each { inputId ->
			inputDefs << getLatestInputDefinition(inputId)
		}
		inputDefs
	}
	
	@Override
	public List<InputDefinition> getInputDefinitionsBeforeDate(UUID inputId, String effectiveDate) {
		cassandraOps.select("SELECT * FROM ${inputDefinitionTable} where input_id = ${inputId.toString()} AND effective_date <= '${effectiveDate.toString()}'", InputDefinition.class)
	}
	
	@Override
	public List<InputDefinition> getInputDefinitionsById(UUID inputId) {
		cassandraOps.select("SELECT * FROM ${inputDefinitionTable} where input_id = ${inputId.toString()}", InputDefinition.class)
	}
	
	//get input definition by client and subclient info
	@Override
	public Set<InputDefinition> getInputDefinitionsByClientAndSubclient(String clientName, String sublientName) {
		def clientOrgInput = orgInputService.getOrganizationInput(clientName, "CLIENT")
		def subclientOrgInput = orgInputService.getOrganizationInput(sublientName, "SUBCLIENT")
		
		def inputDefs = []
		clientOrgInput?.inputIds?.each { inputId ->
			def result = getLatestInputDefinition(inputId)
			inputDefs << result
		}
		
		subclientOrgInput?.inputIds?.each { inputId ->
			inputDefs << getLatestInputDefinition(inputId)
		}

		inputDefs as Set
	}

	//get an input definition w/a specific date
	@Override
	public InputDefinition getInputDefinitionByIdAndDate(UUID inputId, String effectiveDate) {
		cassandraOps.select("SELECT * FROM ${inputDefinitionTable} where input_id = ${inputId.toString()} AND effective_date = ${effectiveDate.toString()}", InputDefinition.class)
	}
	
	//grab the latest input definition by id
	@Override
	public InputDefinition getLatestInputDefinition(UUID inputId) {
		def results = cassandraOps.select("SELECT * FROM ${inputDefinitionTable} where input_id = ${inputId.toString()} LIMIT 1", InputDefinition.class)
		results && results.size() > 0  ? results[0] : null
	}
	
	//grab the latest input definition status by id
	@Override
	public InputDefinition getLatestInputDefinitionStatus(UUID inputId) {
		def results = cassandraOps.select("SELECT status FROM ${inputDefinitionTable} where input_id = ${inputId.toString()} LIMIT 1", InputDefinition.class)
		results && results.size() > 0  ? results[0] : null
	}

	@Override
	public InputDefinition createInputDefinition(InputDefinition inputDefintion) {
		
		//if input id is not passed in, create new inputId
		if (!inputDefintion?.inputId) {
			inputDefintion.inputId = UUID.randomUUID()
		}
		
		//if effective date is not passed in, create new effectiveDate
		if (!inputDefintion?.effectiveDate) {
			inputDefintion.effectiveDate = new Date()
		}
		
		//TURN INTO BATCH CALL
		def clientInput = orgInputService.getOrganizationInput(inputDefintion.clientOrgName, "CLIENT")
		
		clientInput.inputIds << inputDefintion.inputId
		
		orgInputService.updateOrganizationInput(clientInput)
		
		def sublientInput = orgInputService.getOrganizationInput(inputDefintion.subclientOrgName, "SUBCLIENT")
		sublientInput.inputIds << inputDefintion.inputId
		orgInputService.updateOrganizationInput(sublientInput)

		cassandraOps.insert(inputDefintion)
	}

}
