package com.novell.zenwoks.command.process;

import com.novell.zenwoks.Conf;
import com.novell.zenwoks.command.commands.ExportListView;
import org.atteo.classindex.ClassIndex;

import java.io.FileNotFoundException;
import java.text.Format;
import java.util.*;

public class ZenDockerCommandProcessor
{
    public static void main(String[] args)
    {

        try
        {
            /*System.out.println(Conf.getCurrentWorkingDir());
            System.out.println(Conf.getConfLocation());*/
            ZenDockerCommandProcessor commandProcessor = new ZenDockerCommandProcessor();
            commandProcessor.executeCommand(args);
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
        }

    }

    protected void executeCommand(String[] args) throws Exception
    {
        Map<String,ZenDockerCommand> availableCommands = getAvailableCommands();

        if(args == null || args.length < 1 || (args.length == 1 && (args[0].equalsIgnoreCase("-h") || args[0].equalsIgnoreCase("--help"))))
        {
            printHelpMessage(availableCommands);
        }
        else
        {
            args = ExportListView.extractInParam(args);
            String command = args[0];
            ZenDockerCommand zenDockerCommand = availableCommands.get(command);
            if(zenDockerCommand == null)
            {
                printHelpMessage(availableCommands);
            }
            else
            {
                if(args.length > 1)
                {
                    args = Arrays.copyOfRange(args, 1, args.length);
                }

                if(args.length >= zenDockerCommand.argumentsNameList().size())
                {

                    System.out.println(Arrays.toString(args));
                    zenDockerCommand.executeCommand(new ArrayList<String>(Arrays.asList(args)));
                }
                else
                {
                    System.out.println("input pass-"+Arrays.toString(args));
                    printHelp(zenDockerCommand);
                    System.out.println(zenDockerCommand.usageString());
                }
            }
        }
    }



    protected void printHelpMessage(Map<String,ZenDockerCommand> availableCommands)
    {
        if(availableCommands == null || availableCommands.isEmpty())
        {
            System.out.println("No command found");
        }
        else
        {
            System.out.println("Available commands:");
            for(ZenDockerCommand command : availableCommands.values())
            {
                printHelp(command);
            }
        }
    }

    private void printHelp(ZenDockerCommand command)
    {
        System.out.println();
        System.out.print(String.format("%-20s -",command.getCommandName()));
        String[] split = command.helpString().split("\n");
        for(int i=0;i<split.length;i++)
        {
            if(i==0)
            System.out.println(split[i]);
            else
                System.out.println(String.format("%25s %s"," ",split[i].trim()));
        }
        System.out.println();
    }

    private Map<String,ZenDockerCommand> getAvailableCommands()
    {

        Map<String,ZenDockerCommand> zenDockerCommands = new TreeMap<>();
        Iterable<Class<?>> annotated = ClassIndex.getAnnotated(ZenCommand.class);

        for(Class aClass : annotated)
        {
            try
            {
                ZenDockerCommand instance = (ZenDockerCommand)aClass.newInstance();
                zenDockerCommands.put(instance.getCommandName(), instance);
            }
            catch(InstantiationException e)
            {
                e.printStackTrace();
            }
            catch(IllegalAccessException e)
            {
                e.printStackTrace();
            }
        }
        return zenDockerCommands;
    }
}
