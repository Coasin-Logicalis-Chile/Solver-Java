package com.logicalis.apisolver.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity(name="api_execution_status")
public class APIExecutionStatus implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	private String uri;
	@Column(name = "user_API")
	private String userAPI;
	@Column(name = "password_API")
	private String passwordAPI;
	private Boolean error;
	private String message;
	private Date date;
	private Long executionTime;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getUserAPI() {
		return userAPI;
	}

	public void setUserAPI(String user) {
		this.userAPI = user;
	}

	public String getPasswordAPI() {
		return passwordAPI;
	}

	public void setPasswordAPI(String password) {
		this.passwordAPI = password;
	}

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

	public void setDate(Date date) {
		this.date = date;
	}

	public Date getDate() {
		return date;
	}

	@PrePersist
	public void prePersist() {
		date = new Date();
	}

	public Long getExecutionTime() {
		return executionTime;
	}
	
	public void setExecutionTime(Long executionTime) {
		this.executionTime = executionTime;
	}

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

}