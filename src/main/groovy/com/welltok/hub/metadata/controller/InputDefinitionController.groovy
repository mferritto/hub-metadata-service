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
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody

import com.thoughtworks.xstream.XStream
import com.welltok.hub.metadata.model.InputDefinition
import com.welltok.hub.metadata.model.Organization
import com.welltok.hub.metadata.service.InputDefinitionService
import com.welltok.hub.metadata.util.RecordDigest

@RequestMapping("/inputDefinitions")
@Controller()
class InputDefinitionController {
	
	Logger logger = Logger.getLogger(InputDefinitionController.class)

	@Autowired InputDefinitionService inputDefinitionService
	@Autowired RecordDigest recordDigest
	@Autowired XStream xstream
	
	/**
	 * Get all input definitions available.
	 *
	 * @return - list of input definitions found
	 */
	@RequestMapping(method=RequestMethod.GET)
	@ResponseBody List<InputDefinition> getAllInputDefinitions() {
		inputDefinitionService.getInputDefinitions()
	}
	
	/**
	 * Get all input definitions by org name and org type.
	 *
	 * @param name - the name of the org to query by
	 * @param type - the type of the org to query by
	 * @return - list of input definitions found
	 */
	
	@RequestMapping(value="/{name}/{type}", method=RequestMethod.GET)
	@ResponseBody List<InputDefinition> getInputDefinitionsByOrg(@PathVariable("name") String orgName, @PathVariable("type") String orgType) {
		inputDefinitionService.getInputDefinitionsByOrg(orgName, orgType)
	}
	
	/**
	 * Get all input definitions by client and subclient
	 *
	 * @param clientName - the name of the client to query by
	 * @param subclientName - the type of the subclient to query by
	 * @return - list of input definitions found
	 */
	
	@RequestMapping(value="/client/{clientName}/subclient/{subclientName}", method=RequestMethod.GET)
	@ResponseBody Set<InputDefinition> getInputDefinitionsByClientAndSubclient(@PathVariable("clientName") String clientName, @PathVariable("subclientName") String subclientName) {
		inputDefinitionService.getInputDefinitionsByClientAndSubclient(clientName, subclientName)
	}
	
	/**
	 * Get a all input definitions before date
	 *
	 * @param id - the input id to query by
	 * @param date - the effective date to query by
	 * @return - the input definition found
	 */
	
	@RequestMapping(value="/{id}/beforeDate/{date}", method=RequestMethod.GET)
	@ResponseBody List<InputDefinition> getInputDefinitionsBeforeDate(@PathVariable("id") String inputId, @PathVariable("date") String date) {
		def effectiveDate
		try {
			effectiveDate = new Date().parse("yyyyMMdd", date)
		} catch (ex) {
			 logger.info("INVALID DATE")
			 throw new Exception("INVALID DATE")
		}
		inputDefinitionService.getInputDefinitionsBeforeDate(UUID.fromString(inputId), date)
	}
	
	/**
	 * Get all input definitions w/matching id
	 *
	 * @param id - the input id to query by
	 * @return - the latest input definition found
	 */
	@RequestMapping(value="/{id}", method=RequestMethod.GET)
	@ResponseBody List<InputDefinition> getInputDefinitionsById(@PathVariable("id") String inputId) {
		inputDefinitionService.getInputDefinitionsById(UUID.fromString(inputId))
	}
	
	/**
	 * Get the latest input definition
	 *
	 * @param id - the input id to query by
	 * @return - the latest input definition found
	 */
	@RequestMapping(value="/{id}/latest", method=RequestMethod.GET)
	@ResponseBody InputDefinition getLatestInputDefinition(@PathVariable("id") String inputId) {
		inputDefinitionService.getLatestInputDefinition(UUID.fromString(inputId))
	}
	
	/**
	 * Get a single input definition by id and date
	 *
	 * @param id - the input id to query by
	 * @param date - the effective date to query by
	 * @return - the input definition found
	 */
	
	@RequestMapping(value="/{id}/date/{date}", method=RequestMethod.GET)
	@ResponseBody InputDefinition getInputDefinitionByIdAndDate(@PathVariable("id") String inputId, @PathVariable("date") String date) {
		def effectiveDate
		try {
			effectiveDate = new Date().parse("yyyyMMdd", date)
		} catch (ex) {
			 logger.info("INVALID DATE")
			 throw new Exception("INVALID DATE")
		}
		inputDefinitionService.getInputDefinitionByIdAndDate(UUID.fromString(inputId), date)
	}
	
	
	@RequestMapping(method=RequestMethod.POST)
	@ResponseBody Object createInputDefinition(@RequestBody InputDefinition inputDefinition, HttpServletRequest request) {

		/*
		def errors = []
		def result = orgValidator.validate(null, organization, errors)
		if(!result) {
			def message = 'Create organization is not valid'
			throw new ValidationException(message,errors)
		}
		*/

		logger.info("CREATING INPUT DEFINITION")
		def createdOrganization = inputDefinitionService.createInputDefinition(inputDefinition)
		getRecordDigestResponse(createdOrganization)
	}
	

	@RequestMapping(method=RequestMethod.PUT)
	@ResponseBody Object updateInputDefinition(@RequestBody InputDefinition inputDefinition, HttpServletRequest request) {

		/*
		def errors = []
		def result = orgValidator.validate(null, organization, errors)
		if(!result) {
			def message = 'Create organization is not valid'
			throw new ValidationException(message,errors)
		}
		*/
		
		def createdInputDefinition = inputDefinitionService.createInputDefinition(inputDefinition)
		getRecordDigestResponse(createdInputDefinition)
	}
	
	def getRecordDigestResponse(Organization org) {
		HttpHeaders responseHeaders = new HttpHeaders()
		responseHeaders.set("recordDigest", recordDigest.getHexSha1Digest(xstream.toXML(org)))
		new ResponseEntity<Organization>(org, responseHeaders, HttpStatus.OK)
	}
}
