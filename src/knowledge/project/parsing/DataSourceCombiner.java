package knowledge.project.parsing;

import knowledge.project.util.CommonUtil;
import knowledge.project.util.ConfigUtil;
import knowledge.project.util.ExceptionUtil;
import knowledge.project.util.FileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Combine the xml files from several data sources
 * @Yueshen
 * */
public class DataSourceCombiner {

	private Map<String, List<String>> existingFoodNameAliasMap = null;
	private Map<String, Integer> foodNameIDMap = null;
	
	private List<String> parsedTagList = null;
	
	//the constructor
	public DataSourceCombiner() {
		this.existingFoodNameAliasMap = new HashMap<String, List<String>>();
		this.foodNameIDMap = new HashMap<String, Integer>();
		this.parsedTagList = new ArrayList<String>();
		
		this.parsedTagList.add("unsuit");
		this.parsedTagList.add("suit");
		this.parsedTagList.add("introduction");
		this.parsedTagList.add("effect");
		this.parsedTagList.add("nutrition");
		this.parsedTagList.add("cuisine");
	}
	
	//	
	private void statExistingFood(String existingFolderPath) {
		
		File existingFolder = new File(existingFolderPath);
		for(File file: existingFolder.listFiles()) {
			
			String fileName = file.getName();
			fileName = fileName.replace(".xml", "");
			String[] splitStr = fileName.split("\\s+");
			if(splitStr.length != 2) {
				ExceptionUtil.throwAndCatchException("The length of splitStr is less than 2");
				continue;
			}
			
			int id = Integer.valueOf(splitStr[0]);
			String foodName = splitStr[1];
			this.foodNameIDMap.put(foodName, id);
			
			String filePath = file.getPath();
			Document document = FileUtil.buildDocument(filePath);		
			Element rootElement = document.getDocumentElement();
			NodeList nodeList = rootElement.getElementsByTagName("alias");
			if(nodeList == null || nodeList.getLength() == 0) {
				ExceptionUtil.throwAndCatchException("nodelist error");
				continue;
			}
			Node aliasTag = nodeList.item(0);
			String aliasStr = aliasTag.getTextContent().trim();
			splitStr = aliasStr.split("�?");
			List<String> aliasList = new ArrayList<String>();
			for(String str: splitStr) {
				aliasList.add(str.trim());
			}
			if(this.existingFoodNameAliasMap.containsKey(foodName)) {
				System.out.println(foodName + id);
			} else {
				this.existingFoodNameAliasMap.put(foodName, aliasList);
			}
		}//for...
		
	}//
	
