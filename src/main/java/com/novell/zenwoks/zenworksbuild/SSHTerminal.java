package com.novell.zenwoks.zenworksbuild;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Scanner;

import javax.swing.JOptionPane;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;
import com.novell.zenwoks.zenworksbuild.Shell.MyUserInfo;


public class SSHTerminal implements Terminal {
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
	private BufferedReader consoleOutput;
	
	
	
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

	       
	      
	       /* commandWriter = new PipedOutputStream();
	        final PipedInputStream  input  = new PipedInputStream(commandWriter);
	       channel.setInputStream(input);*/
	      // Enable agent-forwarding.
	      //((ChannelShell)channel).setAgentForwarding(true);

	   
	    
	     
	      /*
	      // a hack for MS-DOS prompt on Windows.
	      channel.setInputStream(new FilterInputStream(System.in){
	          public int read(byte[] b, int off, int len)throws IOException{
	            return in.read(b, off, (len>1024?1024:len));
	          }
	        });
	       */

	      
	       /*  commandOutput  = new PipedInputStream();
	         
	         consoleOutput = new BufferedReader(new InputStreamReader(commandOutput));
	        
	        PipedOutputStream out = new PipedOutputStream(commandOutput);
	      
	   channel.setOutputStream(out);
*/
	      /*
	      // Choose the pty-type "vt102".
	      ((ChannelShell)channel).setPtyType("vt102");
	      */

	      /*
	      // Set environment variable "LANG" as "ja_JP.eucJP".
	      ((ChannelShell)channel).setEnv("LANG", "ja_JP.eucJP");
	      */

	      //channel.connect();
	     // channel.connect(3*1000);
	    }
	    catch(Exception e){
	    	e.printStackTrace();
	      System.out.println(e);
	    }
	  
	}
	  public String sendCommand(String command)
	  {
	     StringBuilder outputBuffer = new StringBuilder();

	     try
	     {
	    	 channel=session.openChannel("exec");
	        ((ChannelExec)channel).setCommand(command);
	        InputStream commandOutput = channel.getInputStream();
	       channel.connect(3*1000);
	        int readByte = commandOutput.read();

	        while(readByte != 0xffffffff)
	        {
	           outputBuffer.append((char)readByte);
	           readByte = commandOutput.read();
	        }

	        channel.disconnect();
	     }
	     catch(IOException ioX)
	     {
	        ioX.printStackTrace();
	        return null;
	     }
	     catch(JSchException jschX)
	     {
	    	 jschX.printStackTrace();
	        return null;
	     }

	     //System.out.println("Command out put:"+outputBuffer);
	     return outputBuffer.toString();
	  }

	@Override
	public void disconnect() throws TerminalException {
		channel.disconnect();
		session.disconnect();
		isConnected=false;

	}

	@Override
	public boolean isConnected() throws TerminalException {
		return isConnected;
	}

	@Override
	public String executeCommand(String command) throws TerminalExecutionException {
		
		
		System.out.println("Execute Command:"+command);
		 String outString="";
		/* try {
			 System.out.println("Available:"+commandOutput.available());
			commandOutput.skip(commandOutput.available());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}*/
		/*try {
			if(!"KILL".equalsIgnoreCase(command)) {
			commandWriter.write((command+" \n").getBytes());
			commandWriter.flush();
			}else {
				commandWriter.write(3
			}
			
			outString=readOutPut();
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		*/
		return stringOutput=sendCommand(command);
	}
	
	private String readLines() throws IOException
	{
		StringBuilder out=new StringBuilder();
		boolean end=false;
		while(consoleOutput.ready())
		{
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		   
		    
		    out.append( consoleOutput.readLine()+"\n");
		     end = false;
		   
		}
		return out.toString();
		
	}
	private String readOutPut() throws IOException {
		int avl = 0;
		String outString = "";
		do {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			avl = commandOutput.available();
			if (avl > 0) {

				byte[] b = new byte[avl];

				commandOutput.read(b);
				outString = outString + new String(b);
			}
			avl = commandOutput.available();
		} while (avl > 0);

		return outString;

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


}
