package knowledge.project.parsing;

import healthtextbase.project.baseclass.XMLTagger;
import healthtextbase.project.type.DependencyTuple;
import healthtextbase.project.util.ConfigUtil;
import healthtextbase.project.util.ExceptionUtility;
import healthtextbase.project.util.FileUtil;
import healthtextbase.project.util.TaggingUtil;

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
 * Generate tags for each xml file in a full version style
 * @Yueshen
 * */

public class XMLFullTagger extends XMLTagger{
	
	private List<String> tobeTaggedList = null;
	
	private List<String> unsuitAndSuitPOSStrList = null;
	private List<String> unsuitAndSuitBlankSuffixList = null;
	private List<String> unsuitAndSuitBlankPrefixList = null;
	private List<String> unsuitAndSuitBlankExcludeList = null;
	
	private List<String> concernedTypeList = null;
	private List<String> containedExcludeList = null;
	private List<String> equalExcludedList = null;
	
	public XMLFullTagger() {
		
		this.tobeTaggedList = new ArrayList<String>();
		this.tobeTaggedList.add("unsuit");
		this.tobeTaggedList.add("suit");
		this.tobeTaggedList.add("introduction");
		this.tobeTaggedList.add("effect");
		this.tobeTaggedList.add("nutrition");
		this.tobeTaggedList.add("cuisine");
		
		this.unsuitAndSuitPOSStrList = new ArrayList<String>();
		this.unsuitAndSuitBlankSuffixList = new ArrayList<String>();
		this.unsuitAndSuitBlankPrefixList = new ArrayList<String>();
		this.unsuitAndSuitBlankExcludeList = new ArrayList<String>();
		
		this.unsuitAndSuitPOSStrList = ConfigUtil.unsuitAndSuitPOSStrList;
		
		this.unsuitAndSuitBlankSuffixList = ConfigUtil.unsuitAndSuitBlankSuffixList;
		
		this.unsuitAndSuitBlankPrefixList = ConfigUtil.unsuitAndSuitBlankPrefixList;
		
		this.unsuitAndSuitBlankExcludeList = ConfigUtil.unsuitAndSuitBlankExcludeList;

		this.concernedTypeList = new ArrayList<String>();
		this.concernedTypeList = ConfigUtil.concernedTypeList;

		
		this.containedExcludeList = new ArrayList<String>();
		this.containedExcludeList = ConfigUtil.containedExcludedList;
		
		this.equalExcludedList = new ArrayList<String>();
		this.equalExcludedList = ConfigUtil.equalExcludedList;

	}
	
	//generate the unchanged node
	private StringBuffer toReserveFullVersionNode(Element rootElement, Node node) {
		
		String nodeName = node.getNodeName();
		StringBuffer tagPlusContent = new StringBuffer("");
		if(!nodeName.equals("ingredient")) {
			
			String text = node.getTextContent();
			if(nodeName.equals("manuleffecttag")) {
				if(text.contains("ï¼?")){
					text = text.replace("ï¼?", "  ");
				}
			}
			tagPlusContent.append(ConfigUtil.First_Indent + "<" + nodeName + ">\n");
			tagPlusContent.append(ConfigUtil.Second_Indent + text.trim() + "\n");
			tagPlusContent.append(ConfigUtil.First_Indent + "</" + nodeName + ">\n\n");
			
		} else{
			tagPlusContent.append(ConfigUtil.First_Indent + "<" + nodeName + ">\n");
			
			NodeList subNodeList = node.getChildNodes();
			if(subNodeList.getLength() > 0) {
			
				for(int i = 0; i < subNodeList.getLength(); i++) {
					Node subNode = subNodeList.item(i);
					String subNodeName = subNode.getNodeName();
					if(subNodeName.contains("text")) {
						continue;
					}
					
					tagPlusContent.append(ConfigUtil.Second_Indent + "<" + subNodeName + ">\n");
				
					NodeList subSubNodeList = subNode.getChildNodes();
					for(int j = 0; j < subSubNodeList.getLength(); j++) {
						Node subSubNode = subSubNodeList.item(j);
						String subSubNodeName = subSubNode.getNodeName();
						if(subSubNodeName.contains("text")) {
							continue;
						}
						
						String subSubText = subSubNode.getTextContent();
						
						if(subSubText != null && subSubText.length() > 0) {
							tagPlusContent.append(ConfigUtil.Third_Indent + "<" + subSubNodeName + ">\n");
							tagPlusContent.append(ConfigUtil.Fourth_Indent + subSubText.trim() + "\n");
							tagPlusContent.append(ConfigUtil.Third_Indent + "</" + subSubNodeName + ">\n");
						}
					}
				
					tagPlusContent.append(ConfigUtil.Second_Indent + "</" + subNodeName + ">\n");
				}//for...
			} else {
				tagPlusContent.append("\n");
			}
					
			tagPlusContent.append(ConfigUtil.First_Indent + "</" + nodeName + ">\n\n");
		}
		
		return tagPlusContent;
	}
	
