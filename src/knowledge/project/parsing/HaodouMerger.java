package knowledge.project.parsing;

import knowledge.project.util.ExceptionUtil;
import knowledge.project.util.FileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Merge the xml files of Haodou website from several data sources
 * @Yueshen
 * */

public class HaodouMerger {
	
	//
	public void mergeHaodou(String haodouFolderPath) {
		
		File haodouFolder = new File(haodouFolderPath);
		List<String> nameList = new ArrayList<String>();
		for(File file: haodouFolder.listFiles()) {
			String filePath = file.getPath();
			
			Document doc = FileUtil.buildDocument(filePath);
			
			NodeList idList = doc.getElementsByTagName("id");
			if(idList == null || idList.getLength() == 0) {
				ExceptionUtil.throwAndCatchException("idList error");
				continue;
			}
			Node idNode = idList.item(0);
			String id = idNode.getTextContent().trim();
			
			NodeList foodNameList = doc.getElementsByTagName("foodname");
			if(foodNameList == null || foodNameList.getLength() == 0) {
				ExceptionUtil.throwAndCatchException("foodNameList error");
				continue;
			}
			Node foodNode = foodNameList.item(0);
			String foodName = foodNode.getTextContent().trim();
			int mark = 0;
			if(nameList.contains(foodName)) {
				System.out.println("foodName: " + foodName + " " + id + " " );
				mark = 1;
				continue;
			}
			
			nameList.add(foodName);
			
			NodeList aliasNodeList = doc.getElementsByTagName("alias");
			if(aliasNodeList == null || aliasNodeList.getLength() == 0) {
				ExceptionUtil.throwAndCatchException("aliasNodeList error");
				continue;
			}
			
			Node aliasNode = aliasNodeList.item(0);
			String[] splitStr = aliasNode.getTextContent().trim().split("、");
			if(splitStr != null && splitStr.length > 0) {
				for(String alias: splitStr) {
					alias = alias.trim();
					if(alias.length() > 0) {
						if(nameList.contains(alias)) {
							System.out.print("id: " + foodName + " " + id + " " + alias + " ");
							mark = 1;
							continue;
						}
						nameList.add(alias);
					}//if...
				}//for...
			}//if...
			
			if(mark == 1) {
				System.out.println();
			}
		}//for...
	}//
	
	//main function
	public static void main(String[] args) {
		
		String rootFolderPath = "C:\\Users\\Yueshen\\git\\ProjectRetrieval\\data";
		String haodouFolderPath = rootFolderPath + File.separator + "haodouXML" + File.separator;
		
		HaodouMerger haodouMerger = new HaodouMerger();
		haodouMerger.mergeHaodou(haodouFolderPath);
		System.out.println("Program ends");
		
	}//main
}
