package com.logicalis.apisolver.model;

import com.logicalis.apisolver.model.servicenow.SnDomain;

import java.io.Serializable;
import java.util.List;

public class APIResponse implements Serializable {

	private Boolean error;
	private String message;
	private List<SnDomain> snDomains;

	public Boolean getError() {
		return error;
	}

	public void setError(Boolean error) {
		this.error = error;
	}


	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public List<SnDomain> getSnDomains() {
		return snDomains;
	}

	public void setSnDomains(List<SnDomain> snDomains) {
		this.snDomains = snDomains;
	}
}