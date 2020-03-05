package com.novell.zenwoks.command.commands;


import com.novell.zenwoks.command.process.ZenCommand;
import com.novell.zenwoks.command.process.ZenDockerBaseCommand;
import com.novell.zenwoks.zenworksbuild.SSHDockerTerminalConsoleInOut;
import com.novell.zenwoks.zenworksbuild.TerminalException;
import com.novell.zenwoks.zenworksbuild.TerminalExecutionException;
import com.novell.zenwoks.zenworksbuild.TerminalLoginException;

import java.io.IOException;
import java.util.ArrayList;

@ZenCommand
public class ZenDockerExecCommand extends ZenDockerBaseCommand
{
    @Override
    public String getCommandName()
    {
        return "exec";
    }

    @Override
    public ArrayList<String> argumentsNameList()
    {
        return super.argumentsNameList();
    }

    @Override
    public void executeCommand(ArrayList<String> argument)
    {
        try
        {
            SSHDockerTerminalConsoleInOut.main(argument.toArray(new String[]{}));
        }
        catch(TerminalLoginException e)
        {
            e.printStackTrace();
        }
        catch(TerminalExecutionException e)
        {
            e.printStackTrace();
        }
        catch(TerminalException e)
        {
            e.printStackTrace();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        catch(InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public String usageString()
    {
        return  super.usageString()+"[command]";
    }

    @Override
    public String helpString()
    {
        return "Run a command in a running container";
    }

}
