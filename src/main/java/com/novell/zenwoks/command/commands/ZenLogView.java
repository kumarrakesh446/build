package com.novell.zenwoks.command.commands;

import com.novell.zenwoks.command.process.ZenCommand;
import com.novell.zenwoks.command.process.ZenDockerBaseCommand;
import com.novell.zenwoks.zenworksbuild.ZenworksLogViewer;

import java.util.ArrayList;
@ZenCommand
public class ZenLogView extends ZenDockerBaseCommand
{
    @Override
    public String getCommandName()
    {
        return "log";
    }

    @Override
    public void executeCommand(ArrayList<String> argument) throws Exception
    {
        ZenworksLogViewer.main(argument.toArray(new String[] {}));
    }

    @Override
    public String usageString()
    {
        return super.usageString() + " log-location [number of line to show  default 1000 lines]";
    }

    @Override
    public String helpString()
    {
        return "Show Log  file location(/var/opt/novell/log/zenworks/zcc.log)";
    }
}
