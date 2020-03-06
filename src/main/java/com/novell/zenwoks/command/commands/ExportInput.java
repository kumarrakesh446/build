package com.novell.zenwoks.command.commands;

import com.novell.zenwoks.Conf;
import com.novell.zenwoks.command.process.ZenCommand;
import com.novell.zenwoks.command.process.ZenDockerCommand;
import com.novell.zenwoks.zenworksbuild.TerminalExecutionException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;

@ZenCommand
public class ExportInput implements ZenDockerCommand
{
    @Override
    public String getCommandName()
    {
        return "export";
    }

    @Override
    public ArrayList<String> argumentsNameList()
    {
        return new ArrayList<String>(Arrays.asList(new String[] {"input-name", "parameters"}));
    }

    @Override
    public void executeCommand(ArrayList<String> argument) throws TerminalExecutionException
    {

        if(!argument.get(0).startsWith("name="))
        {
            throw new TerminalExecutionException("Invalid Argument\nExapmle " +
                    "zendocker export name=my root novell zenworks.epm.blr.novell.com");
        }
        try(PrintWriter out = new PrintWriter(new FileOutputStream(new File(Conf.getConfLocation(), argument.get(0).replace("name=","") + Conf.getExportFileExt()))))
        {
            for(int i = 1; i < argument.size(); i++)
            {
                out.write(argument.get(i) + " ");
            }
        }
        catch(FileNotFoundException e)
        {
            e.printStackTrace();
        }

    }

    @Override
    public String usageString()
    {
        return "name=name parameters(space separated)";
    }

    @Override
    public String helpString()
    {
        return "Export your in-put param in file and later run command by command and -in=input-name\n" +
                "               example- zendocker zcc-log -in=my";
    }
}
