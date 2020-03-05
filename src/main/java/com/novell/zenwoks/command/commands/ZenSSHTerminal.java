package com.novell.zenwoks.command.commands;

import com.novell.zenwoks.command.process.ZenCommand;
import com.novell.zenwoks.command.process.ZenDockerBaseCommand;
import com.novell.zenwoks.command.process.ZenServerBoxBaseCommand;
import com.novell.zenwoks.zenworksbuild.SSHDockerTerminalConsoleInOut;
import com.novell.zenwoks.zenworksbuild.SSHTerminalConsoleInOutMain;
import com.novell.zenwoks.zenworksbuild.SSHTerminalConsoleOutMain;

import java.util.ArrayList;

@ZenCommand
public class ZenSSHTerminal extends ZenServerBoxBaseCommand
{
    @Override
    public String getCommandName()
    {
        return "ssh";
    }

    @Override
    public void executeCommand(ArrayList<String> argument) throws Exception
    {
        SSHTerminalConsoleOutMain.main(argument.toArray(new String[] {}));
    }

    @Override
    public String helpString()
    {
        return "SSH server";
    }
}
