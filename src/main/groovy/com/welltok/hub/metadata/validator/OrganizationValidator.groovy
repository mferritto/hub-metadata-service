package com.welltok.hub.metadata.validator

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired

import com.welltok.hub.metadata.model.Organization
import com.welltok.hub.metadata.model.ValidationError
import com.welltok.hub.metadata.service.OrganizationService

class OrganizationValidator {
	
	Logger logger = Logger.getLogger(OrganizationValidator.class)
	
	@Autowired OrganizationService orgService

	public boolean validate(Organization oldObject, Organization newObject, List e) {
		def isValid = true
		
		if(oldObject) {
			logger.info("OLD ORG: ${oldObject.orgName}, ${oldObject.orgType}")
			logger.info("NEW ORG: ${newObject.orgName}, ${newObject.orgType}")
			if(oldObject.orgName != newObject.orgName || oldObject.orgType != newObject.orgType) {
				isValid = false
				e.add(new ValidationError(code:'orgName_orgType1', description: 'orgName and orgType cannot be changed during an update' , solution: 'Change the orgName and orgType to the original values', tag:'orgName_orgType'))
			}
			
		} else {

				
				if (!newObject.orgName) {
					isValid = false
					e.add(new ValidationError(code:'orgName1', description: 'orgName is null' , solution: 'Change the value to not null', tag:'orgName'))
				}
				
				if (!newObject.orgType) {
					isValid = false
					e.add(new ValidationError(code:'orgType1', description: 'orgType is null' , solution: 'Change the value to not null', tag:'orgType'))
				}
				
				if(newObject.orgType != "CLIENT" && newObject.orgType != "SUBCLIENT") {
					isValid = false
					e.add(new ValidationError(code:'orgType1', description: 'orgType is not client or subclient' , solution: 'Change the orgType to client or subclient', tag:'orgType1'))
				}
				
				if(newObject.orgName && newObject.orgType && orgService.getOrganizationByNameAndType(newObject.orgName, newObject.orgType)){
					isValid = false
					e.add(new ValidationError(code:'orgName_orgType2', description: "Organization w/orgName: ${newObject.orgName} and orgType: ${newObject.orgType} already exists" , solution: 'Change the orgName and orgTypeor update the existing org', tag:'orgName_orgType'))
				}
				
				if(newObject.modifiedAt) {
					isValid = false
					e.add(new ValidationError(code:'modifiedAt1', description: 'The field modifiedAt should be null' , solution: 'Change the value to null', tag:'modifiedAt'))
				}

				if(newObject.modifiedBy) {
					isValid = false
					e.add(new ValidationError(code:'modifiedBy1', description: 'The field modifiedBy should be null' , solution: 'Change the value to null', tag:'modifiedBy'))
				}
			
			
			if((newObject.parentOrgName && !newObject.parentOrgType) || (!newObject.parentOrgName && newObject.parentOrgType)) {
				isValid = false
				e.add(new ValidationError(code:'parentOrgName_parentOrgType1', description: 'Cannot have parentOrgName without parentOrgType' , solution: 'Populate both values or change to null', tag:'parentOrgName_parentOrgType'))
			}
			
			if((newObject.parentOrgName && newObject.parentOrgType) && (!orgService.getOrganizationByNameAndType(newObject.parentOrgName, newObject.parentOrgType))) {
				isValid = false
				e.add(new ValidationError(code:'parentOrgName_parentOrgType2', description: "Parent organization w/parentOrgName: ${newObject.parentOrgName} and parentOrgType: ${newObject.parentOrgType} does not exist" , solution: 'Find an existing organization', tag:'parentOrgName_parentOrgType'))
			}
			
		}
		
		isValid
	}
}
