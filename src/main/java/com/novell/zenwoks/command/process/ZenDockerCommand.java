package com.novell.zenwoks.command.process;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public interface ZenDockerCommand
{
    String getCommandName();
    ArrayList<String> argumentsNameList();
    void executeCommand(ArrayList<String> argument) throws Exception;
    String usageString();
    String helpString();
}
