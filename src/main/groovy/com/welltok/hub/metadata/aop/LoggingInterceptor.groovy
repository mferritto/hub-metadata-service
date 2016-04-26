package com.welltok.hub.metadata.aop

import groovy.json.JsonBuilder

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import org.apache.log4j.Logger;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter

class LoggingInterceptor extends HandlerInterceptorAdapter {

	Logger logger = Logger.getLogger(LoggingInterceptor.class)
	
	@Override
	boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		long startTime = System.currentTimeMillis();
		request.setAttribute("startTime", startTime);
		return true;
	}
	
	@Override
	void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

		def startTime = (Long) request.getAttribute("startTime")
		def totalTime = System.currentTimeMillis() - startTime

		def msg = [
			request: [
				remoteHost: request.remoteHost,
				remotePort: request.remotePort,
				remoteAddr: request.remoteAddr,
				headers: combineRequestHeaders(request),
				characterEncoding: request.characterEncoding,
				contentType: request.contentType,
				cookies: request.cookies,
				localAddr: request.localAddr,
				localPort: request.localPort,
				method: request.method,
				queryString: request.queryString,
				uri: request.requestURI,
				serverName: request.serverName,
				serverPort: request.serverPort,
				username: request.getAttribute("username")
			],
			response: [
				characterEncoding: response.characterEncoding,
				contentType: response.contentType,
				status: response.status
			],
			exception: [
				msg: ex?.message,
				stacktrace: ex?.stackTrace
			],
			timeSpent: totalTime
		]

		def msgJson = new JsonBuilder(msg).toString()

		logger.info(msgJson)
	}


	def combineRequestHeaders(HttpServletRequest request) {
		def headers = [:]

		request?.headerNames?.each { headerName ->
			//if (!(headerName in ['crowd-sso-token', 'crowd-sso-name'])) {
			headers[headerName] = request.getHeader(headerName)
			//}
		}

		headers
	}
}
