package com.github.sats17.saga.order.model.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 
 * Final response class, which generalized for error and success response.
 * 
 * Success Response Example :
 * {
 * 		"status": {
 * 			"rootCode": 20000,
 * 			"rootType": "success",
 * 			"service": "application_name"
 * 		},
 * 		"response": {
 * 			"responseMessage": "This is your customized response body"
 * 		}
 * }
 * 
 * Error Response Example :
 * {
 * 		"status": {
 * 			"rootCode": 40000,
 * 			"rootType": "RequestValidationException",
 * 			"service": "application_name"
 * 		}, 
 * 		"error": [{
 * 				"resultCode": 40001,
 * 				"resultType": "HeaderValidationException",
 * 				"message": "API-Key is missing",
 * 				"httpMethod": "GET",
 * 				"httpRequestURI": "/api/register"
 * 		}]
 * }
 * 
 * @author sats17
 *
 * @param <E>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FinalResponse<E> {

	private Status status;
	private E response;
	private List<Error> error;

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public E getResponse() {
		return response;
	}

	public void setResponse(E response) {
		this.response = response;
	}
	
	

	public List<Error> getError() {
		return error;
	}

	public void setError(List<Error> error) {
		this.error = error;
	}

	@Override
	public String toString() {
		return "FinalResponse [status=" + status + ", response=" + response + ", error=" + error + "]";
	}

	

}
