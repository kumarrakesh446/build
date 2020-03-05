package com.novell.zenwoks.command.commands;


import com.novell.zenwoks.command.process.ZenCommand;
import com.novell.zenwoks.command.process.ZenDockerBaseCommand;
import com.novell.zenwoks.zenworksbuild.*;

import java.util.ArrayList;
import java.util.UUID;

@ZenCommand
public class ZenDockerCopyFileToZenServer extends ZenDockerCopyFile
{
    @Override
    public String getCommandName()
    {
        return "copy-server";
    }

    @Override
    public ArrayList<String> argumentsNameList()
    {
        ArrayList<String> arrayList = super.argumentsNameList();
        arrayList.remove(3);
        return arrayList;
    }

    @Override
    public void executeCommand(ArrayList<String> argument)
    {
        argument.add(3,"zenserver");
        super.executeCommand(argument);
    }

    @Override
    public String usageString()
    {
        return  "Usages:User-Name    Password    Url ";
    }

    @Override
    public String helpString()
    {
        return "Copy files/folders between zenserver container and the local filesystem\n" +
                "         file-name to copy file\n" +
                "         Folder-name to copy folder hierarchy\n " +
                "         Folder-name\\. to copy folder contains\n " +
                "         example:zendocker copy root novell zenserver.epm.blr.novell.com  D:\\temp /etc/tmp\n";
    }

}
