package com.welltok.hub.metadata.service

import com.welltok.hub.metadata.model.InputDefinition

interface InputDefinitionService {

	List<InputDefinition> getInputDefinitions()
	List<InputDefinition> getInputDefinitionsByOrg(String orgName, String orgType)
	List<InputDefinition> getInputDefinitionsBeforeDate(UUID inputId, String effectiveDate)
	List<InputDefinition> getInputDefinitionsById(UUID inputId)
	Set<InputDefinition> getInputDefinitionsByClientAndSubclient(String clientName, String subClientName)
	InputDefinition getInputDefinitionByIdAndDate(UUID inputId, String effectiveDate)
	InputDefinition getLatestInputDefinition(UUID inputId)
	InputDefinition getLatestInputDefinitionStatus(UUID inputId)
	InputDefinition createInputDefinition(InputDefinition inputDefintion)
}
