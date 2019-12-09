package com.novell.zenwoks.zenworksbuild;

import java.io.IOException;

public class Maintest {

	public static void main(String[] args) throws IOException {
		
		while(true)
		{
			int i=RawConsoleInput.read(true);
			if(i!=-1)
				System.out.println(i);
		}
		

	}

}
