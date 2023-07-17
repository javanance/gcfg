package com.gof.util;

public class StringUtil {
	/*static public String directodyStringCheck(String sdir) {
		if(!sdir.endsWith(PropertiesUtil.getData("DIRECTORY_CHAR"))) {
			sdir = sdir + "\\";
		}
		return sdir;
	}*/
	
	public static String ColNametoCarmel(String s) {
		char[] c = s.toLowerCase().trim().replaceAll(" ", "").toCharArray();		
		String sGen = "";
		boolean bUpper = false;
		for(int i = 0 ; i < c.length ; i++) {
			if(bUpper) {
				sGen += (char)((int)c[i]-32);
				bUpper = false;
			}else if(i==0 && ((int)c[i] != 95)) {
				sGen += (char)((int)c[i]);
			}else if((int)c[i] == 95) {
				sGen += " ";
				bUpper = true;
			}else {
				sGen += c[i];
			}
//			System.out.println(c[i]+" : "+(int)c[i]);
			
		}
//		System.out.println(sGen.replaceAll(" ", ""));
	
		return sGen.replaceAll(" ", "");
	}

	public static String ColNametoPascal(String s) {
		char[] c = s.toLowerCase().trim().replaceAll(" ", "").toCharArray();
		String sGen = "";
		boolean bUpper = false;
		for(int i = 0 ; i < c.length ; i++) {
			if(bUpper) {
				sGen += (char)((int)c[i]-32);
				bUpper = false;
			}else if(i==0 && ((int)c[i] != 95)) {
				sGen += (char)((int)c[i]-32);
			}else if((int)c[i] == 95) {
				sGen += " ";
				bUpper = true;
			}else {
				sGen += c[i];
			}
//			System.out.println(c[i]+" : "+(int)c[i]);
			
		}
//		System.out.println(sGen.replaceAll(" ", ""));
	
		return sGen.replaceAll(" ", "");
	}
}
