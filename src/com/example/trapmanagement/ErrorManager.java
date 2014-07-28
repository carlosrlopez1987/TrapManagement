package com.example.trapmanagement;

import java.util.List;

public class ErrorManager {
  private String lastErrorMessage;
  private int lastErrorCode;
  
  public ErrorManager(){
	lastErrorMessage = "No Meesages";
	lastErrorCode = 0;
  }
  
  public void addErrorMessage( int code, String msg ){
	if ( code > 0 )
	  this.lastErrorCode = code;
	
	if ( msg != null )
		this.lastErrorMessage = msg;
  }
  
  public int getLastErrorCode(){
	return lastErrorCode;
  }
  
  public String getLastErrorMessage(){
	return lastErrorMessage;
  }
}
