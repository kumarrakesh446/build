package com.novell.zenwoks.command.commands;

import com.novell.zenwoks.command.process.ZenDockerBaseCommand;

import java.io.IOException;
import java.util.ArrayList;

public class ZenDockerBuildCommand extends ZenDockerBaseCommand
{
    @Override
    public String getCommandName()
    {
        return "patch";
    }

    @Override
    public void executeCommand(ArrayList<String> argument) throws IOException
    {

        //Process process = Runtime.getRuntime().exec();

    }

    @Override
    public String helpString()
    {
        return null;
    }
}