	//
	public void combineDataSource(String existingFolderPath, String haodouFolderPath, 
			String outFolderPath) {
		File outFolder = new File(outFolderPath);
		if(outFolder.exists()) {
			FileUtil.cleanFolder(outFolder);
			System.out.println("Cleaning is over.");
		}
		if(!outFolder.exists()) {
			outFolder.mkdir();
		}
		
		this.statExistingFood(existingFolderPath);
		int existingFileNum = this.existingFoodNameAliasMap.size();
		int fileNum = existingFileNum;
		System.out.println(fileNum);
		
		File haodouFolder = new File(haodouFolderPath);
		for(File haodouFile: haodouFolder.listFiles()) {
			String haodouFileName = haodouFile.getName();
			haodouFileName = haodouFileName.replace(".xml", "");
			String[] splitStr = haodouFileName.split("\\s+");
			if(splitStr.length != 2) {
				ExceptionUtil.throwAndCatchException("The length of splitStr is less than 2");
				continue;
			}
			
			String haodouFoodName = splitStr[1];
			String outFilePath = null;
			int mark = 0;
			int id = -1;
			String outFoodName = null;
			
			if(this.existingFoodNameAliasMap.containsKey(haodouFoodName)) {		//existing as a typical food
				id = this.foodNameIDMap.get(haodouFoodName);
				outFilePath = outFolderPath + File.separator + id + " " + haodouFoodName + ".xml";
				outFoodName = haodouFoodName;
				mark = 1;
			} else {
				Iterator<String> iter = this.existingFoodNameAliasMap.keySet().iterator();
				while(iter.hasNext()) {
					String foodName_Copy = iter.next();
					List<String> aliasList = this.existingFoodNameAliasMap.get(foodName_Copy);
					for(String alias: aliasList) {
						if(alias.equals(haodouFoodName)) {
							id = this.foodNameIDMap.get(foodName_Copy);
							outFilePath = outFolderPath + File.separator + id + " " + foodName_Copy + ".xml";
							mark = 1;
							outFoodName = foodName_Copy;
							break;
						}
					}//for...
					
					if(mark == 1) {
						break;
					}
				}//while...
			}//if...else...
			
			StringBuffer xmlFileContent = new StringBuffer("");
			if(mark == 1) {				//existing
				if(id == -1 || outFoodName == null) {
					ExceptionUtil.throwAndCatchException("id is -1 or outFoodName is null");
					continue;
				}
				
				if(FileUtil.checkduplicate(outFolderPath, outFoodName)){	//duplicate
					//System.out.println("duplicate: " + id + " " + outFoodName + " " + haodouFoodName);
					outFilePath = null;
					continue;
				}
				
				String existingInFilePath = existingFolderPath + File.separator + id + " " + 
						outFoodName + ".xml";
				Document existingDoc = FileUtil.buildDocument(existingInFilePath);
				Document haodouDoc = FileUtil.buildDocument(haodouFile.getPath());
				xmlFileContent = this.combineExistingFiles(existingDoc, haodouDoc);
			}//if...
			
			if(mark == 0) {				//non-existing
				outFoodName = haodouFoodName;
				outFilePath = outFolderPath + File.separator + fileNum + " " + outFoodName + ".xml";
				Document haodouDoc = FileUtil.buildDocument(haodouFile.getPath());
				xmlFileContent = this.formatHaoDouFile(haodouDoc, fileNum);
				fileNum = fileNum + 1;
			}//if...
			
			if(outFilePath == null) {				//do nothing
			} else {
				FileUtil.writeString(xmlFileContent.toString(), outFilePath);
			}
		}//for...
		
		File existingFolder = new File(existingFolderPath);
		for(File existingFile : existingFolder.listFiles()) {
			String fileName = existingFile.getName();
			String outFilePath = outFolderPath + File.separator + fileName;
			File outFile = new File(outFilePath);
			
			if(!outFile.exists()) {				//existing files but not in Haodou folder
				StringBuffer xmlFileContent = new StringBuffer("");
				//System.out.println(existingFile.getPath());
				Document existingDoc = FileUtil.buildDocument(existingFile.getPath());
				xmlFileContent = this.formatExistingFiles(existingDoc);
				FileUtil.writeString(xmlFileContent.toString(), outFilePath);
			}//if...
		}//for...
		
	}//
	
	//modify the existing files in standard format
	private StringBuffer formatExistingFiles(Document existingDoc) {
		StringBuffer xmlFileContent = new StringBuffer("");
		Element rootElement = existingDoc.getDocumentElement();
		NodeList nodeList = rootElement.getChildNodes();
		
		xmlFileContent.append("<" + rootElement.getNodeName() + ">\n\n");
		
		for(int i = 0; i < nodeList.getLength(); i++) {
			Node subNode = nodeList.item(i);
			String nodeName = subNode.getNodeName();
			
			if(nodeName.contains("text")) {				//#text
				continue;
			}
						
			StringBuffer tagPlusContent = new StringBuffer("");
			if(this.parsedTagList.contains(nodeName)) {				//nodes to be parsed
				tagPlusContent = this.keepComplexNode(subNode);
			} else {												//nodes not to be parsed
				tagPlusContent = this.keepTrivialNode(subNode);
			}
			
			xmlFileContent.append(tagPlusContent);
		}//for i...
		
		xmlFileContent.append("</" + rootElement.getNodeName() + ">");
		return xmlFileContent;
	}
	
