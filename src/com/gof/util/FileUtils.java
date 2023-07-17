package com.gof.util;

import java.io.BufferedWriter;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * File Read, Write 를 기능을 수행하는 클래스
 * <p>  
 * 
 * @author takion77@gofconsulting.co.kr
 * 
 */
public class FileUtils {
	/*static public StringBuffer readFile(String sDir, String FileName) {
		StringBuffer sb = new StringBuffer();
	
		Path path = Paths.get(StringUtil.directodyStringCheck(sDir)+FileName);
		try(BufferedReader rd = Files.newBufferedReader(path,Charset.forName("UTF-8"))){
			String currentLine = null;
			while((currentLine = rd.readLine())!= null) {
				sb.append(currentLine+PropertiesUtil.getData("LINE_BREAK_CHAR"));
			}
		}catch(IOException iop) {
			iop.printStackTrace();
		}
		return sb;
	}*/
	
	/*static public ArrayList<StringBuffer> readFileBufferList(String sDir, String sFileName) {
		ArrayList<StringBuffer> lsb = new ArrayList();
	
		Path path = Paths.get(StringUtil.directodyStringCheck(sDir)+sFileName);
		try(BufferedReader rd = Files.newBufferedReader(path,Charset.forName("UTF-8"))){
			String currentLine = null;
			while((currentLine = rd.readLine())!= null) {
				StringBuffer sb = new StringBuffer();
//				System.out.println(currentLine);
				lsb.add(sb.append(currentLine));
			}
		}catch(IOException iop) {
			iop.printStackTrace();
		}
		return lsb;
	}*/
	
	/*static public StringBuffer RscriptReadFile(String sFileName) {
		String sPath = PropertiesUtil.getData("RSCRIPT_REPOSITORY");
		if(!sPath.endsWith("\\")) {
			sPath = sPath + "\\";
		}
		Path path = Paths.get(sPath+sFileName);
//		path = Paths.get("D:\\data\\comma_data.dat");
//		path = Paths.get("D:\\data\\rscript\\Smith-Wilson.R");
		StringBuffer sb = new StringBuffer();
		try(BufferedReader rd = Files.newBufferedReader(path,Charset.forName("UTF-8"))){
			String currentLine = null;			
			while((currentLine = rd.readLine())!= null) {
//				System.out.println(currentLine);
				sb.append(currentLine+"\n");
			}
		}catch(IOException iop) {
			iop.printStackTrace();
		}
		
		return sb;
	}*/
	
	static public void folderCheck(String dir) {
		File f = new File(dir);
		if(!f.exists()) {
			f.mkdirs();
		}
	}
	
	public static void writeHeader(Path outPath, String header) {
		try (BufferedWriter writer = Files.newBufferedWriter(outPath))	{
			writer.append(header).append("\n");
			writer.close();
		}
		catch(Exception e)
		{
			System.out.println("writeHeader Error : " + outPath.getFileName() +":" + e);
		}
	}
	
	public static void reset(Path outPath) {
		try (BufferedWriter writer = Files.newBufferedWriter(outPath))	{
			writer.append("");
			writer.close();
		}
		catch(Exception e)
		{
			System.out.println("writeHeader Error : " + outPath.getFileName() +":" + e);
		}
	}
}
