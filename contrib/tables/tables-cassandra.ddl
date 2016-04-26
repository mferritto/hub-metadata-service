CREATE KEYSPACE metadata WITH replication = {'class': 'SimpleStrategy', 'replication_factor': '1'}  AND durable_writes = true;

CREATE TABLE metadata.organization (
    org_name text,
    org_type text,
    parent_org_name text,
    parent_org_type text,
    modified_at timestamp,
    modified_by text,
   PRIMARY KEY (org_name, org_type)
);

CREATE INDEX parent_org_name ON organization (parent_org_name);
CREATE INDEX parent_org_type ON organization (parent_org_type);

CREATE TABLE metadata.organization_history (
    org_name text,
    org_type text,
    parent_org_name text,
    parent_org_type text,
    modified_at timestamp,
    modified_by text,
    PRIMARY KEY ((org_name, org_type), modified_at)
) WITH CLUSTERING ORDER BY (modified_at ASC);