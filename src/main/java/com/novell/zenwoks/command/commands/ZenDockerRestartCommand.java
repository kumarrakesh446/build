package com.novell.zenwoks.command.commands;

import com.novell.zenwoks.command.process.ZenCommand;
import com.novell.zenwoks.command.process.ZenDockerBaseCommand;
import com.novell.zenwoks.zenworksbuild.SSHTerminal;
import com.novell.zenwoks.zenworksbuild.Terminal;

import java.util.ArrayList;


/**
 * Created by GHarsh on 3/5/2020.
 */
@ZenCommand
public class ZenDockerRestartCommand extends ZenDockerBaseCommand
{

    @Override
    public String getCommandName()
    {
        return "restart-container";
    }

    @Override
    public void executeCommand(ArrayList<String> argument) throws Exception
    {
        String containerName = argument.get(3);
        Terminal terminal = new SSHTerminal();
        terminal.login(argument.get(0),argument.get(1),argument.get(2));
        terminal.executeCommand("docker restart " + containerName);
        terminal.disconnect();
    }

    @Override
    public String helpString()
    {
        return "Restarts container by specifying container id/name\n" +
                "example:zendocker restart-container root novell zenserver.epm.blr.novell.com zenserver";
    }
}
