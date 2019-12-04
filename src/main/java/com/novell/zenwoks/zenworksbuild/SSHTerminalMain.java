package com.novell.zenwoks.zenworksbuild;

import java.util.Scanner;

public class SSHTerminalMain {

	private static SSHTerminal sshTerminal;

	public static void main(String[] args) throws TerminalLoginException, TerminalExecutionException, TerminalException {
		String userId = "";
		String pass = "";
		String url = "";
		String command = "";
		
		if(args==null||args.length==0)
		{
			Scanner scanner=new Scanner(System.in);
			System.out.println("Login User:");
			userId=scanner.nextLine();
			
			
			System.out.println("Password:");
			pass=scanner.nextLine();
			
			System.out.println("Server Url:");
			url=scanner.nextLine();
			
		}
		else
		{
			userId=args[0];
			pass=args[1];
			url=args[2];
			
		}
		
		for (int i = 3; i < args.length; i++) {
			command += " "+args[i];
		}
		
		sshTerminal = new SSHTerminal();
		sshTerminal.login(userId, pass, url);
		
		String out = sshTerminal.executeCommand(command);
		System.out.println(out);
		sshTerminal.disconnect();

	}

}
