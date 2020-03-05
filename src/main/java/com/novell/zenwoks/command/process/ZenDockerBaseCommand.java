package com.novell.zenwoks.command.process;


import java.util.ArrayList;

public abstract class ZenDockerBaseCommand extends ZenServerBoxBaseCommand
{
    @Override
    public ArrayList<String> argumentsNameList()
    {
        ArrayList<String> arrayList = super.argumentsNameList();
        arrayList.add("Container Id/Name");
        return arrayList;
    }

    @Override
    public String usageString()
    {
        return super.usageString().concat(" Container-Id/Name");
    }
}
