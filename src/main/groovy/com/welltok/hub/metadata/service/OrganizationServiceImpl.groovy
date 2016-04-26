package com.welltok.hub.metadata.service

import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.cassandra.core.CassandraOperations

import com.datastax.driver.core.ConsistencyLevel
import com.datastax.driver.core.querybuilder.Batch
import com.datastax.driver.core.querybuilder.Delete
import com.datastax.driver.core.querybuilder.Insert
import com.datastax.driver.core.querybuilder.QueryBuilder
import com.datastax.driver.core.querybuilder.Update
import com.welltok.hub.metadata.model.Organization

class OrganizationServiceImpl implements OrganizationService {

	Logger logger = Logger.getLogger(OrganizationServiceImpl.class)
	
	String orgTable = "organization"
	String orgHistoryTable = "organization_history"
	@Autowired CassandraOperations cassandraOps
	
	@Override
	public List<Organization> getOrganizations() {
		cassandraOps.select("SELECT * FROM ${orgTable}", Organization.class)
	}

	@Override
	public List<Organization> getOrganizationsByName(String orgName) {
		cassandraOps.select("SELECT * FROM ${orgTable} WHERE org_name = '${orgName}'", Organization.class)
	}
	
	@Override
	public List<Organization> getOrganizationsByType(String orgType) {
		logger.info("GETTING ORGANIZATION")
		cassandraOps.select("SELECT * FROM ${orgTable} WHERE org_type = '${orgType}' ALLOW FILTERING", Organization.class)
	}

	@Override
	public Organization getOrganizationByNameAndType(String orgName, String orgType) {
		def results = cassandraOps.select("SELECT * FROM ${orgTable} WHERE org_name = '${orgName}' AND org_type = '${orgType}'", Organization.class)
		logger.info("RESULUTS: ${results}")
		logger.info("RESULUTS: ${results.size()}")
		logger.info("RESULUTS: ${results[0]}")
		results && results.size() > 0  ? results[0] : null
	}
	
	@Override
	public List<Organization> getSubclients(String clientName) {
		logger.info("clientName: ${clientName}")
		cassandraOps.select("SELECT * FROM ${orgTable} WHERE parent_org_name = '${clientName}' AND parent_org_type = 'CLIENT' ALLOW FILTERING", Organization.class)
	}
	
	@Override
	public Organization createOrganization(Organization organization) {
		organization.modifiedAt = new Date()
		insertHistory(organization)
		cassandraOps.insert(organization)
	}

	@Override
	public Organization updateOrganization(Organization organization) {
		organization.modifiedAt = new Date()
		insertHistory(organization)
		cassandraOps.update(organization)
	}
	
	//check organization_inputs table, if client has inputs in table, do NOT allow delete
	@Override
	public void deleteOrganizationByNameAndType(String orgName, String orgType) {
		
		Batch batch = QueryBuilder.batch()
		
		//grab org, insert history
		def existingOrg = getOrganizationByNameAndType(orgName, orgType)
		Insert historyInsert = insertHistory(existingOrg)
		batch.add(historyInsert)

		//update any orgs that are subclients of existingOrg
		def subclients = getSubclients(orgName)
		Insert subClientInsert
		subclients.each { subclient ->
			subClientInsert = QueryBuilder.insertInto(orgTable)
			subClientInsert.value("org_name", subclient.orgName)
			subClientInsert.value("org_type", subclient.orgType)
			subClientInsert.value("parent_org_name", null)
			subClientInsert.value("parent_org_type", null)
			subClientInsert.value("modified_at", new Date())
			batch.add(subClientInsert)
		}
		
		//delete org
		Delete deleteOrg = QueryBuilder.delete().from(orgTable)
		deleteOrg.where(QueryBuilder.eq("org_name", orgName))
		deleteOrg.where(QueryBuilder.eq("org_type", orgType))
		batch.add(deleteOrg)

		cassandraOps.execute(batch)
	}
	
	@Override
	public List getOrganizationHistory(String orgName, String orgType) {
		cassandraOps.select("SELECT * FROM ${orgHistoryTable} where org_name = '${orgName}' AND org_type = '${orgType}'", Organization.class)
	}

	private Insert insertHistory(Organization organization) {
		Insert insertHistory = QueryBuilder.insertInto(orgHistoryTable)
		insertHistory.setConsistencyLevel(ConsistencyLevel.QUORUM)
		insertHistory.value("org_name", organization.orgName)
		insertHistory.value("org_type", organization.orgType)
		insertHistory.value("parent_org_name", organization.parentOrgName)
		insertHistory.value("parent_org_type", organization.parentOrgType)
		insertHistory.value("modified_by", organization.modifiedBy)
		insertHistory.value("modified_at", organization.modifiedAt)
		insertHistory
	}

}