	//combine the existing files 
	private StringBuffer combineExistingFiles(Document existingDoc, Document haodouDoc) {
		StringBuffer xmlFileContent = new StringBuffer("");
		Element existingRootElement = existingDoc.getDocumentElement();
		Element haodouRootElement = haodouDoc.getDocumentElement();
		
		NodeList existingNodeList = existingRootElement.getChildNodes();
		xmlFileContent.append("<" + existingRootElement.getNodeName() + ">\n\n");
		for(int i = 0; i < existingNodeList.getLength(); i++) {
			Node existingSubNode = existingNodeList.item(i);
			String nodeName = existingSubNode.getNodeName();
			
			if(nodeName.contains("text")) {					//#text
				continue;
			}
			
			NodeList haodouNodeList = haodouRootElement.getElementsByTagName(nodeName);
			if(haodouNodeList == null || haodouNodeList.getLength() == 0) {
				ExceptionUtil.throwAndCatchException("nodelist error");
				continue;
			}
			Node haodouNode = haodouNodeList.item(0);
			
			StringBuffer tagPlusContent = new StringBuffer("");
			if(this.parsedTagList.contains(nodeName)) {				//nodes to be parsed
				tagPlusContent = this.generateComplexNode(existingSubNode, haodouNode, haodouRootElement);
			} else {												//nodes not to be parsed
				tagPlusContent = this.generateTrivialNode(existingSubNode, haodouNode, haodouRootElement);
			}
			
			xmlFileContent.append(tagPlusContent);
		}//for i...
		
		xmlFileContent.append("</" + existingRootElement.getNodeName() + ">");
		return xmlFileContent;
	}//

	//improve the xml files of haodou with standard format
	private StringBuffer formatHaoDouFile(Document haodouDoc, int id) {
		StringBuffer xmlFileContent = new StringBuffer("");
		Element haodouRootElement = haodouDoc.getDocumentElement();
		NodeList haodouNodeList = haodouRootElement.getChildNodes();
		
		xmlFileContent.append("<" + haodouRootElement.getNodeName() + ">\n\n");
		xmlFileContent.append(ConfigUtil.First_Indent + "<id>\n" + ConfigUtil.Second_Indent + id + "\n");
		xmlFileContent.append(ConfigUtil.First_Indent + "</id>\n\n");
		
		for(int i = 0; i < haodouNodeList.getLength(); i++) {
			Node haodouSubNode = haodouNodeList.item(i);
			String nodeName = haodouSubNode.getNodeName();
			
			if(nodeName.contains("text") || nodeName.equals("id")) {	//#text
				continue;
			}
			
			StringBuffer tagPlusContent = new StringBuffer("");												//nodes not to be parsed
			tagPlusContent = this.generateTrivialNode(haodouSubNode);
			
			xmlFileContent.append(tagPlusContent);
		}//for i...
		
		xmlFileContent.append("</" + haodouRootElement.getNodeName() + ">");
		return xmlFileContent;
	}
	
	//generate a trivial node
	private StringBuffer generateTrivialNode(Node existingTrivialNode, Node newTrivialNode, 
			Element newRootElement) {
		StringBuffer tagPlusContent = new StringBuffer("");
			
		String nodeName = existingTrivialNode.getNodeName();
		if(!nodeName.equals("ingredient")) {
			String existingStr = existingTrivialNode.getTextContent().trim();
			String newStr = newTrivialNode.getTextContent().trim();
			tagPlusContent.append(ConfigUtil.First_Indent + "<" + nodeName + ">\n");

			if(existingStr != null && existingStr.length() > 0) {
				tagPlusContent.append(ConfigUtil.Second_Indent + existingStr + "\n");
			} else {
				if(newStr != null && newStr.length() > 0) {
					tagPlusContent.append(ConfigUtil.Second_Indent + newStr + "\n");
				}else{
					tagPlusContent.append("\n");
				}
			}
			tagPlusContent.append(ConfigUtil.First_Indent + "</" + nodeName + ">\n\n");
			
			if(nodeName.equals("alias")) {						//<season>
				tagPlusContent.append(ConfigUtil.First_Indent + "<season>\n");
				NodeList seasonNodeList = newRootElement.getElementsByTagName("season");
				if(seasonNodeList == null || seasonNodeList.getLength() == 0) {
					ExceptionUtil.throwAndCatchException("nodelist error");
				}
				Node seasonNode = seasonNodeList.item(0);
				String seasonText = seasonNode.getTextContent().trim();
				tagPlusContent.append(ConfigUtil.Second_Indent + seasonText + "\n");
				tagPlusContent.append(ConfigUtil.First_Indent + "</season>\n\n");
			}
		}//if ! ingredient

		if(nodeName.equals("ingredient")) {
			tagPlusContent.append(ConfigUtil.First_Indent + "<" + nodeName + ">\n");
				
			NodeList existingElementNodeList = existingTrivialNode.getChildNodes();
			NodeList newElementNodeList = newTrivialNode.getChildNodes();
			NodeList elementNodeList = null;
			if(CommonUtil.countChildNodeNumber(existingElementNodeList) > 0) {
				elementNodeList = existingElementNodeList;
			} else {
				if(CommonUtil.countChildNodeNumber(newElementNodeList) > 0) {
					elementNodeList = newElementNodeList;
				}
			}
				
			if(elementNodeList != null) {
				for(int i = 0; i < elementNodeList.getLength(); i++) {
					Node elementNode = elementNodeList.item(i);
					String elementNodeName = elementNode.getNodeName();
					if(elementNodeName.contains("text")) {
						continue;
					}
					tagPlusContent.append(ConfigUtil.Second_Indent + "<" + elementNodeName + ">\n");
					
					NodeList elenamevalueNodeList = elementNode.getChildNodes();
					for(int j = 0; j < elenamevalueNodeList.getLength(); j++) {
						Node elenamevalueNode = elenamevalueNodeList.item(j);
						String elename = elenamevalueNode.getNodeName();
						String elevalue = elenamevalueNode.getTextContent().trim();
							
						if(elevalue != null && elevalue.length() > 0) {
							tagPlusContent.append(ConfigUtil.Third_Indent + "<" + elename + ">\n");
							tagPlusContent.append(ConfigUtil.Fourth_Indent + elevalue + "\n");
							tagPlusContent.append(ConfigUtil.Third_Indent + "</" + elename + ">\n");
						}
					}
					
					tagPlusContent.append(ConfigUtil.Second_Indent + "</" + elementNodeName + ">\n");
				}//for...
			} else {
				tagPlusContent.append("\n");
			}

			tagPlusContent.append(ConfigUtil.First_Indent + "</" + nodeName + ">\n\n");
		}// if ingredient
		
		return tagPlusContent;
	}//
	
