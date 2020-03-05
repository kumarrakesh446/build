package com.novell.zenwoks.command.process;

import org.mockito.Matchers;
import org.mockito.Mockito;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class ZenDockerCommandProcessorTest
{

    @Test(dataProvider = "helpData")
    public void testShouldPrintHelp(String[] input,int times) throws Exception
    {
        ZenDockerCommandProcessor commandProcessor = Mockito.spy(new ZenDockerCommandProcessor());
        commandProcessor.executeCommand(input);
        Mockito.verify(commandProcessor,Mockito.times(times)).printHelpMessage(Matchers.anyMap());
    }

    @DataProvider(name = "helpData")
    public Object[][] helpData()
    {
        return new Object[][] {
                {null,1},
                {new String[] {},1},
                {new String[] {"-h"},1},
                {new String[] {"--help"},1},
                {new String[] {"a -help"},1},
                {new String[] {"a","-help"},1},
                {new String[] {"exec","a"},0}
        };
    }

    @Test
    public void execCommand() throws Exception
    {
        ZenDockerCommandProcessor commandProcessor = Mockito.spy(new ZenDockerCommandProcessor());
        commandProcessor.executeCommand("exec".split(" "));
    }


    @Test
    public void exportAndUseCommand() throws Exception
    {
        ZenDockerCommandProcessor commandProcessor = Mockito.spy(new ZenDockerCommandProcessor());
        commandProcessor.executeCommand("export my root novell rakesh-jenkins.epm.blr.novell.com".split(" "));
        commandProcessor.executeCommand("export catlina log -in=my zenserver /opt/novell/zenworks/share/tomcat/logs/catalina.out".split(" "));
        commandProcessor.executeCommand("view".split(" "));
        commandProcessor.executeCommand("-in=catlina".split(" "));
    }
    /*
        @Test
        public void exportViewCommand() throws Exception
        {
            ZenDockerCommandProcessor commandProcessor = Mockito.spy(new ZenDockerCommandProcessor());
            commandProcessor.executeCommand("export my root novell rakesh-jenkins.epm.blr.novell.com".split(" "));
            commandProcessor.executeCommand("export kshama root novell kshama-jenkins.epm.blr.novell.com".split(" "));
            commandProcessor.executeCommand("view".split(" "));
        }

        @Test
        public void zccViewCommand() throws Exception
        {
            ZenDockerCommandProcessor commandProcessor = Mockito.spy(new ZenDockerCommandProcessor());
            commandProcessor.executeCommand("export my root novell rakesh-jenkins.epm.blr.novell.com".split(" "));
            commandProcessor.executeCommand("zcclog -in=my 10".split(" "));
        }

        @Test
        public void copyCommand() throws Exception
        {
            ZenDockerCommandProcessor commandProcessor = Mockito.spy(new ZenDockerCommandProcessor());
            commandProcessor.executeCommand("export my root novell rakesh-jenkins.epm.blr.n\ell.com".split(" "));
            commandProcessor.executeCommand("copy -in=my zenserver D:\\Olympus\\security\\. /tmp/rak".split(" "));
        }
    */
    @Test
    public void copyServerCommand() throws Exception
    {
        ZenDockerCommandProcessor commandProcessor = Mockito.spy(new ZenDockerCommandProcessor());
        commandProcessor.executeCommand("export my root novell rakesh-jenkins.epm.blr.novell.com".split(" "));
        commandProcessor.executeCommand("copyserver -in=my D:\\Olympus\\WorkSpace\\GIT_HUB\\2020\\server-core\\server\\win-mdm\\stage\\linux\\. /".split(" "));
    }


    @Test
    public void patchServerCommand() throws Exception
    {
        ZenDockerCommandProcessor commandProcessor = Mockito.spy(new ZenDockerCommandProcessor());
        commandProcessor.executeCommand("export my root novell rakesh-jenkins.epm.blr.novell.com".split(" "));
        commandProcessor.executeCommand("patch -in=my zenserver".split(" "));
    }
}