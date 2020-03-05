package com.novell.zenwoks.command.commands;

import com.novell.zenwoks.command.process.ZenCommand;
import com.novell.zenwoks.command.process.ZenDockerBaseCommand;
import com.novell.zenwoks.zenworksbuild.SSHTerminalConsoleOutMain;

import java.util.ArrayList;

@ZenCommand
public class ZenDockerLog extends ZenDockerBaseCommand
{
    @Override
    public String getCommandName()
    {
        return "log-docker";
    }

    @Override
    public void executeCommand(ArrayList<String> argument) throws Exception
    {
        argument.set(3,"docker logs "+argument.get(3));
        SSHTerminalConsoleOutMain.main(argument.toArray(new String[] {}));
    }

    @Override
    public String usageString()
    {
        return super.usageString() + " container-name";
    }

    @Override
    public String helpString()
    {
        return "Show docker log of running container";
    }
}