	//for those nodes that need to be parsed
	private StringBuffer toTagNodeFullVersion(Element rootElement, Node node) {
		
		String nodeName = node.getNodeName();
		if(nodeName.contains("text")) {
			return new StringBuffer("");
		}
		
		NodeList subNodeList = node.getChildNodes();
		
		StringBuffer tagPlusContent = new StringBuffer(ConfigUtil.First_Indent + "<" + nodeName + ">\n");
		
		if(subNodeList == null || subNodeList.getLength() <= 2) {	//magic number #text
			tagPlusContent.append("\n");
		} else {
			
			String foodName = rootElement.getElementsByTagName("foodname").item(0).getTextContent().trim();
			
			String rawDataText = null;
			String posText = null;
			String parsingText = null;
			int subNodeCount = subNodeList.getLength();
			for(int i = 0; i < subNodeCount; i++) {
				Node subNode = subNodeList.item(i);
				String subNodeName = subNode.getNodeName();
				
				if(subNodeName.contains("text") || subNodeName.equals("tag")) {		//#text
					continue;
				}
				
				if(subNodeName.equals("rawdata")) {			//<rawdata>
					rawDataText = subNode.getTextContent();
				}
				if(subNodeName.equals("pos")) {				//<pos>
					posText = subNode.getTextContent();
				}
				
				if(subNodeName.equals("parsing")) {			//<parsing>
					parsingText = subNode.getTextContent();
				}
				
				tagPlusContent.append(ConfigUtil.Second_Indent + "<" + subNodeName + ">");
				tagPlusContent.append(subNode.getTextContent());
				tagPlusContent.append("</" + subNodeName + ">\n\n");
			}//i...

			tagPlusContent.append(ConfigUtil.Second_Indent + "<tag>\n" );
			if(rawDataText == null || posText == null) {
				ExceptionUtility.throwAndCatchException("rawDataText or posText is null ");
			} else {
			
				if(nodeName.equals("unsuit") || nodeName.equals("suit")) {
					tagPlusContent.append(ConfigUtil.Third_Indent + 
							this.toTagSuitAndUnsuit(foodName, rawDataText, posText) + "\n");
				} else {
					tagPlusContent.append(ConfigUtil.Third_Indent + 
							this.toTagOther(foodName, parsingText) + "\n");
				}//if...else...
			}//if...else...
			
			tagPlusContent.append(ConfigUtil.Second_Indent + "</tag>\n");
			
		}//if...else...
		
		tagPlusContent.append(ConfigUtil.First_Indent + "</" + nodeName + ">\n\n");

		return tagPlusContent;
	}
	
	//
	private void tagXMLFileFullVersion(String xmlFilePath, String outXMLFilePath) {

		Document document = FileUtil.buildDocument(xmlFilePath);		
		if(document == null) {
			System.out.println("The document is null");
			System.out.println(xmlFilePath);
			System.exit(-1);
			return;
		}		
		Element rootElement = document.getDocumentElement();
			
		if(rootElement == null) {
			System.out.println("The rootElement is null");
			return;
				
		}else {
			StringBuffer xmlFileContent = new StringBuffer("");
			xmlFileContent.append("<" + rootElement.getNodeName() + ">\n\n");
				
			NodeList nodeList = rootElement.getChildNodes();
			int iterCount = nodeList.getLength();

			for(int i = 0; i < iterCount; i++) {
				Node subNode = nodeList.item(i);
				String nodeName = subNode.getNodeName();
					
				if(nodeName.contains("text")) {
					continue;
				}
					
				StringBuffer tagPlusContent = new StringBuffer("");
				if(this.tobeTaggedList.contains(nodeName)) {				//nodes to be parsed
					tagPlusContent = this.toTagNodeFullVersion(rootElement, subNode);
				} else {												//nodes not to be parsed
					tagPlusContent = this.toReserveFullVersionNode(rootElement, subNode);
				}
					
				xmlFileContent.append(tagPlusContent);
			}//for...

			xmlFileContent.append("</" + rootElement.getNodeName() + ">");
			FileUtil.writeString(xmlFileContent.toString(), outXMLFilePath);
		}//if...else...
	}
	
