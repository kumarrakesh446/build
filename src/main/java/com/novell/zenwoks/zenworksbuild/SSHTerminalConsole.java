package com.novell.zenwoks.zenworksbuild;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.novell.zenwoks.zenworksbuild.Shell.MyUserInfo;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;


public class SSHTerminalConsole implements Terminal
{
    private boolean isConnected = false;
    private JSch jsch;
    private String user;
    private String host;
    private String passwd;
    private Channel channel;
    private PipedOutputStream commandWriter;
    private PipedInputStream commandOutput;
    private Session session;
    private String stringOutput;


    public SSHTerminalConsole(PipedOutputStream outputStream, PipedInputStream commandOutput)
    {
        commandWriter = outputStream;
        this.commandOutput = commandOutput;
    }

    public SSHTerminalConsole()
    {

    }

    @Override
    public void login(String userName, String password, String aditionalCriteria) throws TerminalLoginException
    {

        isConnected = true;
        user = userName;
        host = aditionalCriteria;
        passwd = password;
        doLogin();
    }

    private void doLogin()
    {


        try
        {
            jsch = new JSch();

            //jsch.setKnownHosts("/home/foo/.ssh/known_hosts");


            session = jsch.getSession(user, host, 22);


            session.setPassword(passwd);

            MyUserInfo ui = new MyUserInfo()
            {
                public void showMessage(String message)
                {

                }

                public boolean promptYesNo(String message)
                {

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

            channel = session.openChannel("shell");
            ((ChannelShell)channel).setPtyType("dumb");
            ((ChannelShell)channel).setPty(true);


            if(commandWriter != null)
            {
                final PipedInputStream input = new PipedInputStream(commandWriter);
                channel.setInputStream(input);
            }
            else
            {
                channel.setInputStream(System.in);
            }
	       /*if(commandWriter==null)
	       {
	    	   
	    	   channel.setInputStream(System.in);
	       }else{
	    	   final PipedInputStream  input  = new PipedInputStream(commandWriter);
	    	   channel.setInputStream(input);
	       }*/


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

            if(commandOutput == null)
            {
                channel.setOutputStream(System.out);
            }
            else
            {
                PipedOutputStream out = new PipedOutputStream(commandOutput);
                channel.setOutputStream(out);
            }


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
            /*new Thread(new Runnable()
            {

                @Override
                public void run()
                {
                    while(channel.getExitStatus() == -1)
                    {
                        try
                        {
                            Thread.sleep(1000);
                        }
                        catch(Exception e)
                        {
                            System.out.println(e);
                        }
                    }
                    try
                    {
                        disconnect();
                    }
                    catch(TerminalException e)
                    {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }
            }).start();*/

        }
        catch(Exception e)
        {
            System.out.println(e);
        }

    }

    @Override
    public void disconnect() throws TerminalException
    {
        channel.disconnect();
        session.disconnect();

        try
        {
            if(commandOutput!=null)
            commandOutput.close();
            if(commandWriter!=null)
            commandWriter.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        isConnected = false;

    }

    @Override
    public boolean isConnected() throws TerminalException
    {
        return isConnected;
    }

    @Override
    public String executeCommand(String command) throws TerminalExecutionException
    {


        String outString = "";
        try
        {

            if(commandWriter != null)
            {
                commandWriter.write((command + ";\n").getBytes());
                commandWriter.flush();
                if(commandOutput!=null)
                outString = refresh();
            }


        }
        catch(Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //System.out.println(outString);
        return stringOutput = outString;
    }

    @Override
    public String refresh() throws TerminalExecutionException
    {


        String outString = "";
        try
        {

            int avl = 0;
            do
            {
                try
                {
                    Thread.sleep(2000);
                }
                catch(InterruptedException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                avl = commandOutput.available();
                if(avl > 0)
                {

                    byte[] b = new byte[avl];

                    commandOutput.read(b);
                    outString = outString + new String(b);
                }
                //avl = commandOutput.available();
            } while(avl > 0);

        }
        catch(IOException e)
        {

            e.printStackTrace();
        }

        return stringOutput = outString;
    }

    public void setInput(InputStream in)
    {
        channel.setInputStream(in);
    }


}
