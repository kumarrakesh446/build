package com.novell.zenwoks.zenworksbuild;

import java.util.Scanner;

public class SSHDockerContainerTerminal extends SSHTerminal
{

    private String containerId="";
    private String containerName;

    public SSHDockerContainerTerminal(String containerName)
    {
        super();
        this.containerName=containerName;

    }

    @Override
    public void login(String userName, String password, String aditionalCriteria) throws TerminalLoginException
    {
        super.login(userName, password, aditionalCriteria);
        getContainerId();
    }

    private void getContainerId()
    {
        try
        {
            containerId = executeCommand("docker ps -q -a -f  name=" + containerName.trim()).trim();
        }
        catch(TerminalExecutionException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public String executeCommand(String command) throws TerminalExecutionException
    {
        command=command.replace("CONTAINER_ID",containerId);
        return super.executeCommand(command);
    }

    private static SSHTerminal sshTerminal;

    public static void main(String[] args) throws TerminalLoginException, TerminalExecutionException, TerminalException
    {
        String userId = "";
        String pass = "";
        String url = "";
        String command = "";

        if(args == null || args.length == 0)
        {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Login User:");
            userId = scanner.nextLine();


            System.out.println("Password:");
            pass = scanner.nextLine();

            System.out.println("Server Url:");
            url = scanner.nextLine();

        }
        else
        {
            userId = args[0];
            pass = args[1];
            url = args[2];

        }

        for(int i = 4; i < args.length; i++)
        {
            command += args[i];
        }

        sshTerminal = new SSHDockerContainerTerminal("zenloader");
        sshTerminal.login(userId, pass, url);

        String out = sshTerminal.executeCommand("docker restart CONTAINER_ID");
        System.out.println(out);
        sshTerminal.disconnect();

    }


}
