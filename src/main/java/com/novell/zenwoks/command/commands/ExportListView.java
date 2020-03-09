package com.novell.zenwoks.command.commands;

import com.novell.zenwoks.Conf;
import com.novell.zenwoks.command.process.ZenCommand;
import com.novell.zenwoks.command.process.ZenDockerCommand;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

@ZenCommand
public class ExportListView implements ZenDockerCommand
{
    @Override
    public String getCommandName()
    {
        return "view";
    }

    @Override
    public ArrayList<String> argumentsNameList()
    {
        return new ArrayList<String>();
    }

    @Override
    public void executeCommand(ArrayList<String> argument)
    {
        File dir = new File(Conf.getConfLocation());
        System.out.println(dir);
        File[] files = dir.listFiles(new FilenameFilter()
        {
            public boolean accept(File dir, String name)
            {
                return name.toLowerCase().endsWith(".zenin");
            }
        });

        for(File file : files)
        {
            try
            {
                System.out.println();
                System.out.println(file.getName().replace(Conf.getExportFileExt(), "") + " -" + Arrays.toString(readFile(file.getName())));
            }
            catch(Exception e)
            {

            }
        }

    }

    public static String[] readFile(String filename) throws Exception
    {
        StringBuilder builder = new StringBuilder();
        try(Scanner reader = new Scanner(new FileInputStream(new File(Conf.getConfLocation(), filename))))
        {
            String str = reader.nextLine();
            if(str.contains("-in="))
            {
                    return extractInParam(str.split(" "));
            }
            else
            {
                builder.append(str);
            }

        }

        return builder.toString().split(" ");
    }
    public static String[] extractInParam(String[] args) throws Exception
    {

        ArrayList<String> arrayList = new ArrayList<>();
        for(int i = 0; i < args.length; i++)
        {
            if(args[i].toLowerCase().startsWith("-in="))
            {
                String[] split = args[i].split("=");
                if(split.length > 1)
                {
                    ArrayList<String> readInput = readInputParam(split[1]);
                    arrayList.addAll(readInput);
                }
                else
                {
                    throw new Exception("invalid usage in=exported-input name");
                }
            }
            else
            {

                arrayList.add(args[i]);
            }
        }
        return arrayList.toArray(new String[] {});
    }

    private static ArrayList<String> readInputParam(String s) throws Exception
    {
        return new ArrayList<>(Arrays.asList(ExportListView.readFile(s+Conf.getExportFileExt())));
    }
    @Override
    public String usageString()
    {
        return "[in-param-name]";
    }

    @Override
    public String helpString()
    {
        return "View Exported parameters.";
    }
}
