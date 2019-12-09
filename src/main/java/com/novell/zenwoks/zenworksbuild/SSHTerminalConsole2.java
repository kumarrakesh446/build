package com.novell.zenwoks.zenworksbuild;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.novell.zenwoks.zenworksbuild.Shell.MyUserInfo;


public class SSHTerminalConsole2 implements Terminal {
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

	       channel=session.openChannel("shell");
	       ((ChannelShell) channel).setPtyType("dumb");
	       ((ChannelShell) channel).setPty(true);
	       
	        //commandWriter = new PipedOutputStream();
	        
	       
	        //final PipedInputStream  input  = new PipedInputStream(commandWriter);
	       channel.setInputStream(System.in);
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

	      
	         /*commandOutput  = new PipedInputStream();
	        PipedOutputStream out = new PipedOutputStream(commandOutput);*/
	      
	   channel.setOutputStream(System.out);

	      /*
	      // Choose the pty-type "vt102".
	      ((ChannelShell)channel).setPtyType("vt102");
	      */

	      /*
	      // Set environment variable "LANG" as "ja_JP.eucJP".
	      ((ChannelShell)channel).setEnv("LANG", "ja_JP.eucJP");
	      */

	      //channel.connect();
	      channel.connect(3*1000);
	      
	      while (channel.getExitStatus() == -1){
	            try{Thread.sleep(1000);}catch(Exception e){System.out.println(e);}
	         }
	      disconnect();
	    }
	    catch(Exception e){
	      System.out.println(e);
	    }
	  
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
		
		
		
		 String outString="";
		try {
			if(!"KILL".equalsIgnoreCase(command)) {
			commandWriter.write((command+" \n").getBytes());
			commandWriter.flush();
			}else {
				commandWriter.write(3);
			}
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


}
