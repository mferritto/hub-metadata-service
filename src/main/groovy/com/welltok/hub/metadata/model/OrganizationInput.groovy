package com.welltok.hub.metadata.model

import org.springframework.cassandra.core.PrimaryKeyType
import org.springframework.data.cassandra.mapping.Column
import org.springframework.data.cassandra.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.mapping.Table

@Table("organization_input")
class OrganizationInput {
	
	@PrimaryKeyColumn(name = "org_name", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
	String orgName		// the name of the organization
  
	@PrimaryKeyColumn(name = "org_type", ordinal = 1, type = PrimaryKeyType.PARTITIONED)
	String orgType		// the organization type (client, sub-client, carrier)
	
	@Column("input_ids") Set<UUID> inputIds	// the set of input_ids relating to the client/subclient
}
