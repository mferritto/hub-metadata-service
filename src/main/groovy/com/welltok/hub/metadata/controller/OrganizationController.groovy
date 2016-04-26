package com.welltok.hub.metadata.controller

import javax.servlet.http.HttpServletRequest

import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody

import com.thoughtworks.xstream.XStream
import com.welltok.hub.metadata.model.DataAlreadyUpdatedException
import com.welltok.hub.metadata.model.DataNotFoundException
import com.welltok.hub.metadata.model.NothingChangedException
import com.welltok.hub.metadata.model.Organization
import com.welltok.hub.metadata.model.ValidationException
import com.welltok.hub.metadata.service.OrganizationInputService
import com.welltok.hub.metadata.service.OrganizationService
import com.welltok.hub.metadata.util.RecordDigest
import com.welltok.hub.metadata.validator.OrganizationValidator

@RequestMapping("/organizations")
@Controller()
class OrganizationController {
	
	Logger logger = Logger.getLogger(OrganizationController.class)

	@Autowired OrganizationService orgService
	@Autowired OrganizationInputService orgInputService
	@Autowired OrganizationValidator orgValidator
	@Autowired XStream xstream
	@Autowired RecordDigest recordDigest

	/**
	 * Get all organizations available.
	 *
	 * @return - list of organizations found
	 */
	@RequestMapping(method=RequestMethod.GET)
	@ResponseBody List<Organization> getAllOrganizations() {
		orgService.getOrganizations()
	}

	/**
	 * Get all organizations by name.
	 *
	 * @param name - the name of the organization to find
	 * @return - list of organizations found
	 */
	@RequestMapping(value="/name/{name}", method=RequestMethod.GET)
	@ResponseBody List<Organization> getOrganizationsByName(@PathVariable("name") String orgName) {
		def orgs = orgService.getOrganizationsByName(orgName)
		logger.info(orgs.dump())
		orgs
	}

	/**
	 * Get all organizations by type.
	 *
	 * @param type - the type of the organization to find
	 * @return - list of organizations found
	 */
	@RequestMapping(value="/type/{type}", method=RequestMethod.GET)
	@ResponseBody List<Organization> getOrganizationsByType(@PathVariable("type") String orgType) {
		orgService.getOrganizationsByType(orgType)
	}

	/**
	 * Get an organization by name and type.
	 *
	 * @param name - the name of the organization to find
	 * @param type - the type of the organization to find
	 * @return - the organization found
	 */
	@RequestMapping(value="/{name}/{type}", method=RequestMethod.GET)
	@ResponseBody Object getOrganizationByTypeAndName(@PathVariable("name") String orgName, @PathVariable("type") String orgType) {
		def org = orgService.getOrganizationByNameAndType(orgName, orgType)
		getRecordDigestResponse(org)
	}

	/**
	 * Get all subclients of client.
	 *
	 * @param name - the name of the client to query by
	 * @return - list of subclients found
	 */
	@RequestMapping(value="/{name}/subclients", method=RequestMethod.GET)
	@ResponseBody List<Organization> getSubclients(@PathVariable("name") String clientName) {
		orgService.getSubclients(clientName)
	}

	/**
	 * Create an new organization. The organization to create must first pass validation, if validation fails then a ValidationException
	 * is thrown with the list of errors.
	 *
	 * Response header will include dataDigest with a digest value for the created organization value.
	 *
	 * @param organization - the organization to create
	 * @return - the created organization
	 */
	@RequestMapping(method=RequestMethod.POST)
	@ResponseBody Object createOrganization(@RequestBody Organization organization, HttpServletRequest request) {

		def errors = []

		def result = orgValidator.validate(null, organization, errors)
		if(!result) {
			def message = "Create organization is not valid."
			throw new ValidationException(message,errors)
		}

		organization.modifiedBy = "mferritto"
		def createdOrganization = orgService.createOrganization(organization)
		getRecordDigestResponse(createdOrganization)
	}

	/**
	 * Create an new organization. The organization to create must first pass validation, if validation fails then a ValidationException
	 * is thrown with the list of errors.
	 *
	 * Response header will include dataDigest with a digest value for the created organization value.
	 *
	 * @param organization - the organization to create
	 * @return - the created organization
	 */
	@RequestMapping(value="/{name}/{type}", method=RequestMethod.PUT)
	@ResponseBody Object updateOrganization(@PathVariable("name") String orgName, @PathVariable("type") String orgType,
		@RequestBody Organization organization, @RequestHeader("recordDigest") String oldDigest, HttpServletRequest request) {

		def existingOrganization = orgService.getOrganizationByNameAndType(orgName, orgType)
		logger.info("EXISTING ORG: ${existingOrganization.orgName}, ${existingOrganization.orgType}")
		def errors = []
		if(!existingOrganization){
			def message = 'Data Not Found'
			throw new DataNotFoundException(message)
		}

		/*
		if(organization.orgName != existingOrganization.orgName || organization.orgType != existingOrganization.orgType) {
			def message = 'Name and type is not same as the exisiting organization name and type.'
			throw new ValidationException(message,errors)
		}*/

		def currentDigest = recordDigest.getHexSha1Digest(xstream.toXML(existingOrganization))
		if (oldDigest != currentDigest) {
			def message = 'The recordDigest does not match digest for the existing organization, must have been updated already.'
			throw new DataAlreadyUpdatedException(message)
		}

		def result = orgValidator.validate(existingOrganization, organization, errors)
		if(!result) {
			def message = "Updated organization is not valid: ${errors.dump()}."
			throw new ValidationException(message,errors)
		}

		def newDigest = recordDigest.getHexSha1Digest(xstream.toXML(organization))
		if (newDigest == currentDigest) {
			def message = 'Update was called but the data has not changed.'
			throw new NothingChangedException(message)
		}
		
		organization.modifiedBy = "mferritto"
		def udpatedOrganization = orgService.updateOrganization(organization)
		getRecordDigestResponse(udpatedOrganization)
	}

	@RequestMapping(value="/{name}/{type}/history", method=RequestMethod.GET)
	@ResponseBody List<Organization> getOrganizationHistory(@PathVariable("name") String orgName, @PathVariable("type") String orgType) {
		orgService.getOrganizationHistory(orgName, orgType)
	}
	
	@RequestMapping(value="/{name}/{type}", method=RequestMethod.DELETE)
	@ResponseBody Object deleteOrganization(@PathVariable("name") String orgName, @PathVariable("type") String orgType) {
		
		def existingOrganization = orgService.getOrganizationByNameAndType(orgName, orgType)
		def errors = []
		if(!existingOrganization){
			def message = 'Data Not Found'
			throw new DataNotFoundException(message)
		}
		
		def orgInput = orgInputService.getOrganizationInput(orgName, orgType)
		if (orgInput?.inputIds) {
			def message = 'Cannot delete organization if there are input defintions still associated with it.'
			throw new ValidationException(message)
		}

		def deletedOrganization = orgService.deleteOrganizationByNameAndType(orgName, orgType)
		getRecordDigestResponse(deletedOrganization)

		[ "success" : true ]
	}

	def getRecordDigestResponse(Organization org) {
		HttpHeaders responseHeaders = new HttpHeaders()
		responseHeaders.set("recordDigest", recordDigest.getHexSha1Digest(xstream.toXML(org)))
		new ResponseEntity<Organization>(org, responseHeaders, HttpStatus.OK)
	}
}