	//generate a complex node
	private StringBuffer generateComplexNode(Node existingComplexNode, Node newComplexNode, 
			Element newRootElement) {
		StringBuffer tagPlusContent = new StringBuffer("");
		NodeList existingComplexSubNodeList = existingComplexNode.getChildNodes();
		String nodeName = existingComplexNode.getNodeName();
		int mark = 0;
		if(existingComplexSubNodeList == null || existingComplexSubNodeList.getLength() == 0) {
			mark = 0;
		} else {
			for(int i = 0; i < existingComplexSubNodeList.getLength(); i++) {
				Node  existingComplexSubNode = existingComplexSubNodeList.item(i);
				if(existingComplexSubNode.getNodeName().equals("rawdata")){
					mark = 1;
					break;
				}
			}
		}
		
		if(mark == 1) {
			tagPlusContent.append(ConfigUtil.First_Indent + "<" + nodeName + ">\n");
			int subNodeListLength = existingComplexSubNodeList.getLength();
			for(int i = 0; i < subNodeListLength; i++) {
				Node existingComplexSubNode = existingComplexSubNodeList.item(i);				
				String subNodeName = existingComplexSubNode.getNodeName();
				if(subNodeName.contains("text")) {
					continue;
				}
				
				String subText = existingComplexSubNode.getTextContent();
				tagPlusContent.append(ConfigUtil.Second_Indent + "<" + subNodeName + ">\n");
				String formatedSubText = CommonUtil.formatedText(subText, ConfigUtil.Third_Indent);
				tagPlusContent.append(formatedSubText + "\n");
				if(subNodeName.equals("tag")) {
					tagPlusContent.append(ConfigUtil.Second_Indent + "</" + subNodeName + ">\n");
				}else{
					tagPlusContent.append(ConfigUtil.Second_Indent + "</" + subNodeName + ">\n\n");
				}
			}//for...
			
			tagPlusContent.append(ConfigUtil.First_Indent + "</" + nodeName + ">\n\n");
		}
		
		if(mark == 0) {
			tagPlusContent.append(ConfigUtil.First_Indent + "<" + nodeName + ">\n");

			String subText = newComplexNode.getTextContent().trim();
			if(subText == null || subText.length() <= 1) {
				tagPlusContent.append("\n");
			}else{
				String formatedText = CommonUtil.formatedText(subText, ConfigUtil.Second_Indent);
				tagPlusContent.append(formatedText + "\n");
			}
			
			tagPlusContent.append(ConfigUtil.First_Indent + "</" + nodeName + ">\n\n");
		}
		
		if(nodeName.equals("effect")) {
			tagPlusContent.append(ConfigUtil.First_Indent + "<manuleffecttag>\n");
			NodeList manulTagList = newRootElement.getElementsByTagName("manultag");
			if(manulTagList == null || manulTagList.getLength() == 0) {
				ExceptionUtil.throwAndCatchException("manulTagList errors");
				tagPlusContent.append("\n");
			}
			String manulEffectText = manulTagList.item(0).getTextContent().trim();
			String[] manulSplitStr = manulEffectText.split("�?");
			tagPlusContent.append(ConfigUtil.Second_Indent);
			for(String manulTag: manulSplitStr){
				tagPlusContent.append(manulTag + "  ");
			}
			tagPlusContent.append("\n");
			tagPlusContent.append(ConfigUtil.First_Indent + "</manuleffecttag>\n\n");
		}
		
		return tagPlusContent;
	}//
	
