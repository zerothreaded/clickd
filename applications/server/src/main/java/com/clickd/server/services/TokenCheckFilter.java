package com.clickd.server.services;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class TokenCheckFilter implements Filter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
//		String accessToken = request.getParameter("apikey");
//		if (accessToken != null) {
//			// System.out.println("TOKEN = " + accessToken);
//			filterChain.doFilter(request, response);
//		} else {
//			// System.out.println("TOKEN = " + "NOT PRESENT");
//			filterChain.doFilter(request, response);
//		}

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
