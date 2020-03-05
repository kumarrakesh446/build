package com.novell.zenwoks.command.commands;

import com.novell.zenwoks.command.process.ZenCommand;

import java.util.ArrayList;
import java.util.Arrays;

@ZenCommand
public class ZenServerLog extends ZenLogView
{
    @Override
    public String getCommandName()
    {
        return "log-server";
    }

    @Override
    public ArrayList<String> argumentsNameList()
    {
        return new ArrayList<String>(Arrays.asList(new String[]{"User Name", "Password", "Url",}));
    }

    @Override
    public void executeCommand(ArrayList<String> argument) throws Exception
    {
        int pos=argument.size();
        if(argument.size()>3)
        {
            pos=pos-1;
        }

        argument.add(pos,"/var/opt/novell/log/zenworks/services-messages.log");
        argument.add(pos,"zenserver");
        super.executeCommand(argument);
    }

    @Override
    public String usageString()
    {
        return "Usages:User-Name    Password    Url ";
    }

    @Override
    public String helpString()
    {
        return "Show Service message log";
    }
}
