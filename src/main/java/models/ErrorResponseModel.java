package models;

import javax.ws.rs.core.Response.Status;

import helps.HttpResponseStatusEnums;

public class ErrorResponseModel {
	public Object ErrorCode;
	public String ErrorMsg;
	
	public ErrorResponseModel(HttpResponseStatusEnums ErrorCode, String ErrorMsg) {
		this.ErrorCode = ErrorCode;
		this.ErrorMsg = ErrorMsg;
	}
	
	public ErrorResponseModel(Status ErrorCode, String ErrorMsg) {
		this.ErrorCode = ErrorCode;
		this.ErrorMsg = ErrorMsg;
	}
}