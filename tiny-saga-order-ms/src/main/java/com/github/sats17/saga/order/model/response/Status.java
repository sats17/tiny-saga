package com.github.sats17.saga.order.model.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Status class contains information about your payload
 * 
 * @author sats17
 *
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Status {

	private Integer rootCode;
	private String rootType;
	private String service;

	public Integer getRootCode() {
		return rootCode;
	}

	public void setRootCode(Integer rootCode) {
		this.rootCode = rootCode;
	}

	public String getRootType() {
		return rootType;
	}

	public void setRootType(String string) {
		this.rootType = string;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	@Override
	public String toString() {
		return "Status [rootCode=" + rootCode + ", rootType=" + rootType + ", service=" + service + "]";
	}

}
