package com.dknv.example.interceptors;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.core.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.WebUtils;

@Component
public class PayloadLoggingInterceptor extends HandlerInterceptorAdapter {

	
	private static final String KUBE_NODE_ID_LOG_VAR_NAME = "kubeNodeId";
	
	private static final String CORRELATION_ID_HEADER_NAME = "Fulk-CorrelationId";
	private static final String CORRELATION_ID_LOG_VAR_NAME = "correlationId";

	private static final String WORKFLOW_ID_HEADER_NAME = "Fulk-WorkflowId";
	private static final String WORKFLOW_ID_LOG_VAR_NAME = "workflowId";

	private static final String LOGGED_USER_ID_LOG_VAR_NAME = "loggedUserId";
	
	private static final String UNKNOWN = "[unknown]";

	private static final Logger LOGGER = LoggerFactory.getLogger(PayloadLoggingInterceptor.class);

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		HttpServletRequest requestCacheWrapperObject = new ContentCachingRequestWrapper(request);
		logRequestPayload("Before request",requestCacheWrapperObject);
		return true;
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		super.afterCompletion(request, response, handler, ex);
		HttpServletRequest requestCacheWrapperObject = new ContentCachingRequestWrapper(request);
		logRequestPayload("After request", requestCacheWrapperObject);
	}

	// "%{logdate} %{+logdate} %{loglevel} %{logger.name} [%{kube.node.id}]
	// [%{component.id}] [%{user.id}] [%{workflow.id}]
	// [%{correlation.id}][%{thread.id}] %{msg}
	private void logRequestPayload(String logprefix, HttpServletRequest request) {

		MDC.put(KUBE_NODE_ID_LOG_VAR_NAME, "?");
		
		String correlationId = request.getHeader(CORRELATION_ID_HEADER_NAME) != null ? request.getHeader(CORRELATION_ID_HEADER_NAME) : UNKNOWN;
		MDC.put(CORRELATION_ID_LOG_VAR_NAME, correlationId);

		String workflowId = request.getHeader(WORKFLOW_ID_HEADER_NAME) != null ? request.getHeader(WORKFLOW_ID_HEADER_NAME) : UNKNOWN;
		MDC.put(WORKFLOW_ID_LOG_VAR_NAME, workflowId);

		Principal principal = request.getUserPrincipal();
		String loggedInUser = principal != null ? principal.getName() : UNKNOWN;
		MDC.put(LOGGED_USER_ID_LOG_VAR_NAME, loggedInUser);

		StringBuilder log = new StringBuilder("Payload :: " + logprefix + " [[ ");

		log.append("logdate: ").append(getTimeStamp()).append(" | ");
		log.append("kube.node.id: ").append("?").append(" | ");
		log.append("component.id: ").append(request.getRequestURI()).append(" | ");
		log.append("user.id: ").append(loggedInUser).append(" | ");
		log.append("workflow.id: ").append(workflowId).append(" | ");
		log.append("correlation.id: ").append(correlationId).append(" | ");
		
		log.append(" || ");
		
		log.append("method: ").append(request.getMethod()).append(" | ");
		log.append("path: ").append(request.getRequestURI()).append(" | ");
		log.append("query: ").append(request.getQueryString()).append(" | ");
		log.append("headers: ").append(new ServletServerHttpRequest(request).getHeaders()).append(" | ");
		log.append("payload: ").append(getMessagePayload(request));
		
		log.append("Request ATTRIBUTES: ");
		Enumeration<?> attributes = request.getAttributeNames();
		while (attributes.hasMoreElements())	{
			String attr = (String) attributes.nextElement();
			log.append("{").append(attr).append(":").append(request.getAttribute(attr)).append("}");
		}		    
		
		log.append("Request PARAMETERS: ");
		Enumeration<?> parameters = request.getParameterNames();
		while(parameters.hasMoreElements()) {
			String param = (String) parameters.nextElement();			
			log.append("{").append(param).append(":").append(request.getParameter(param)).append("}");
		}
		log.append("]]");
		
		LOGGER.info(">>>>>>>>>>>>>>>>>");
		LOGGER.info(log.toString());
		LOGGER.info("<<<<<<<<<<<<<<<<<");
	}

	private String getTimeStamp() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd '-' HH:mm:ss");
		return dateFormat.format(new Date());
	}

	protected String getMessagePayload(HttpServletRequest request) {
		try {
			return IOUtils.toString(request.getReader());
		} catch (IOException e) {
			return "payload logging error: " + e.getMessage();
		}
	}
}
