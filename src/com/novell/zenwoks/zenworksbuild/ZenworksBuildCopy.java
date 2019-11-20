package com.novell.zenwoks.zenworksbuild;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.StringTokenizer;

import com.jcraft.jsch.JSchException;

public class ZenworksBuildCopy {
	private static HashMap<String,String> extraParamMap;
	private SSHCopyFileTerminal copyFileTerminal;
	private SSHTerminal sshTerminal;
	private static final long MAX_LAST_MODIFY_TIME_HR=1;
	//private SSHManager sshManager;

	private static final String JAVA_JAR_LIB = "/opt/novell/zenworks/java/lib";
	private static final String ZENWORK_JAR_LIB = "/opt/novell/zenworks/share/tomcat/webapps/zenworks/WEB-INF/lib";
	private static final String ZENWORK_WEB_LIB ="/opt/novell/zenworks/share/tomcat/webapps";
	private static final String ZENWORK_JSC_PAGE="/jsp/pages";

	public void login(String userName, String password, String aditionalCriteria) throws TerminalLoginException {

		copyFileTerminal = new SSHCopyFileTerminal();
		copyFileTerminal.login(userName, password, aditionalCriteria);

		sshTerminal = new SSHTerminal();
		sshTerminal.login(userName, password, aditionalCriteria);
		

	}

	public String copyjar(String fileName) throws TerminalExecutionException {

		String outString = "";
		try {
			File file = new File(fileName);
			String jarName = file.getName();
			jarName = getJarLocation(jarName);
			if(jarName==null||jarName.isEmpty())
			{
				if(fileName.toLowerCase().endsWith(".jsc"))
				{
					
					
					jarName = getJarLocation(file.getParentFile().getName());
					jarName+="/"+file.getName();
				}
				
			}
			if(jarName==null||jarName.isEmpty())
			{
				return "";
			}
				
			createBackup(jarName);
			copyFileTerminal.executeCommand("src=" + fileName + " out=" + jarName);
			return "Done!!!";
		} catch (Exception e) {
			e.printStackTrace();
		}

		return outString;
	}

	private void createBackup(String jarName) throws TerminalExecutionException {
		
		String findjar="";
		int i=0;
		File file=new File(jarName);
		/*do
		{
			i++;
			findjar= sshTerminal.executeCommand("dir " + file.getParent().replaceAll("\\\\", "/")  + " | grep '" + file.getName() +i+ ".backup'");
		
		}while(findjar != null &&! findjar.isEmpty());*/
		
		
		findjar= sshTerminal.executeCommand("dir " + file.getParent().replaceAll("\\\\", "/")  + " | grep '" +file.getName().replace(getExtension(file.getName()), "")+ ".backup'");
		if(findjar == null || findjar.isEmpty())
		{
			sshTerminal.executeCommand("mv '" + jarName + "' '"+jarName.replace(getExtension(file.getName()), "")+ ".backup'");
		}
		
	}

	private CharSequence getExtension(String fileName) {
			
		return fileName.substring(fileName.lastIndexOf('.'));
	}

	private String getJarLocation(String jarName) throws JSchException, IOException, TerminalExecutionException {
		String serverJarName = "";

		if (jarName.matches(".*\\d\\d\\.\\d\\.\\d.jar")) {
			jarName = jarName.replaceAll("-\\d\\d\\.\\d\\.\\d", "");
		}
		serverJarName = getLibLocation(jarName);
		if(serverJarName==null||serverJarName.isEmpty())
			return "";
		return serverJarName + "/" + jarName;
	}

	private String getLibLocation(String jarName) throws JSchException, IOException, TerminalExecutionException {
		String findjar = sshTerminal.executeCommand("find " + JAVA_JAR_LIB + " -name '" + jarName + "'");
		if (findjar != null && !findjar.isEmpty()) {
			return JAVA_JAR_LIB;
		}
		findjar = sshTerminal.executeCommand("find " + ZENWORK_JAR_LIB + " -name '" + jarName + "'");
		if (findjar != null && !findjar.isEmpty()) {
			return ZENWORK_JAR_LIB;
		}
		
		findjar = sshTerminal.executeCommand("find " + ZENWORK_WEB_LIB + " -path  \\*/" + jarName + "");
		if (findjar != null && !findjar.isEmpty()) {
			return new File(findjar).getParent().replace('\\', '/');
		}
		
//find /opt/novell/zenworks/share/tomcat/webapps/ -name
		return "";
	}

