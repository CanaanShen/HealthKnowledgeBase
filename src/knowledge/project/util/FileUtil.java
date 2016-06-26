package knowledge.project.util;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

public class FileUtil {
	
	public static boolean checkduplicate(String outFolderPath, String fileName) {
		boolean duplicate = false;
		File outFolder = new File(outFolderPath);
		for(File file: outFolder.listFiles()) {
			String eachFileName = file.getName().replace(".xml", "");
			String[] splitStr = eachFileName.split("\\s+");
			if(splitStr.length != 2) {
				ExceptionUtility.throwAndCatchException("splitStr length error");
				continue;
			}
			eachFileName = splitStr[1];
			if(eachFileName.equals(fileName)) {
				duplicate = true;
				break;
			}
		}
		return duplicate;
	}
	
	//build an xml file
	public static Document buildDocument(String filePath) {
		
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		Document document = null;
		
		try{
			DocumentBuilder builder = builderFactory.newDocumentBuilder();
			document = builder.parse(new File(filePath));
		} catch(Exception e) {
			System.err.println("xml document building error");
			e.printStackTrace();
			return document;
		}
		
		return document;
	}//
	
	public static void cleanFolder(File folder) {
		for(File file: folder.listFiles()) {
			file.delete();
		}
	}
	
	//output Map to the disk
	public static void writeString(String str, String filePath) {
		
		if(null == str) {
			return;
		}
		try {
			PrintStream printStream = new PrintStream(new BufferedOutputStream(
				new FileOutputStream(filePath))); 
	
			printStream.println(str);
			printStream.flush();
		
			printStream.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}//outputIntMap
	
	//output Map to the disk
	public static void writeSetList(List<Set<String>> strSetList, String filePath) {
		
		try {
			PrintStream printStream = new PrintStream(new BufferedOutputStream(
				new FileOutputStream(filePath))); 
	
			for(int i = 0; i < strSetList.size(); i++) {
				Set<String> setStr = strSetList.get(i);
				printStream.println(setStr);
				printStream.flush();
			}
		
			printStream.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}//outputIntMap
	
	//
	public static List<Set<String>> readStrSetList(String filePath, List<String> strList, String delimiter) {
		List<Set<String>> strSetList = new ArrayList<Set<String>>();
		
		try{
			BufferedReader reader = new BufferedReader(
				new InputStreamReader(new FileInputStream(filePath)));
		
			String readLine = new String("");  
			while((readLine = reader.readLine()) != null){
			   String[] splitStr = readLine.split(delimiter);
			   if(null == splitStr ){
				   System.err.println("spliting error");
				   System.exit(-1);
			   }
			   
			   if(splitStr.length < 2 ){
				   continue;
			   }
			   
			   Set<String> strSet = new HashSet<String>();
			   for(String term: splitStr){
				   term = term.trim();
				   if(!strList.contains(term)) {
					   strList.add(term);
				   }
				   
				   strSet.add(term);
			   }//for...
			   
			   strSetList.add(strSet);
			}//while(...)

			reader.close();	
		}catch(IOException e) {
			e.printStackTrace();
		}	
		return strSetList;
	}
	
	
	//output Map to the disk
	public static void writeListMap(Map<String, List<String>> mapInstance, String filePath)  {
			
		try{
			PrintStream printStream = new PrintStream(new BufferedOutputStream(
					new FileOutputStream(filePath))); 
			
			Iterator<String> iter = mapInstance.keySet().iterator();
			while(iter.hasNext()) {
				String key = iter.next(); 
				List<String> list = mapInstance.get(key);

				printStream.print(key + ", ");
				printStream.flush();
				
				for(int i = 0; i < list.size(); i++) {
					printStream.print(list.get(i) + ", ");
					printStream.flush();
				}
				
				printStream.println();
				printStream.flush();
			}//while...
			
			printStream.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}//outputIntMap
	
	//
	public static void main(String[] args) {
		
		String str = "浙江大学";
		String filePath = "./filetest.txt";
		writeString(str, filePath);
	}
	
}

//Element url = document.createElement("segment");
//rootElement.appendChild(url);
//Text urlText = document.createTextNode("\n 浙江大学 \n");
//url.appendChild(urlText);
//
//TransformerFactory tf = TransformerFactory.newInstance();
//tf.setAttribute("indent-number", 4);
//try {
//	Transformer transformer = tf.newTransformer();
//	
//	DOMSource source = new DOMSource(document);
//	
//    transformer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
//    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
//    transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
//    transformer.setOutputProperty(OutputPropertiesFactory.S_KEY_INDENT_AMOUNT, "2");
//
//	PrintWriter pw = new PrintWriter(new FileOutputStream(outXMLFilePath));
//	StreamResult result = new StreamResult(pw);
//	transformer.transform(source, result);
//} catch(Exception e) {
//	e.printStackTrace();
//}
