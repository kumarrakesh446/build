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
public class ZenDockerComposeCommand extends ZenDockerBaseCommand
{
    @Override
    public String getCommandName()
    {
        return "docker-compose";
    }

    @Override
    public ArrayList<String> argumentsNameList()
    {
        ArrayList<String> arrayList = super.argumentsNameList();
        arrayList.remove(3);
        arrayList.add("docker-compose-folder");
        arrayList.add("compose-args");
        return arrayList;
    }

    @Override
    public void executeCommand(ArrayList<String> argument) throws Exception
    {
        String commandType = argument.get(4);
        Terminal terminal = new SSHTerminal();
        terminal.login(argument.get(0),argument.get(1),argument.get(2));
        //terminal.executeCommand("cd  " + argument.get(3));
        //System.out.println(terminal.executeCommand("ls  "));
        String extraArgs="";
        for(int i=5;i<argument.size();i++)
        {
            extraArgs+=argument.get(i);
        }
        System.out.println(terminal.executeCommand("cd  " + argument.get(3)+";\nls;\n"+"docker-compose " + commandType + " " + extraArgs));
        terminal.disconnect();

    }

    @Override
    public String helpString()
    {
        return "cd /home/jenkins/jobs/../../../../compose\n" +
                "docker-compose up/down [-d if provided with up as extra argument will start services in detach mode]";
    }

    @Override
    public String usageString(){
        return "Usages:User-Name    Password    Url   Docker-Compose-Folder   Up/Down ";

    }

}