	//tag the XML foler
	public void tagXMLFolder(String inFolderPath, String outFolderPath_FullVersion) {
		
		File outFolder = new File(outFolderPath_FullVersion);
		if(!outFolder.exists()) {
			outFolder.mkdir();
		}
		
		File inFolder = new File(inFolderPath);
		File[] xmlFileList = inFolder.listFiles();
		for(File xmlFile : xmlFileList) {
			
			String xmlFilePath = xmlFile.getPath();
			String xmlFileName = xmlFile.getName();
			
			String outXMLFilePath = outFolderPath_FullVersion + File.separator + xmlFileName;
			System.out.println(outXMLFilePath);
			
			tagXMLFileFullVersion(xmlFilePath, outXMLFilePath);
		}//for
		
	}//
	
	//main function
	public static void main(String[] args) {
		
		String rootFolderPath = "C:\\Users\\Yueshen\\git\\ProjectRetrieval\\data\\xianghadougouhaodou";

		String inFolderPath = rootFolderPath + File.separator + "foodtextbaseparsed" + File.separator; 
		String outFolderPath_FullVersion = rootFolderPath + File.separator + "foodtextbasetaggedfullversion" 
										+ File.separator;
		
		XMLFullTagger xmlTagger = new XMLFullTagger();
		xmlTagger.tagXMLFolder(inFolderPath, outFolderPath_FullVersion);
		
		System.out.println("Program ends");
		
	}//
	
	//
	private StringBuffer toTagSuitAndUnsuit(String foodName, String rawText, String posText) {
		StringBuffer taggedText = new StringBuffer("");
		
		String[] splitPOS = posText.trim().split("\\s+");
		Map<String, String> termPOSMap = new HashMap<String, String>();
		for(String str: splitPOS) {
			String[] termPOSPair = str.split("#");
			if(termPOSPair.length != 2) {
				ExceptionUtility.throwAndCatchException("The length of ... is less than 2");
				continue;
			}
			termPOSMap.put(termPOSPair[0], termPOSPair[1]);
		}
		
		Iterator<String> iter = termPOSMap.keySet().iterator();
		while(iter.hasNext()) {
			String term = iter.next();
			String pos = termPOSMap.get(term);
			if(this.unsuitAndSuitPOSStrList.contains(pos)) {
				rawText = rawText.replace(term, "  ");
			}
			
			if(this.unsuitAndSuitBlankSuffixList.contains(term)) {
				rawText = rawText.replace(term, term + "  ");
			}
			
			if(this.unsuitAndSuitBlankPrefixList.contains(term)) {
				rawText = rawText.replace(term, "  " + term);
			}
			
			if(this.unsuitAndSuitBlankExcludeList.contains(term)) {
				rawText = rawText.replace(term, "  ");
			}
			
		}
		
		rawText = rawText.replace(foodName, "  ");
		
		String[] splitRawText = rawText.split("\\s+");
		for(String str: splitRawText) {
			if(str.length() >= 2) {
				
				if(!this.unsuitAndSuitBlankExcludeList.contains(str)) {
					taggedText.append(str + "  ");
				}
			}
		}
		
		return taggedText;
	}
	
	//to generate other tags
	private StringBuffer toTagOther(String foodName, String parsingText) {
				
		String[] splitParsingText = parsingText.split("\\(");
		List<DependencyTuple> dependencyTupleList = new ArrayList<DependencyTuple>();
		
		for(String splitStr: splitParsingText) {
			
			DependencyTuple dependencyTuple = TaggingUtil.getDependencyTuple(splitStr, 
					this.concernedTypeList);
			if(dependencyTuple != null) {
				dependencyTupleList.add(dependencyTuple);
			}
		}//for...
		
		StringBuffer taggedText = new StringBuffer("  ");
		List<String> singleTermList = new ArrayList<String>();
		for(DependencyTuple dependencyTuple: dependencyTupleList) {
			TaggingUtil.generateOneTaggedTerm(taggedText, dependencyTuple, singleTermList, foodName, 
					this.containedExcludeList, this.equalExcludedList);
		}//for...
		
		String taggedTextStr = taggedText.toString();
		for(String singleTerm : singleTermList) {
			String taggedTextTemp = taggedTextStr.replace(" " + singleTerm + " ", "  ");
			if(taggedTextTemp.contains(singleTerm)) {	//The singleTerm is contained in some phrases
				taggedTextStr = taggedTextTemp;
			} else {
				//do nothing
			}
		}
		
		String[] splitTaggedText = taggedTextStr.trim().split("\\s+");
		taggedText = new StringBuffer("");
		for(String splitStr: splitTaggedText) {
			taggedText.append(splitStr.trim() + "  ");
		}
		//System.out.println(taggedText);
		return taggedText;
	}
}//
