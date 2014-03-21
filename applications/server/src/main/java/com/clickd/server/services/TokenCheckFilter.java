package com.clickd.server.services;

import java.io.File;
import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tools.ant.util.FileUtils;

public class TokenCheckFilter implements Filter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
		// String accessToken = request.getParameter("apikey");
		// if (accessToken != null) {
		// // System.out.println("TOKEN = " + accessToken);
		// filterChain.doFilter(request, response);
		// } else {
		// // System.out.println("TOKEN = " + "NOT PRESENT");
		// filterChain.doFilter(request, response);
		// }

		// Add whatever headers you want here
		HttpServletResponse servletResponse = (HttpServletResponse) response;
		// servletResponse.setHeader("Cache-Control", "public, max-age=1");
		// servletResponse.setHeader("Expires", new Date().getTime() + 0 + "");
		// servletResponse.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP
		// servletResponse.setHeader("Cache-Control", "cache"); // HTTP// 1.1.

		
		HttpServletRequest servletRequest = (HttpServletRequest) request;
		String path = servletRequest.getRequestURI();
		if (path != null) {
			if (!path.contains("img")) {
				servletResponse.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP
				servletResponse.setHeader("Cache-Control", "cache"); // HTTP// 1.1.
				servletResponse.setHeader("Pragma", "cache"); // HTTP 1.0.
			} else {
				if(path.contains("profile-img")) {
					int pathWait = 1;
					 String dataDir = System.getProperty("dataDir");
					 if (null == dataDir) {
						 dataDir = "C:\\sandbox\\data\\profile-img\\";
						 //System.out.println("\n\nData Directory = " + dataDir);
					 } else {
						 //System.out.println("\n\nData Directory = " + dataDir);
					 }
					String targetFileName = path.substring(12);
					// Image files - serve these from disk
					byte[] fileContents;
					File imageFile = new File(dataDir, targetFileName);
					System.out.println(imageFile.getAbsolutePath());
					fileContents = org.apache.commons.io.FileUtils.readFileToByteArray(imageFile);
					
					servletResponse.getOutputStream().write(fileContents);
					servletResponse.flushBuffer();
					return;
				} else {
					// All others
				}
				int wait  = 1;
				
			}
		}		
		servletResponse.setHeader("Access-Control-Allow-Origin", "*");
		
		filterChain.doFilter(request, response);
	}

	@Override
	public void destroy() {
		System.out.println("==========================================================");
		System.out.println("destroy() called on Filter");
		System.out.println("==========================================================");
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		System.out.println("==========================================================");
		System.out.println("init() called on Filter");
		System.out.println("==========================================================");
	}

}
