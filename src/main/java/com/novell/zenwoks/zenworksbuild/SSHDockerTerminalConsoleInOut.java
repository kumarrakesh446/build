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
import java.util.Scanner;


public class SSHDockerTerminalConsoleInOut implements Terminal
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

    public SSHDockerTerminalConsoleInOut(PipedOutputStream commandWriter)
    {
        this.commandWriter = commandWriter;
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

            //commandWriter = new PipedOutputStream();

            final PipedInputStream input = new PipedInputStream(commandWriter);
            channel.setInputStream(input);
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

        }
        catch(Exception e)
        {
            System.out.println(e);
        }

    }

    @Override
    public void disconnect() throws TerminalException
    {
        try
        {
            channel.setOutputStream(null);
            channel.disconnect();
        }
        catch(Exception e)
        {

        }
        try
        {
            session.disconnect();
        }
        catch(Exception e)
        {

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
            if(!"KILL".equalsIgnoreCase(command))
            {
                commandWriter.write((command + " \n").getBytes());
                commandWriter.flush();
            }
            else
            {
                commandWriter.write(3);
            }

        }
        catch(Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

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
                avl = commandOutput.available();
            } while(avl > 0);

        }
        catch(IOException e)
        {

            e.printStackTrace();
        }

        return stringOutput += outString;
    }

    public static void main(String[] args) throws TerminalLoginException, TerminalExecutionException, TerminalException, IOException, InterruptedException
    {
        String userId = "";
        String pass = "";
        String url = "";
        String command = "";

        String containerName = "";
        if(args == null || args.length < 3)
        {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Login User:");
            userId = scanner.nextLine();


            System.out.println("Password:");
            pass = scanner.nextLine();

            System.out.println("Server Url:");
            url = scanner.nextLine();

            System.out.println("Container Name:");
            containerName = scanner.nextLine();
        }
        else
        {
            userId = args[0];
            pass = args[1];
            url = args[2];
            containerName = args[3];

        }


        //if(command!=null&&!command.isEmpty())
        {

            String containerId = getContainerId(userId, pass, url, containerName);
            PipedOutputStream testInput = new PipedOutputStream();
            SSHDockerTerminalConsoleInOut sshTerminal = new SSHDockerTerminalConsoleInOut(testInput);
            sshTerminal.login(userId, pass, url);

            command = "docker exec -it " + containerId.trim() + " /bin/bash;";

            sshTerminal.executeCommand(command);

            if(args.length > 4)
            {
                String extraCommand = "";
                for(int i = 4; i < args.length; i++)
                {
                    extraCommand += args[i];
                }
                sshTerminal.executeCommand(extraCommand);
            }
            while(true)
            {
                Scanner scanner = new Scanner(System.in);
                command = scanner.nextLine();
                if(command.equalsIgnoreCase("exit"))
                {
                    sshTerminal.executeCommand("KILL");
                    sshTerminal.disconnect();
                    System.exit(1);
                }
                else
                {
                    if(!command.endsWith(";"))
                    {
                        command += ";";
                    }
                    sshTerminal.executeCommand(command);
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

    private void run()
    {
        channel.run();
    }

    private static String getContainerId(String userId, String pass, String url, String containerName) throws TerminalLoginException, TerminalExecutionException, TerminalException
    {
        SSHTerminal sshTerminal = new SSHTerminal();
        sshTerminal.login(userId, pass, url);
        String containerId = sshTerminal.executeCommand("docker ps -a -q -f  name=" + containerName).trim();
        sshTerminal.disconnect();
        return containerId;
    }

    public void setInputStream(InputStream inputStream)
    {
        channel.setInputStream(inputStream);
    }
}
