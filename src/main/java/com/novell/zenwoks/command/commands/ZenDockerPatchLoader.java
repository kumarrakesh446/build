package com.novell.zenwoks.command.commands;

import com.novell.zenwoks.command.Constans;
import com.novell.zenwoks.command.process.ZenCommand;

import java.util.ArrayList;

@ZenCommand
public class ZenDockerPatchLoader extends ZenDockerPatch
{

    @Override
    public String getCommandName()
    {
        return "patch-loader";
    }

    @Override
    public ArrayList<String> argumentsNameList()
    {
        ArrayList<String> arrayList = super.argumentsNameList();
        arrayList.remove(3);
        return arrayList;
    }

    @Override
    public void executeCommand(ArrayList<String> argument) throws Exception
    {
        argument.add(3,"zenloader");
        super.executeCommand(argument);
    }

    @Override
    public String usageString()
    {
        return Constans.USAGES_DEFAULT + "  [maven argument-default -e -X  -Dmaven.test.skip=true -Dmaven.native.skip=true]";
    }

    @Override
    public String helpString()
    {
        return "Build a module and patch to zenloader running container";
    }
}