	public static void main(String[] args) throws TerminalExecutionException, TerminalLoginException, TerminalException {

		String userId = "";
		String pass = "";
		String url = "";
		String curDir = "";
		curDir = "";
		if(args==null||args.length==0)
		{
			Scanner scanner=new Scanner(System.in);
			System.out.println("Login User:");
			userId=scanner.nextLine();
			
			
			System.out.println("Password:");
			pass=scanner.nextLine();
			
			System.out.println("Server Url:");
			url=scanner.nextLine();
			
			
			System.out.println("Maven Directory:");
			curDir=scanner.nextLine();
		}
		else
		{
			userId=args[0];
			pass=args[1];
			url=args[2];
			curDir=args[3];
			
		}
		String extraParam="";
		for (int i = 4; i < args.length; i++) {
			extraParam += args[i];
		}
		System.out.println("extraParam="+extraParam);
		extraParamMap=extractExtraParam(extraParam);
		
		ZenworksBuildCopy zenworksBuildCopy = new ZenworksBuildCopy();
		List<String> jarList = getAllJarInBuildPath(curDir);
		
		if(extraParamMap.get("jsc")!=null&&extraParamMap.get("jsc").equals("true"))
		{
			List<String> jscFileList = getAllChangedJsc(curDir);
			jarList.addAll(jscFileList);
			
		}
		
		
		if (jarList.size() < 1) {
			System.out.println("No Changes found....");

		} else {
			System.out.println("Started copying file to server....");
			zenworksBuildCopy.login(userId, pass, url);
			zenworksBuildCopy.copyJars(jarList);
			zenworksBuildCopy.disconnect();
		}
		System.out.println("!!!!!!!!Done Copy!!!!!!!!!!!!!");
	}

	private static List<String> getAllChangedJsc(String curDir) {
		List<String> list=new ArrayList<>();
		findFile(".jsc", new File(curDir), list);
		for (Iterator<String> iterator = list.iterator(); iterator.hasNext();) {
			String string =  iterator.next();
			
			File file=new File(string);
			if((System.currentTimeMillis()-file.lastModified())/1000/60/60>1||string.contains("\\target"))
			{
				iterator.remove();
			}
			
		}
		
		
		return list;
	}

	private static HashMap<String,String> extractExtraParam(String extraParam) {
		extraParamMap=new HashMap<String,String>();
		
		if(extraParam!=null&&!extraParam.isEmpty())
		{
			StringTokenizer stringTokenizer=new StringTokenizer(extraParam, "=");
			while (stringTokenizer.hasMoreElements()) {
				String param = (String) stringTokenizer.nextElement();
				
				extraParamMap.put(param.toLowerCase(), ((String) stringTokenizer.nextElement()).toLowerCase());
				
			}
		}
		
		
		
		return extraParamMap;
	}

	private void copyJars(List<String> jarList) throws TerminalExecutionException {

		for (int i = 0; i < jarList.size(); i++) {

			copyjar(jarList.get(i));
		}

	}

	private void disconnect() throws TerminalException {
		copyFileTerminal.disconnect();
		sshTerminal.disconnect();

	}

	private static List<String> getAllJarInBuildPath(String curDir) {

		List<String> jarList = new ArrayList<>();
		System.out.println("***********************Finding All build jars***********************");
		findFile("-17.3.0.jar", new File(curDir), jarList);
		System.out.println("********************************************************************");
		return jarList;
	}

	public static void findFile(String name, File file, List<String> jarList) {
		File[] list = file.listFiles();
		if (list != null)
			for (File fil : list) {
				if (fil.isDirectory()) {
					findFile(name, fil, jarList);
				} else if (fil.getName().matches(".*\\d\\d\\.\\d\\.\\d.jar")) {
					System.out.println(fil.getAbsolutePath());
					jarList.add(fil.getAbsolutePath());
				}
			}
	}
	
	public String getJarVersion(String fileName)
	{
		String jarVersion=fileName;
		if(fileName!=null&&fileName.isEmpty())
		{
			if(fileName.lastIndexOf('-')!=-1)
			{
				String subFileName=fileName.substring(fileName.lastIndexOf('-'));
			}
			
		}
		
		return jarVersion;
		
	}
}
