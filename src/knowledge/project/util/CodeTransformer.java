package knowledge.project.util;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;

public class CodeTransformer {
	
	   public static String getEncoding(String str) {  
	        String encode = "GB2312";  
	        try {  
	            if (str.equals(new String(str.getBytes(encode), encode))) {  
	                String s = encode;  
	                return s;  
	            }  
	        } catch (Exception exception) {  
	        }  
	        encode = "ISO-8859-1";  
	        try {  
	            if (str.equals(new String(str.getBytes(encode), encode))) {  
	                String s1 = encode;  
	                return s1;  
	            }  
	        } catch (Exception exception1) {  
	        }  
	        encode = "UTF-8";  
	        try {  
	            if (str.equals(new String(str.getBytes(encode), encode))) {  
	                String s2 = encode;  
	                return s2;  
	            }  
	        } catch (Exception exception2) {  
	        }  
	        encode = "GBK";  
	        try {  
	            if (str.equals(new String(str.getBytes(encode), encode))) {  
	                String s3 = encode;  
	                return s3;  
	            }  
	        } catch (Exception exception3) {  
	        }  
	        return "";  
	    }  
	
	//
	private void transformCode(String inFolderPath, String outFolderPath) {
		
		File inFolder = new File(inFolderPath);
		File[] fileList = inFolder.listFiles();
		for(File file: fileList) {
			String fileName = file.getName();
			String filePath = file.getPath();
			try{
				BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(new FileInputStream(filePath), "GBK"));
				
				String outFilePath = outFolderPath + File.separator + fileName;
				PrintStream printStream = new PrintStream(new BufferedOutputStream(
						new FileOutputStream(outFilePath))); 
				
				String readLine = new String("");
				while((readLine = bufferedReader.readLine()) != null) {
					printStream.println(readLine);
					System.out.println(getEncoding(readLine));
					printStream.flush();
				}//while(...)
			
				bufferedReader.close();
				printStream.close();
			}
			catch(Exception e) {
				e.printStackTrace();
			}

		}
		
	}
	
	public static void main(String[] args) {
		
		CodeTransformer codeTransformer = new CodeTransformer();
		
		String rootFolderPath = "C:\\Users\\Yueshen\\git";
		
		String inFolderPath = rootFolderPath + File.separator + "ProjectCrawler\\data\\xiangha\\foodtextbase"; 
		String outFolderPath = rootFolderPath + File.separator + "ProjectRetrieval\\data\\xiangha\\foodtextbaseutf8";
		
		codeTransformer.transformCode(inFolderPath, outFolderPath);
		
		System.out.println("Program ends");
	}
	
}
