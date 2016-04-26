package com.welltok.hub.metadata.model

import org.springframework.cassandra.core.Ordering
import org.springframework.cassandra.core.PrimaryKeyType
import org.springframework.data.cassandra.mapping.Column
import org.springframework.data.cassandra.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.mapping.Table

/**
 * The Organization model represents a client, sub-client, or carrier information.
 *
 * @author mferritto
 *
 */
@Table("organization")
class Organization extends AuditedDocument {

	@PrimaryKeyColumn(name = "org_name", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
	String orgName	// the name of the organization
  
	@PrimaryKeyColumn(name = "org_type", ordinal = 1, type = PrimaryKeyType.PARTITIONED)
	String orgType	// the organization type (client, sub-client, carrier)
	
	@Column("parent_org_name") String parentOrgName	// the name of the parent organization
	@Column("parent_org_type") String parentOrgType // the type of the parent organization

}
