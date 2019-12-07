package com.olbius.jcr.servlet;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class OlbiusWebdavFilter implements Filter {

	@Override
	public void destroy() {

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		String uri = httpRequest.getPathInfo();
//		uri = uri.replaceAll("@tenant", JackrabbitContainer.getTenant());
		RequestWrapper modifiedRequest = null;
		if(uri.indexOf("/@tenant")>=0) {
			modifiedRequest = new RequestWrapper(httpRequest);
//			modifiedRequest.changeDestinationAgent("/@tenant", "/"+JackrabbitOlbiusContainer.getTenant().getTenantID());
			chain.doFilter(modifiedRequest, response);
		} else {
			chain.doFilter(request, response);
		}
//		RequestDispatcher dispatcher = httpRequest.getRequestDispatcher(uri);
//		dispatcher.forward(httpRequest, response);
		
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

	}

	class RequestWrapper extends HttpServletRequestWrapper {
		
		private String originalDestination, newDestinationAgent;
		
		public RequestWrapper(HttpServletRequest request) {
			super(request);
		}

		@Override
		public String getRequestURI() {
			String originalURI = super.getRequestURI();
			
			StringBuffer newURI = new StringBuffer();
			
			newURI.append(originalURI.substring(0, originalURI.indexOf(originalDestination)));
			newURI.append(newDestinationAgent);
			newURI.append(originalURI.substring(originalURI.indexOf(originalDestination) + originalDestination.length(), 
												originalURI.length()));
			
			return newURI.toString();
		}
		
		protected void changeDestinationAgent(String originalDestination, String newDestination) {
			this.originalDestination = originalDestination;
			this.newDestinationAgent = newDestination;
		}
		
	}
	
}
