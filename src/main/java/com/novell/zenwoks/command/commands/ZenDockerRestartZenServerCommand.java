package com.novell.zenwoks.command.commands;

import com.novell.zenwoks.command.process.ZenCommand;
import com.novell.zenwoks.command.process.ZenDockerBaseCommand;
import com.novell.zenwoks.zenworksbuild.SSHTerminal;
import com.novell.zenwoks.zenworksbuild.Terminal;

import java.util.ArrayList;

/**
 * Created by GHarsh on 3/6/2020.
 */
@ZenCommand
public class ZenDockerRestartZenServerCommand extends ZenDockerBaseCommand
{

    @Override
    public String getCommandName()
    {
        return "restart-server";
    }

    @Override
    public ArrayList<String> argumentsNameList()
    {
        ArrayList<String> arrayList = super.argumentsNameList();
        arrayList.remove(3);
        return arrayList;
    }

    @Override
    public void executeCommand(ArrayList<String> argument) throws Exception
    {
        argument.add(3,"zenserver");
        Terminal terminal = new SSHTerminal();
        terminal.login(argument.get(0),argument.get(1),argument.get(2));
        terminal.executeCommand("docker restart " + argument.get(3));
        terminal.disconnect();
    }

    @Override
    public String helpString()
    {
        return "Restarts zenserver container";
    }
}
