package knowledge.project.parsing;

import knowledge.project.baseclass.XMLParser;
import knowledge.project.util.CommonUtil;
import knowledge.project.util.ConfigUtil;
import knowledge.project.util.FileUtil;
import knowledge.project.util.ParsingUtil;
import knowledge.project.util.SegmentUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Parse each xml file in a basic manner
 * @Yueshen
 * */
public class XMLBasicParser extends XMLParser{
	
	private List<String> parsedTagList = null;
	private SegmentUtil segmentUtil = null;
	private ParsingUtil parsingUtil = null;
	
	//constructor 
	public XMLBasicParser() {
		this.parsedTagList = new ArrayList<String>();
		this.parsedTagList.add("unsuit");
		this.parsedTagList.add("suit");
		this.parsedTagList.add("introduction");
		this.parsedTagList.add("effect");
		this.parsedTagList.add("nutrition");
		this.parsedTagList.add("cuisine");
		
		this.segmentUtil = new SegmentUtil();
		this.parsingUtil = new ParsingUtil();
	}
	
	//build an xml file
	private Document buildDocument(String filePath) {
		
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
	
	//generate the unchanged node
	private StringBuffer generateUnChangedNode(Node node) {
		
		String nodeName = node.getNodeName();
		if(!nodeName.equals("ingredient")) {
			return generateUnChangedNode(nodeName, node.getTextContent());
		} else{
			StringBuffer tagPlusContent = new StringBuffer("");
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
						String subSubText = subSubNode.getTextContent().trim();
						
						if(subSubText != null && subSubText.length() > 0) {
							tagPlusContent.append(ConfigUtil.Third_Indent + "<" + subSubNodeName + ">\n");
							tagPlusContent.append(ConfigUtil.Fourth_Indent + subSubText + "\n");
							tagPlusContent.append(ConfigUtil.Third_Indent + "</" + subSubNodeName + ">\n");
						}
					}
				
					tagPlusContent.append(ConfigUtil.Second_Indent + "</" + subNodeName + ">\n");
				}//for...
			} else {
				tagPlusContent.append("\n");
			}
					
