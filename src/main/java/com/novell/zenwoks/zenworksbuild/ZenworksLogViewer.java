package com.novell.zenwoks.zenworksbuild;

import java.io.IOException;
import java.util.Scanner;

public class ZenworksLogViewer
{

    public static void main(String[] args) throws TerminalLoginException, TerminalExecutionException, TerminalException, IOException, InterruptedException
    {


        String userId = "";
        String pass = "";
        String url = "";
        String command = "";
        String container = "";
        String nuOfLine = "1000";
        if(args == null || args.length < 3)
        {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Login User:");
            userId = scanner.nextLine();


            System.out.println("Password:");
            pass = scanner.nextLine();

            System.out.println("Server Url:");
            url = scanner.nextLine();
            if(args != null && args.length == 1)
            {
                command = args[0];
            }
            else
            {

                command = scanner.nextLine();

            }

        }
        else
        {
            userId = args[0];
            pass = args[1];
            url = args[2];
            container = args[3];
            command=args[4];
            if(args.length > 5)
            {
                nuOfLine = args[5];
            }
        }


        command = "tail -f -n " + nuOfLine + " " + command;
        SSHDockerTerminalConsoleInOut.main(new String[] {userId, pass, url, container, command});


    }

}
