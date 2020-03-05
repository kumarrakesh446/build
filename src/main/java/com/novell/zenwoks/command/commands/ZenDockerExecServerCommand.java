package com.novell.zenwoks.command.commands;


import com.novell.zenwoks.command.process.ZenCommand;
import com.novell.zenwoks.command.process.ZenDockerBaseCommand;
import com.novell.zenwoks.zenworksbuild.SSHDockerTerminalConsoleInOut;
import com.novell.zenwoks.zenworksbuild.TerminalException;
import com.novell.zenwoks.zenworksbuild.TerminalExecutionException;
import com.novell.zenwoks.zenworksbuild.TerminalLoginException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

@ZenCommand
public class ZenDockerExecServerCommand extends ZenDockerExecCommand
{
    @Override
    public String getCommandName()
    {
        return "exec-server";
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

        argument.add(pos, "zenserver");
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
        return "Run a command in a zenserver running container";
    }

}
