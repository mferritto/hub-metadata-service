package com.welltok.hub.metadata.service

import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.cassandra.core.CassandraOperations

import com.welltok.hub.metadata.model.OrganizationInput

class OrganizationInputServiceImpl implements OrganizationInputService {
	
	Logger logger = Logger.getLogger(OrganizationInputServiceImpl.class)

	@Autowired CassandraOperations cassandraOps
	String orgInputTable = "organization_input"

	@Override
	public OrganizationInput getOrganizationInput(String orgName, String orgType) {
		def results = cassandraOps.select("SELECT * FROM ${orgInputTable} WHERE org_name = '${orgName}' AND org_type = '${orgType}'", OrganizationInput.class)
		results && results.size() > 0  ? results[0] : null
	}
	
	@Override
	public OrganizationInput updateOrganizationInput(OrganizationInput orgInput) {
		cassandraOps.update(orgInput)
	}
	
	@Override
	public void deleteOrganizationInput(OrganizationInput orgInput) {
		cassandraOps.delete(orgInput)
	}
}
