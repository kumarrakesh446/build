package com.novell.zenwoks.command.commands;


import com.novell.zenwoks.command.process.ZenCommand;
import com.novell.zenwoks.command.process.ZenDockerBaseCommand;
import com.novell.zenwoks.zenworksbuild.*;

import java.io.File;
import java.io.PipedOutputStream;
import java.util.ArrayList;
import java.util.UUID;

@ZenCommand
public class ZenDockerCopyFile extends ZenDockerBaseCommand
{
    @Override
    public String getCommandName()
    {
        return "copy";
    }

    @Override
    public ArrayList<String> argumentsNameList()
    {
        ArrayList<String> arrayList = super.argumentsNameList();

        arrayList.add("Source");
        arrayList.add("Destination");
        return arrayList;
    }

    @Override
    public void executeCommand(ArrayList<String> argument)
    {
        try
        {
            //copying to remote location
            String containerName = argument.get(3);
            String tempFolder = "/tmp/"+UUID.randomUUID().toString();

            SSHCopyFileTerminal.main(new String[] {argument.get(0), argument.get(1), argument.get(2), argument.get(4),tempFolder});

            try
            {
                //create directory in container
                System.out.println("Creating directory in container");
                PipedOutputStream testInput = new PipedOutputStream();
                SSHDockerTerminalConsoleInOut sshDockerTerminalConsoleInOut = new SSHDockerTerminalConsoleInOut( testInput );
                sshDockerTerminalConsoleInOut.login(argument.get(0), argument.get(1), argument.get(2));

                String command="docker exec -it " + argument.get(3) + " /bin/bash;";

                sshDockerTerminalConsoleInOut.executeCommand(command);
                sshDockerTerminalConsoleInOut.executeCommand("mkdir -p "+argument.get(5));
                sshDockerTerminalConsoleInOut.disconnect();
            }catch(Exception e)
            {

            }
            //copying to docker
            SSHTerminal sshTerminal=new SSHTerminal();
            sshTerminal.login(argument.get(0), argument.get(1), argument.get(2));

            /*sshTerminal.executeCommand("chmod -R 755 "+tempFolder);
            sshTerminal.executeCommand("chown -R zenworks:zenworks "+tempFolder);
*/
            System.out.println("Copying to docker container");
            File file=new File(argument.get(5));

            sshTerminal.executeCommand("docker cp  "+tempFolder+"/."+" "+argument.get(3)+":"+argument.get(5));
            //chown zenworks:zenworks

            System.out.println("removing from temp");
            sshTerminal.executeCommand("rm -rf "+tempFolder);

            sshTerminal.disconnect();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public String usageString()
    {
        return super.usageString() + " source destination";
    }

    @Override
    public String helpString()
    {
        return "Copy files/folders between a container and the local filesystem\n" +
                "         file-name to copy file\n" +
                "         Folder-name to copy folder hierarchy\n " +
                "         Folder-name\\. to copy folder contains\n " +
                "         example:zendocker copy root novell zenserver.epm.blr.novell.com zenserver D:\\temp /etc/tmp\n";
    }

}
