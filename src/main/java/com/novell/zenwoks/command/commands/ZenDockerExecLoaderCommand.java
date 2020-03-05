package com.novell.zenwoks.command.commands;


import com.novell.zenwoks.command.process.ZenCommand;

import java.util.ArrayList;
import java.util.Arrays;

@ZenCommand
public class ZenDockerExecLoaderCommand extends ZenDockerExecCommand
{
    @Override
    public String getCommandName()
    {
        return "exec-loader";
    }

    @Override
    public ArrayList<String> argumentsNameList()
    {
        return new ArrayList<String>(Arrays.asList(new String[]{"User Name", "Password", "Url",}));
    }

    @Override
    public void executeCommand(ArrayList<String> argument)
    {
        int pos = argument.size();
        if(argument.size() > 3)
        {
            pos = pos - 1;
        }

        argument.add(pos, "zenloader");
        super.executeCommand(argument);
    }

    @Override
    public String usageString()
    {
        return "Usages:User-Name    Password    Url [command]";
    }

    @Override
    public String helpString()
    {
        return "Run command in zenloader running container";
    }

}
