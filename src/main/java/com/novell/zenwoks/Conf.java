package com.novell.zenwoks;

import java.io.File;
import java.net.URISyntaxException;
import java.security.CodeSource;

public class Conf
{
    public static String getCurrentWorkingDir()
    {
        return new File(".").getAbsolutePath();
    }

    public static String getConfLocation()
    {
        try
        {
            CodeSource codeSource = Conf.class.getProtectionDomain().getCodeSource();
            File jarFile = new File(codeSource.getLocation().toURI().getPath());
            String jarDir = jarFile.getParentFile().getPath();
            return jarDir;
        }
        catch(URISyntaxException e)
        {
            e.printStackTrace();
        }
        return new File(".").getAbsolutePath();
    }
    public static String getExportFileExt()
    {
        return ".zenin";
    }
}
