package com.novell.zenwoks.zenworksbuild;

public interface Terminal {

	void login(String userName,String password,String aditionalCriteria) throws TerminalLoginException;
	
	void disconnect()throws TerminalException;
	boolean isConnected()throws TerminalException;
	
	String executeCommand(String command)throws TerminalExecutionException;
	
	String refresh()throws TerminalExecutionException;



}
