package com.novell.zenwoks.zenworksbuild;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Scanner;

import javax.swing.JOptionPane;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpProgressMonitor;
import com.jcraft.jsch.UserInfo;
import com.novell.zenwoks.zenworksbuild.Shell.MyUserInfo;


public class SSHCopyFileTerminal implements Terminal {
	private boolean isConnected=false;
	private JSch jsch;
	private String user;
	private String host;
	private String passwd;
	private Channel channel;
	private PipedOutputStream commandWriter;
	private PipedInputStream commandOutput;
	private Session session;
	private String stringOutput;
	
	
	
	@Override
	public void login(String userName, String password, String aditionalCriteria) throws TerminalLoginException {
		
		isConnected=true;
		user=userName;
		host=aditionalCriteria;
		passwd=password;
		doLogin();
	}

	private void doLogin() {


	    
	    try{
	       jsch=new JSch();

	      //jsch.setKnownHosts("/home/foo/.ssh/known_hosts");

	   
	     
	       session=jsch.getSession(user, host, 22);

	    
	      session.setPassword(passwd);

	      MyUserInfo ui = new MyUserInfo(){
	        public void showMessage(String message){
	         
	        }
	        public boolean promptYesNo(String message){
	          
	          return true;
	        }

	        // If password is not given before the invocation of Session#connect(),
	        // implement also following methods,
	        //   * UserInfo#getPassword(),
	        //   * UserInfo#promptPassword(String message) and
	        //   * UIKeyboardInteractive#promptKeyboardInteractive()

	      };

	      session.setUserInfo(ui);

	      // It must not be recommended, but if you want to skip host-key check,
	      // invoke following,
	      // session.setConfig("StrictHostKeyChecking", "no");

	      //session.connect();
	      session.connect(30000);   // making a connection with timeout.

	      
	    }
	    catch(Exception e){
	      System.out.println(e);
	    }
	  
	}
	@Override
	public void disconnect() throws TerminalException {
		session.disconnect();
		isConnected=false;

	}

	@Override
	public boolean isConnected() throws TerminalException {
		return isConnected;
	}

	@Override
	public String executeCommand(String command) throws TerminalExecutionException {
		
		
		System.out.println(command);
		 String outString="";
		try {
			
			if(command==null||command.isEmpty()||!command.contains("src=")||!command.contains("out="))
				return "Invalid command- usage src=scourcepath  out=outpath";
			
			command=command.trim();
			String scr = command.substring(command.indexOf("=")+1, command.indexOf("out="));
			File file=new File(scr.trim());
			
			
			String out = command.substring(command.lastIndexOf("=")+1);
			out=out.trim();
			System.out.println();
			FileInputStream fileInputStream=new FileInputStream(file);
			//String jarName = file.getName();
			//getJarLocation();/root/Desktop/filename.txt
			
			ChannelSftp channelWrite = (ChannelSftp)session.openChannel("sftp");
			channelWrite.connect();
		      channelWrite.put(fileInputStream, out,ChannelSftp.OVERWRITE);
		      
		     
		      channelWrite.disconnect();
		      System.out.println("Copying done....");
		     return "Done!!!" ;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return stringOutput=outString;
	}

	@Override
	public String refresh() throws TerminalExecutionException {

		
		
		 
		 String outString="";
		try {
			 
			
			int avl=0; 
			do{
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			 avl=commandOutput.available();
			if(avl>0){
				
				byte[] b=new byte[avl];
				
				commandOutput.read(b);
				outString=outString+new String(b);
			}
			avl=commandOutput.available();
			}while(avl>0);
			
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
		return stringOutput+=outString;
	}

public static void main(String[] args) throws TerminalExecutionException, TerminalLoginException, TerminalException {
	
	if(args==null||args.length<5)
	{
		System.out.println("Invalid Usage:");
		System.out.println("Use: userName password server ");
	}
	
	String userId=args[0];
	String pass=args[1];
	String url=args[2];
	
	String command="";
	for (int i = 3; i < args.length; i++) {
		command+=args[i];
	}
	
	SSHCopyFileTerminal copyFileTerminal=new SSHCopyFileTerminal();
	copyFileTerminal.login(userId, pass, url);
	copyFileTerminal.executeCommand(command);
	copyFileTerminal.disconnect();
}
}
