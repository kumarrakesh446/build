package com.novell.zenwoks.command.process;


import java.util.ArrayList;
import java.util.Arrays;

public abstract class ZenServerBoxBaseCommand implements ZenDockerCommand
{
    @Override
    public ArrayList<String> argumentsNameList()
    {
        return new ArrayList<String>(Arrays.asList(new String[]{"User Name","Password","Url",}));
    }

    @Override
    public String usageString()
    {
        return "Usages:User-Name    Password    Url ";
    }
}
