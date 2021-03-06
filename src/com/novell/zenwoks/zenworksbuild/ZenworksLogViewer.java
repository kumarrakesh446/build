package com.novell.zenwoks.zenworksbuild;

import java.util.Scanner;

public class ZenworksLogViewer {

	public static void main(String[] args) throws TerminalLoginException, TerminalExecutionException, TerminalException {
		
		

		String userId = "";
		String pass = "";
		String url = "";
		String command = "";
		
		if(args==null||args.length<3)
		{
			Scanner scanner=new Scanner(System.in);
			System.out.println("Login User:");
			userId=scanner.nextLine();
			
			
			System.out.println("Password:");
			pass=scanner.nextLine();
			
			System.out.println("Server Url:");
			url=scanner.nextLine();
			if(args!=null&&args.length==1)
			{
				command=args[0];
			}else{
				
				System.out.println("Log file(zcc.log=z,services-messages.log=s,loader-messages.log=l,exit=e)?:");
				command=scanner.nextLine();
				
			}
			
		}
		else
		{
			userId=args[0];
			pass=args[1];
			url=args[2];
			for (int i = 3; i < args.length; i++) {
				command += args[i]+" ";
			}
			
			
			
		}
		if(command.toLowerCase().startsWith("z"))
		{
			command="zcc.log";
		}else if(command.toLowerCase().startsWith("s")){
			command="services-messages.log";
			
		}else if(command.toLowerCase().startsWith("l")){
			command="loader-messages.log";
		}else{
			System.out.println("Wrong command...");
			System.exit(0);
		}
		command="tail -f /var/opt/novell/log/zenworks/"+command;	
		SSHTerminalConsoleMain.main(new String[]{userId,pass,url,command});
		
	

	}

}
