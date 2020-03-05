package com.novell.zenwoks.command.commands;

import com.novell.zenwoks.command.process.ZenCommand;
import com.novell.zenwoks.command.process.ZenDockerBaseCommand;
import com.novell.zenwoks.zenworksbuild.ZenworksBuildCopy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ZenCommand
public class ZenDockerPatch extends ZenDockerBaseCommand
{
    @Override
    public String getCommandName()
    {
        return "patch";
    }

    @Override
    public void executeCommand(ArrayList<String> argument) throws Exception
    {
        int res = mavenBuild(argument.subList(4, argument.size()));
        if(res != 0)
        {
            System.out.println("Build fail");
        }
        else
        {
            System.out.println("Starting Copy Build....");
            ZenworksBuildCopy.main(new String[] {argument.get(0), argument.get(1), argument.get(2), argument.get(3), "."});
        }
    }

    private int mavenBuild(List<String> argument)
    {
        String[] mavenArgs;
        if(argument.size() <= 0)
        {
            mavenArgs = "cmd /c mvn clean install -e -X  -Dmaven.test.skip=true -Dmaven.native.skip=true".split(" ");
        }
        else
        {
            argument.add(0, "cmd");
            argument.add(1, "/c");
            argument.add(2, "mvn");
            argument.add(3, "clean");
            argument.add(4, "install");
            mavenArgs = argument.toArray(new String[0]);
        }
        try
        {
            System.out.println("building project with-" + Arrays.toString(mavenArgs));
            Process process = Runtime.getRuntime().exec(mavenArgs);

            printOutStream(process.getInputStream());
            printOutStream(process.getErrorStream());
            return process.waitFor();

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return 1;
    }

    private void printOutStream(final InputStream inputStream) throws IOException
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    String line;
                    BufferedReader input = new BufferedReader(new InputStreamReader(inputStream));
                    while((line = input.readLine()) != null)
                    {
                        System.out.println(line);
                    }
                    input.close();
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    @Override
    public String usageString()
    {
        return super.usageString() + " [maven argument-default -e -X  -Dmaven.test.skip=true -Dmaven.native.skip=true]";
    }

    @Override
    public String helpString()
    {
        return "Build a module and patch to a running container";
    }
}
