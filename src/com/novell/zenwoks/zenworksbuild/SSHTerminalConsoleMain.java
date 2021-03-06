package com.novell.zenwoks.zenworksbuild;

import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.Scanner;

public class SSHTerminalConsoleMain {

	private static SSHTerminalConsole sshTerminal;

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
		if(args!=null&&args.length==1)
		{
			command=args[0];
		}
		
		
		
		//if(command!=null&&!command.isEmpty())
		{
			PipedOutputStream commandWriter=new PipedOutputStream();
			sshTerminal = new SSHTerminalConsole(commandWriter);			
			sshTerminal.login(userId, pass, url);
			
			if(command!=null&&!command.isEmpty()&&!command.endsWith(";"))
			{
				command+=";\n";
				sshTerminal.executeCommand(command);
			}
			
			
			 
			
			while(true)
			{
				Scanner scanner=new Scanner(System.in);
				String s=scanner.nextLine();
				if(s.equalsIgnoreCase("exit"))
				{
					sshTerminal.disconnect();
					System.exit(1);;
				}else
				{
					if(!s.endsWith(";"))
					{
						s+=";\n";
					}
					s+="\n";
					sshTerminal.executeCommand(s);
				}
			}
			//
		}
		/*else
		{
			sshTerminal = new SSHTerminalConsole();
			sshTerminal.login(userId, pass, url);
		}*/

	}

}