			tagPlusContent.append(ConfigUtil.First_Indent + "</" + nodeName + ">\n\n");
			return tagPlusContent;
		}
	}
	
	//generate the unchanged node
	private StringBuffer generateUnChangedNode(String nodeName, String text) {
		
		StringBuffer tagPlusContent = new StringBuffer("");
		
		tagPlusContent.append(ConfigUtil.First_Indent + "<" + nodeName + ">\n");
		tagPlusContent.append(ConfigUtil.Second_Indent + text.trim() + "\n");
		tagPlusContent.append(ConfigUtil.First_Indent + "</" + nodeName + ">\n\n");

		return tagPlusContent;
	}//
	
	//for those nodes that need to be parsed
	private StringBuffer generateNewNode(Node node) {
		return generateNewNode(node.getNodeName(), node.getTextContent().trim());
	}
	
	//for those nodes that need to be parsed
	private StringBuffer generateNewNode(String nodeName, String text) {
		StringBuffer tagPlusContent = new StringBuffer(ConfigUtil.First_Indent + "<" + nodeName + ">\n");
		
		if(text == null || text.length() == 0) {
			tagPlusContent.append("\n");
		} else {
		
			tagPlusContent.append(ConfigUtil.Second_Indent + "<rawdata>\n");
			String formatedText = CommonUtil.formatedText(text, ConfigUtil.Third_Indent);
			tagPlusContent.append(formatedText + "\n");
			tagPlusContent.append(ConfigUtil.Second_Indent + "</rawdata>\n\n");
		
//			System.out.println("segmenting begins");
			tagPlusContent.append(ConfigUtil.Second_Indent + "<segment>\n");		//segmenting
			String segmentedText = this.segmentUtil.segmentDoc(text);
			String formatedSegmentedText = CommonUtil.formatedText(segmentedText, ConfigUtil.Third_Indent);
			tagPlusContent.append(formatedSegmentedText + "\n");
			tagPlusContent.append(ConfigUtil.Second_Indent + "</segment>\n\n");
		
//			System.out.println("pos begins");
			tagPlusContent.append(ConfigUtil.Second_Indent + "<pos>\n");			//p-o-s
			String taggedText = this.parsingUtil.taggingPOSDoc(segmentedText);
			String formatedTaggedText = CommonUtil.formatedText(taggedText, ConfigUtil.Third_Indent);
			tagPlusContent.append(formatedTaggedText + "\n");
			tagPlusContent.append(ConfigUtil.Second_Indent + "</pos>\n\n");
		
			System.out.println("dependency parsing begins");
			tagPlusContent.append(ConfigUtil.Second_Indent + "<parsing>\n");		//dependency parsing
			String parsedText = this.parsingUtil.parsingDependencyDoc(segmentedText);
			String formatedParsedText = CommonUtil.formatedText(parsedText, ConfigUtil.Third_Indent);
			tagPlusContent.append(formatedParsedText + "\n");
			tagPlusContent.append(ConfigUtil.Second_Indent + "</parsing>\n\n");
		
			tagPlusContent.append(ConfigUtil.Second_Indent + "<tag>\n");			//tag
			tagPlusContent.append(ConfigUtil.Third_Indent + "\n");
			tagPlusContent.append(ConfigUtil.Second_Indent + "</tag>\n\n");
		}
		
		tagPlusContent.append(ConfigUtil.First_Indent + "</" + nodeName + ">\n\n");

		return tagPlusContent;
	}
	
	//parse an xml file
	private void parseXMLFile(String filePath, String outXMLFilePath) {
		
		Document document = buildDocument(filePath);		
		if(document == null) {
			System.out.println("The document is null");
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
			for(int i = 0; i < nodeList.getLength(); i++) {
				Node subNode = nodeList.item(i);
				String nodeName = subNode.getNodeName();
				
				if(nodeName.contains("text")) {
					continue;
				}
				
				StringBuffer tagPlusContent = new StringBuffer("");
				if(this.parsedTagList.contains(nodeName)) {				//nodes to be parsed
					tagPlusContent = this.generateNewNode(subNode);
				} else {												//nodes not to be parsed
					tagPlusContent = this.generateUnChangedNode(subNode);
				}
				
				xmlFileContent.append(tagPlusContent);
			}//for...
			
			xmlFileContent.append("</" + rootElement.getNodeName() + ">");
			FileUtil.writeString(xmlFileContent.toString(), outXMLFilePath);
		}//if...else...
	}
	
	// parse xml files
	private void parseXMLFolder(String inFolderPath, String outFolderPath) {
		
		if(this.parsedTagList == null || this.parsedTagList.size() == 0) {
			this.parsedTagList = new ArrayList<String>(); 
			this.parsedTagList.add("unsuit");
			this.parsedTagList.add("suit");
			this.parsedTagList.add("introduction");
			this.parsedTagList.add("effect");
			this.parsedTagList.add("nutrition");
			this.parsedTagList.add("cuisine");
		}
		
		if(this.segmentUtil == null) {
			this.segmentUtil = new SegmentUtil();
		}
		
		File outFolder = new File(outFolderPath);
		if(!outFolder.exists()) {
			outFolder.mkdir();
		}
		
		File inFolder = new File(inFolderPath);
		File[] xmlFileList = inFolder.listFiles();
		for(File xmlFile : xmlFileList) {
			
			String xmlFilePath = xmlFile.getPath();
			String xmlFileName = xmlFile.getName();
			String outXMLFilePath = outFolderPath + File.separator + xmlFileName;
			System.out.println(outXMLFilePath);
			File outXMLFile = new File(outXMLFilePath);
			if(outXMLFile.exists()) {
				continue;
			}
			
			parseXMLFile(xmlFilePath, outXMLFilePath);
			
		}//for
		
	}//
	
	//main function
	public static void main(String[] args) {
		
		String rootFolderPath = "C:\\Users\\Yueshen\\git\\ProjectRetrieval\\data\\xiangha";

		String inFolderPath = rootFolderPath + File.separator + "foodtextbase" + File.separator; 
		String outFolderPath = rootFolderPath + File.separator + "foodtextbaseparsed" + File.separator;
		
		XMLBasicParser xmlParser = new XMLBasicParser();
		xmlParser.parseXMLFolder(inFolderPath, outFolderPath);
		
		System.out.println("Program ends");
	}//
	
}
