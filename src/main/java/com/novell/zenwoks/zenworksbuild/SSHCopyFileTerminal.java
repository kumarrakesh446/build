package com.novell.zenwoks.zenworksbuild;

import com.jcraft.jsch.*;
import com.novell.zenwoks.zenworksbuild.Shell.MyUserInfo;

import java.io.*;


public class SSHCopyFileTerminal implements Terminal
{
    private boolean isConnected = false;
    private JSch jsch;
    private String user;
    private String host;
    private String passwd;
    private Channel channel;
    private PipedOutputStream commandWriter;
    private PipedInputStream commandOutput;
    private Session session;
    private String stringOutput;
    private ChannelSftp channelSftp;


    @Override
    public void login(String userName, String password, String aditionalCriteria) throws TerminalLoginException
    {

        isConnected = true;
        user = userName;
        host = aditionalCriteria;
        passwd = password;
        doLogin();
    }

    private void doLogin()
    {


        try
        {
            jsch = new JSch();

            //jsch.setKnownHosts("/home/foo/.ssh/known_hosts");


            session = jsch.getSession(user, host, 22);


            session.setPassword(passwd);

            MyUserInfo ui = new MyUserInfo()
            {
                public void showMessage(String message)
                {

                }

                public boolean promptYesNo(String message)
                {

                    return true;
                }

                // If password is not given before the invocation of Session#connect(),
                // implement also following methods,
                //   * UserInfo#getPassword(),
                //   * UserInfo#promptPassword(String message) and
                //   * UIKeyboardInteractive#promptKeyboardInteractive()

            };

            session.setUserInfo(ui);

            // It must not be recommended, but if you want to skip host-key check,
            // invoke following,
            // session.setConfig("StrictHostKeyChecking", "no");

            //session.connect();
            session.connect(30000);   // making a connection with timeout.


        }
        catch(Exception e)
        {
            System.out.println(e);
        }

    }

    @Override
    public void disconnect() throws TerminalException
    {
        session.disconnect();
        isConnected = false;

    }

    @Override
    public boolean isConnected() throws TerminalException
    {
        return isConnected;
    }

    @Override
    public String executeCommand(String command) throws TerminalExecutionException
    {


        System.out.println(command);
        String outString = "";
        try
        {

            if(command == null || command.isEmpty() || !command.contains("src=") || !command.contains("out="))
                return "Invalid command- usage src=scourcepath  out=outpath";

            command = command.trim();
            String scr = command.substring(command.indexOf("=") + 1, command.indexOf("out="));
            File file = new File(scr.trim());


            String out = command.substring(command.lastIndexOf("=") + 1);
            out = out.trim();
            System.out.println();

            //String jarName = file.getName();
            //getJarLocation();/root/Desktop/filename.txt

            channelSftp = (ChannelSftp)session.openChannel("sftp");
            channelSftp.connect();
            SftpATTRS attrs = null;
            try
            {
                attrs = channelSftp.stat(out);
            }
            catch(Exception e)
            {
            }
            if(attrs == null)
            {
                channelSftp.mkdir(out);
            }
            channelSftp.cd(out); // Change Directory on SFTP Server
            if(file.isFile())
            {
                copyFileToServer(file);

            }
            else
            {

                recursiveFolderUpload(scr, out);
            }
            channelSftp.disconnect();

            System.out.println("Copied to server....");
            return "Done!!!";
        }
        catch(Exception e)
        {
            new TerminalExecutionException("Error in copying file");
        }

        finally
        {
            channelSftp.disconnect();
        }

        return stringOutput = outString;
    }

    private void copyFileToServer(File file) throws IOException, SftpException
    {
        try(FileInputStream fileInputStream = new FileInputStream(file))
        {
            channelSftp.put(fileInputStream, file.getName(), ChannelSftp.OVERWRITE);
            if(file.canExecute())
            {
                channelSftp.chmod(511,file.getName());
            }
        }
    }

    /**
     * This method is called recursively to Upload the local folder content to
     * SFTP server
     *
     * @param sourcePath
     * @param destinationPath
     * @throws SftpException
     * @throws FileNotFoundException
     */
    private void recursiveFolderUpload(String sourcePath, String destinationPath) throws SftpException, IOException
    {

        File sourceFile = new File(sourcePath);
        boolean copyContains = sourceFile.getName().startsWith(".");
        if(sourceFile.isFile())
        {

            // copy if it is a file
            channelSftp.cd(destinationPath);
            if(!copyContains)
            {
                copyFileToServer(sourceFile);
            }

        }
        else
        {

            File[] files = sourceFile.listFiles();

            if(files != null)
            {

                if(!copyContains)
                {
                    channelSftp.cd(destinationPath);
                }
                SftpATTRS attrs = null;

                // check if the directory is already existing
                try
                {
                    attrs = channelSftp.stat(destinationPath + (copyContains ? "" : ("/" + sourceFile.getName())));
                }
                catch(Exception e)
                {
                }

                // else create a directory
                if(attrs != null)
                {

                }
                else
                {
                    if(!copyContains)
                        channelSftp.mkdir(sourceFile.getName());

                }

                for(File f : files)
                {
                    recursiveFolderUpload(f.getAbsolutePath(), destinationPath + (copyContains ? "" : ("/" + sourceFile.getName())));
                }

            }
        }

    }

    @Override
    public String refresh() throws TerminalExecutionException
    {


        String outString = "";
        try
        {


            int avl = 0;
            do
            {
                try
                {
                    Thread.sleep(2000);
                }
                catch(InterruptedException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                avl = commandOutput.available();
                if(avl > 0)
                {

                    byte[] b = new byte[avl];

                    commandOutput.read(b);
                    outString = outString + new String(b);
                }
                avl = commandOutput.available();
            } while(avl > 0);

        }
        catch(IOException e)
        {

            e.printStackTrace();
        }

        return stringOutput += outString;
    }

    public static void main(String[] args) throws TerminalExecutionException, TerminalLoginException, TerminalException
    {

        if(args == null || args.length < 5)
        {
            System.out.println("Invalid Usage:");
            System.out.println("Use: userName password server ");
        }

        String userId = args[0];
        String pass = args[1];
        String url = args[2];

        String src = args[3];
        String dst = args[4];

        SSHCopyFileTerminal copyFileTerminal = new SSHCopyFileTerminal();
        copyFileTerminal.login(userId, pass, url);
        copyFileTerminal.executeCommand("src=" + src + "out=" + dst);
        copyFileTerminal.disconnect();
    }
}
