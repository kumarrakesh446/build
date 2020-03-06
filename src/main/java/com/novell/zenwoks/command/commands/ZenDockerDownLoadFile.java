package com.novell.zenwoks.command.commands;


import com.novell.zenwoks.command.process.ZenCommand;
import com.novell.zenwoks.command.process.ZenDockerBaseCommand;
import com.novell.zenwoks.zenworksbuild.*;

import java.io.File;
import java.io.PipedOutputStream;
import java.util.ArrayList;
import java.util.UUID;

@ZenCommand
public class ZenDockerDownLoadFile extends ZenDockerBaseCommand
{
    @Override
    public String getCommandName()
    {
        return "download";
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
        SSHTerminal sshTerminal=null;
        try
        {
            //copying to remote location
            String containerName = argument.get(3);
            String tempFolder = "/tmp/"+UUID.randomUUID().toString();

            //copying to docker
            sshTerminal=new SSHTerminal();
            sshTerminal.login(argument.get(0), argument.get(1), argument.get(2));

            sshTerminal.executeCommand("mkdir  "+tempFolder);


            System.out.println("Copying from  docker container");
            File file=new File(argument.get(5));

            sshTerminal.executeCommand("docker cp  "+" "+argument.get(3)+":"+argument.get(4)+" "+tempFolder+"/"+argument.get(5));
            //chown zenworks:zenworks


            SSHDownloadFileTerminal.main(new String[] {argument.get(0), argument.get(1), argument.get(2), argument.get(5),tempFolder});

            System.out.println("removing from temp");
            sshTerminal.executeCommand("rm -rf "+tempFolder);

            sshTerminal.disconnect();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }finally
        {
            if(sshTerminal!=null)
            {
                try
                {
                    sshTerminal.disconnect();
                }
                catch(TerminalException e)
                {

                }
            }
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
        return "Download files from running container.";
    }

}
