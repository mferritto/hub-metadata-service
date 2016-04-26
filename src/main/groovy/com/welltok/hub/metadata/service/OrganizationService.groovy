package com.welltok.hub.metadata.service

import java.util.List;

import com.welltok.hub.metadata.model.Organization

interface OrganizationService {

	List<Organization> getOrganizations()
	List<Organization> getOrganizationsByName(String orgName)
	List<Organization> getOrganizationsByType(String orgType)
	Organization getOrganizationByNameAndType(String orgName, String orgType)
	List<Organization> getSubclients(String clientName)
	Organization createOrganization(Organization organization)
	Organization updateOrganization(Organization organization)
	void deleteOrganizationByNameAndType(String orgName, String orgType)
	List getOrganizationHistory(String orgName, String orgType)

}
