package com.novell.zenwoks.zenworksbuild;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.Scanner;

public class SSHDockerContainerTerminalConsole extends SSHTerminalConsole
{
    private String containerName;

    public SSHDockerContainerTerminalConsole(String containerName)
    {
        this(containerName, null,null);
    }

    public SSHDockerContainerTerminalConsole(String containerName, PipedOutputStream outputStream, PipedInputStream pipedInputStream)
    {
        super(outputStream,pipedInputStream);
        this.containerName = containerName;
    }

    @Override
    public void login(String userName, String password, String aditionalCriteria) throws TerminalLoginException
    {
        super.login(userName, password, aditionalCriteria);
        connectToContainer(containerName);
    }

    private void connectToContainer(String aditionalCriteria) throws TerminalLoginException
    {
        try
        {
            executeCommand("su tomcat");
        }
        catch(TerminalExecutionException e)
        {
            throw new TerminalLoginException("unable to switch user:tomcat");
        }

        try
        {
            String containerId = executeCommand("docker ps -q -a -f  name=" + aditionalCriteria);
            try
            {

                if(containerId != null && !containerId.isEmpty())
                {
                    executeCommand("docker exec -it " + containerId.trim() + " /bin/bash");
                }
                else
                {
                    throw new TerminalLoginException("unable to find container" + aditionalCriteria);
                }
            }
            catch(TerminalExecutionException e)
            {
                throw new TerminalLoginException("unable to find container" + aditionalCriteria);
            }
        }
        catch(TerminalExecutionException e)
        {
            throw new TerminalLoginException("unable to find container" + aditionalCriteria);
        }


    }

    @Override
    public String executeCommand(String command) throws TerminalExecutionException
    {
        String out = super.executeCommand(command);
        out = out.trim();
        if(out.indexOf("\n") >0&&out.indexOf("\n") < out.length())
        {
            out = out.substring(out.indexOf("\n"), out.lastIndexOf("\n"));
        }
        return out.trim();
    }

    private static SSHTerminalConsole sshTerminal;

    public static void main(String[] args) throws TerminalLoginException, TerminalExecutionException, TerminalException, IOException
    {
        String userId = "";
        String pass = "";
        String url = "";
        String command = "";
        String containerName="";
        if(args == null || args.length < 4)
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
            containerName=args[3];

        }

        for(int i = 3; i < args.length; i++)
        {
            command += args[i];
        }

        sshTerminal = new SSHDockerContainerTerminalConsole(containerName);
        sshTerminal.login(userId, pass, url);

        String out = sshTerminal.executeCommand("ls");
        sshTerminal.executeCommand("exit");
        System.out.println(out);
        sshTerminal.disconnect();

    }
}