	//generate a trivial node
	private StringBuffer generateTrivialNode(Node node) {
		StringBuffer tagPlusContent = new StringBuffer("");
		String nodeName = node.getNodeName();
		String text = node.getTextContent().trim();
		
		if(nodeName.equals("manultag")) {
			String[] manulSplitStr = text.replace("\n", "  ").split("�?");
			tagPlusContent.append(ConfigUtil.First_Indent + "<manuleffecttag>\n");
			tagPlusContent.append(ConfigUtil.Second_Indent);
			for(String manulTag: manulSplitStr){
				tagPlusContent.append(manulTag.trim() + "  ");
			}

			tagPlusContent.append("\n");
			tagPlusContent.append(ConfigUtil.First_Indent + "</manuleffecttag>\n\n");

		} else {
			if(nodeName.equals("ingredient")) {
				
				tagPlusContent.append(ConfigUtil.First_Indent + "<" + nodeName + ">\n");
				NodeList elementNodeList = node.getChildNodes();
				int mark = 0;
				if(elementNodeList != null && elementNodeList.getLength() > 0) {
					for(int i = 0; i < elementNodeList.getLength(); i++) {
						Node elementNode = elementNodeList.item(i);
						String elementNodeName = elementNode.getNodeName();
						if(elementNodeName.contains("text")) {
							continue;
						}
						tagPlusContent.append(ConfigUtil.Second_Indent + "<" + elementNodeName + ">\n");
						mark = 1;
						
						NodeList elenamevalueNodeList = elementNode.getChildNodes();
						for(int j = 0; j < elenamevalueNodeList.getLength(); j++) {
							Node elenamevalueNode = elenamevalueNodeList.item(j);
							String elename = elenamevalueNode.getNodeName();
							String elevalue = elenamevalueNode.getTextContent().trim();
								
							if(elevalue != null && elevalue.length() > 0) {
								tagPlusContent.append(ConfigUtil.Third_Indent + "<" + elename + ">\n");
								tagPlusContent.append(ConfigUtil.Fourth_Indent + elevalue + "\n");
								tagPlusContent.append(ConfigUtil.Third_Indent + "</" + elename + ">\n");
							}
						}
						
						tagPlusContent.append(ConfigUtil.Second_Indent + "</" + elementNodeName + ">\n");
					}//for...
				}
				
				if(mark == 0) {
					tagPlusContent.append("\n");
				}
				
				tagPlusContent.append(ConfigUtil.First_Indent + "</" + nodeName + ">\n\n");
			} else{
				String formatedText = CommonUtil.formatedText(text, ConfigUtil.Second_Indent);
				tagPlusContent.append(ConfigUtil.First_Indent + "<" + nodeName + ">\n");
				tagPlusContent.append(formatedText + "\n");
				tagPlusContent.append(ConfigUtil.First_Indent + "</" + nodeName + ">\n\n");
			}
		}
		
		return tagPlusContent;
	}//
	
