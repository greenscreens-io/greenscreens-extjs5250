/*
 * Copyright (C) 2015, 2016  GreenScreens Ltd.
 */
package io.greenscreens.web;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;

/**
 * Http request filter to set UTF
 */
@WebFilter("/*")
public final class CharsetUnicodeWebFilter implements Filter {

	@Override
	public void destroy() {}

	@Override
	public void init(FilterConfig arg0) throws ServletException {}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		   request.setCharacterEncoding("UTF-8");
		   //response.setContentType("text/html; charset=UTF-8");
		   response.setCharacterEncoding("UTF-8");
		   chain.doFilter(request, response);
	}

}
