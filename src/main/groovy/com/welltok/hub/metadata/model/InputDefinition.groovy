package com.welltok.hub.metadata.model

import org.springframework.cassandra.core.PrimaryKeyType
import org.springframework.data.cassandra.mapping.Column
import org.springframework.data.cassandra.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.mapping.Table

/**
 * The InputDefinition model
 *
 * @author mferritto
 *
 */
@Table("input_definition")
class InputDefinition {
	
	@PrimaryKeyColumn(name = "input_id", ordinal = 1, type = PrimaryKeyType.PARTITIONED)
	UUID inputId
	
	@PrimaryKeyColumn(name = "effective_date", ordinal = 1, type = PrimaryKeyType.PARTITIONED)
	Date effectiveDate

	@Column("input_type") String inputType
	@Column("location_type") String locationType
	@Column("server_name") String serverName
	@Column("username") String username
	@Column("password") String password
	@Column("is_encrypted") Boolean isEncrypted
	@Column("encryptionType") String encryptionType
	@Column("pass_phrase") String passPhrase
	@Column("destination") String destination
	@Column("feed_type") String feedType
	@Column("file_location") String fileLocation
	@Column("file_name") String fileName
	@Column("is_zipped") Boolean isZipped
	@Column("port_number") Integer portNumber
	@Column("client_org_name") String clientOrgName
	@Column("subclient_org_name") String subclientOrgName
	@Column("carrier_org_name") String carrierOrgName
	@Column("file_delimiter") String fileDelimiter
	@Column("file_header") Integer fileHeader
	@Column("file_footer") Integer fileFooter
	@Column("file_field_count") Integer fileFieldCount
	@Column("input_file_ext") String inputFileExt
	@Column("output_file_ext") String outputFileExt
	@Column("expected_rows_low") Long expectedRowsLow
	@Column("expected_rows_high") Long expectedRowsHigh
	@Column("schedule_cron_expression") String scheduleCronExpression
	@Column("job_id") UUID jobId
	@Column("input_layout_id") String inputLayoutId
	@Column("output_layout_id") String outputLayoutId
	@Column("process_id") UUID processId
	@Column("code_mapping_names") List<String> codeMappingNames
	@Column("qc_thresholds") Map<String, Integer> qcThresholds
	@Column("compression_type") String compressionType
	@Column("private_key_location") String privateKeyLocation
	@Column("is_compressed") Boolean isCompressed
	@Column("file_format") String fileFormat
	@Column("status") String status

}