	//generate a trivial node
	private StringBuffer keepTrivialNode(Node subNode) {
		StringBuffer tagPlusContent = new StringBuffer("");
		String nodeName = subNode.getNodeName();
		String text = subNode.getTextContent().trim();
		
		if(nodeName.equals("ingredient")) {
				
			tagPlusContent.append(ConfigUtil.First_Indent + "<" + nodeName + ">\n");
			NodeList elementNodeList = subNode.getChildNodes();
			int mark = 0;
			if(elementNodeList != null && elementNodeList.getLength() > 0) {
				for(int i = 0; i < elementNodeList.getLength(); i++) {
					Node elementNode = elementNodeList.item(i);
					String elementNodeName = elementNode.getNodeName();
					if(elementNodeName.contains("text")) {
						continue;
					}
					tagPlusContent.append(ConfigUtil.Second_Indent + "<" + elementNodeName + ">\n");
					mark = 1;
						
					NodeList elenamevalueNodeList = elementNode.getChildNodes();
					for(int j = 0; j < elenamevalueNodeList.getLength(); j++) {
						Node elenamevalueNode = elenamevalueNodeList.item(j);
						String elename = elenamevalueNode.getNodeName();
						String elevalue = elenamevalueNode.getTextContent().trim();
								
						if(elevalue != null && elevalue.length() > 0) {
							tagPlusContent.append(ConfigUtil.Third_Indent + "<" + elename + ">\n");
							tagPlusContent.append(ConfigUtil.Fourth_Indent + elevalue + "\n");
							tagPlusContent.append(ConfigUtil.Third_Indent + "</" + elename + ">\n");
						}
					}
						
					tagPlusContent.append(ConfigUtil.Second_Indent + "</" + elementNodeName + ">\n");
				}//for...
			}
				
			if(mark == 0) {
				tagPlusContent.append("\n");
			}
				
			tagPlusContent.append(ConfigUtil.First_Indent + "</" + nodeName + ">\n\n");
		} else{
			String formatedText = CommonUtil.formatedText(text, ConfigUtil.Second_Indent);
			tagPlusContent.append(ConfigUtil.First_Indent + "<" + nodeName + ">\n");
			tagPlusContent.append(formatedText + "\n");
			tagPlusContent.append(ConfigUtil.First_Indent + "</" + nodeName + ">\n\n");
		}
		
		if(nodeName.equals("alias")) {						//<season>
			tagPlusContent.append(ConfigUtil.First_Indent + "<season>\n");
			tagPlusContent.append("\n");
			tagPlusContent.append(ConfigUtil.First_Indent + "</season>\n\n");
		}

		return tagPlusContent;
	}//
	
	//generate a trivial node
	private StringBuffer keepComplexNode(Node node) {
		StringBuffer tagPlusContent = new StringBuffer("");
		String nodeName = node.getNodeName();
		
		tagPlusContent.append(ConfigUtil.First_Indent + "<" + nodeName + ">\n");
		NodeList subNodeList = node.getChildNodes();
		int subNodeListLength = subNodeList.getLength();
		int mark = 0;
		for(int i = 0; i < subNodeListLength; i++) {
			Node subNode = subNodeList.item(i);				
			String subNodeName = subNode.getNodeName();
			if(subNodeName.contains("text")) {
				continue;
			}
			mark = 1;
			String subText = subNode.getTextContent();
			tagPlusContent.append(ConfigUtil.Second_Indent + "<" + subNodeName + ">\n");
			String formatedSubText = CommonUtil.formatedText(subText, ConfigUtil.Third_Indent);
			tagPlusContent.append(formatedSubText + "\n");
			if(subNodeName.equals("tag")) {
				tagPlusContent.append(ConfigUtil.Second_Indent + "</" + subNodeName + ">\n");
			}else{
				tagPlusContent.append(ConfigUtil.Second_Indent + "</" + subNodeName + ">\n\n");
			}
		}//for...
		
		if(mark == 0) {
			tagPlusContent.append("\n");
		}
		
		tagPlusContent.append(ConfigUtil.First_Indent + "</" + nodeName + ">\n\n");
		
		if(nodeName.equals("effect")) {
			tagPlusContent.append(ConfigUtil.First_Indent + "<manuleffecttag>\n");
			tagPlusContent.append("\n");
			tagPlusContent.append(ConfigUtil.First_Indent + "</manuleffecttag>\n\n");
		}
		
		return tagPlusContent;
	}//
	
	//main function
	public static void main(String[] args) {
		String rootFolderPath = "C:\\Users\\Yueshen\\git\\ProjectRetrieval\\data";

		String existingFolderPath = rootFolderPath + File.separator + "xianghadougouparsed" + File.separator; 
		String haodouFolderPath = rootFolderPath + File.separator + "haodouXML" + File.separator;
		String outFolderPath = rootFolderPath + File.separator + "xianghadougouhaodou" + File.separator;
		
		DataSourceCombiner dataSourceCombiner = new DataSourceCombiner();
		dataSourceCombiner.combineDataSource(existingFolderPath, haodouFolderPath, outFolderPath);
		
		System.out.println("Program ends");
	}//main
}//
