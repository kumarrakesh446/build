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
public class ZenDockerPatchServer extends ZenDockerPatch
{

    @Override
    public String getCommandName()
    {
        return "patch-server";
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
        argument.add(3,"zenserver");
        super.executeCommand(argument);
    }

    @Override
    public String usageString()
    {
        return   "Usages:User-Name    Password    Url  [maven argument-default -e -X  -Dmaven.test.skip=true -Dmaven.native.skip=true]";
    }

    @Override
    public String helpString()
    {
        return "Build a module and patch to zenserver running container";
    }
}
