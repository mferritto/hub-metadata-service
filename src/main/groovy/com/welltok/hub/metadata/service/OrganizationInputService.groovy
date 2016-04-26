package com.welltok.hub.metadata.service

import com.welltok.hub.metadata.model.OrganizationInput;

interface OrganizationInputService {
	
	OrganizationInput getOrganizationInput(String orgName, String orgType)
	OrganizationInput updateOrganizationInput(OrganizationInput orgInput)
	void deleteOrganizationInput(OrganizationInput orgInput)

}
