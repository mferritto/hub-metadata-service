package com.welltok.hub.metadata.model

import org.springframework.data.cassandra.mapping.Column

class AuditedDocument {
	
	@Column("modified_at") Date modifiedAt
	@Column("modified_by") String modifiedBy
}
