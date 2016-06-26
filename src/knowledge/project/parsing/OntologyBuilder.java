package knowledge.project.parsing;

import healthtextbase.project.type.DependencyTuple;
import healthtextbase.project.type.TaggedTuple;
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
 * Build the ontology for the text base 
 * @Yueshen 
 * */
public class OntologyBuilder {
	
	private List<String> tobeExtractedTagList = null;
	private List<String> concernedTypeList = null;
	private List<String> containedExcludeTermList = null;
	private List<String> equalExcludeTermList = null;
	
	private Map<String, List<String>> globalRepTermListMap = null;
	private List<String> globalManulTagList = null;
	
	//constructor
	OntologyBuilder() {
		this.tobeExtractedTagList = new ArrayList<String>();
		this.tobeExtractedTagList.add("effect");
		this.tobeExtractedTagList.add("nutrition");
		
		this.concernedTypeList = ConfigUtil.concernedTypeList;
		this.containedExcludeTermList = ConfigUtil.containedExcludedList;
		this.equalExcludeTermList = ConfigUtil.equalExcludedList;
		
		this.globalRepTermListMap = new HashMap<String, List<String>>();
		this.globalManulTagList = new ArrayList<String>();
	}//
	
	//collect node information from each xml file
	private void collectNodeFromEachFile(String xmlFilePath) {
		
		Document document = FileUtil.buildDocument(xmlFilePath);		
		if(document == null) {
			ExceptionUtility.throwAndCatchException("The document is null");
			return;
		}
		
		Element rootElement = document.getDocumentElement();
		
		List<DependencyTuple> dependencyTupleList = new ArrayList<DependencyTuple>();
		
		String foodName = rootElement.getElementsByTagName("foodname").item(0).getTextContent().trim();
		
		for(String tagName: this.tobeExtractedTagList) {
			NodeList tagNodeList = rootElement.getElementsByTagName(tagName);
			if(tagNodeList == null || tagNodeList.getLength() == 0) {
				ExceptionUtility.throwAndCatchException("tagNodeList is null");
				continue;
			}
			
			Node node = tagNodeList.item(0);
			NodeList subNodeList = node.getChildNodes();
			for(int i = 0; i < subNodeList.getLength(); i++) {
				Node subNode = subNodeList.item(i);
				String subNodeName = subNode.getNodeName();
				if(subNodeName.equals("parsing")) {
					
					String text = subNode.getTextContent().trim();
					String[] splitParsingText = text.split("\\(");
					for(String splitStr: splitParsingText) {
						DependencyTuple dependencyTuple = TaggingUtil.getDependencyTuple(splitStr, 
								this.concernedTypeList);
						if(dependencyTuple != null) {
							dependencyTupleList.add(dependencyTuple);
						}
					}//for...
					
					break;
				}//if...
			}//for i...
		}//for String tagName
		
		//
		Map<String, List<String>> localRepTermListMap = new HashMap<String, List<String>>();
		for(DependencyTuple dependencyTuple: dependencyTupleList) {
			
			TaggedTuple taggedTuple = TaggingUtil.generateOneTaggedTerm(dependencyTuple, foodName, 
					this.containedExcludeTermList, this.equalExcludeTermList);
			if(taggedTuple != null) {
				String taggedTerm = taggedTuple.getTaggedTerm();
				String representativeTerm1 = taggedTuple.getRepresentativeTerm1();
				String representativeTerm2 = taggedTuple.getRepresentativeTerm2();
				if(taggedTerm == null) {
					System.out.println("taggedTerm is null");
					continue;
				}
				
				if(representativeTerm2 == null) {
					if(localRepTermListMap.containsKey(representativeTerm1)) {	//Map<String, List<String>
						List<String> termList = localRepTermListMap.get(representativeTerm1);
						if(!termList.contains(taggedTerm)) {
							termList.add(taggedTerm);
						}
						localRepTermListMap.put(representativeTerm1, termList);
						
					}else {
						List<String> termList = new ArrayList<String>();
						termList.add(taggedTerm);
						localRepTermListMap.put(representativeTerm1, termList);
					}
				}else {
					if(localRepTermListMap.containsKey(representativeTerm1)) {
						List<String> termList = localRepTermListMap.get(representativeTerm1);
						if(!termList.contains(representativeTerm1)) {
							termList.add(representativeTerm1);
						}
						localRepTermListMap.put(representativeTerm1, termList);
					}else{
						List<String> termList = new ArrayList<String>();
						termList.add(representativeTerm1);
						localRepTermListMap.put(representativeTerm1, termList);
					}
					
					if(localRepTermListMap.containsKey(representativeTerm2)) {
						List<String> termList = localRepTermListMap.get(representativeTerm2);
						if(!termList.contains(representativeTerm2)) {
							termList.add(representativeTerm2);
						}
						localRepTermListMap.put(representativeTerm2, termList);
					}else{
						List<String> termList = new ArrayList<String>();
						termList.add(representativeTerm2);
						localRepTermListMap.put(representativeTerm2, termList);
					}
				}//if...
				
			}//if...
		}//for...
		
		NodeList manulTagNodeList = rootElement.getElementsByTagName("manuleffecttag");
		if(manulTagNodeList == null || manulTagNodeList.getLength() == 0) {
			ExceptionUtility.throwAndCatchException("manulTagNodeList is null");
			return;
		}
		Node manulTagNode = manulTagNodeList.item(0);
		String manulText = manulTagNode.getTextContent().trim();
		if(manulText != null && manulText.length() > 1) {
			String[] manulTextSplit = manulText.split("\\s+");
			for(String manulTag: manulTextSplit) {
				String manulTagTrim = manulTag.trim();
				
				if(!this.globalManulTagList.contains(manulTagTrim)) {
					this.globalManulTagList.add(manulTagTrim);
				}
			}//for...
		}//if...
		
		Iterator<String> iter = localRepTermListMap.keySet().iterator();
		while(iter.hasNext()) {
			String representative = iter.next();
			List<String> termList = localRepTermListMap.get(representative);
			
			if(this.globalRepTermListMap.containsKey(representative)) {		//equal or contain
				
				List<String> globalRepTermList = this.globalRepTermListMap.get(representative);
				for(String localTerm: termList) {
					if(!globalRepTermList.contains(localTerm)) {
						globalRepTermList.add(localTerm);
					}
				}//for...
				this.globalRepTermListMap.put(representative, globalRepTermList);
			}else{
				this.globalRepTermListMap.put(representative, termList);
			}
			
		}//while...
	}//
	
	//build the ontology
	public void buildOntology(String inFolderPath, String outFolderPath, String ontologyFilePath, 
							String synonymFilePath) {
		
		File inFolder = new File(inFolderPath);
		File[] xmlFileList = inFolder.listFiles();
		for(File xmlFile : xmlFileList) {
			
			String xmlFilePath = xmlFile.getPath();
			System.out.println(xmlFilePath);

			this.collectNodeFromEachFile(xmlFilePath);
		}//for
		
//		Iterator<String> iter = this.globalRepTermListMap.keySet().iterator();
//		while(iter.hasNext()) {
//			String repTerm = iter.next();
//			List<String> taggedTermList =  this.globalRepTermListMap.get(repTerm);
//			System.out.println(repTerm + " " + taggedTermList);
//		}
		
		for(String manulTag: this.globalManulTagList) {
			String manulTagTrim = manulTag.trim();
			
			Iterator<String> iter = this.globalRepTermListMap.keySet().iterator();
			int mark = 0;
			while(iter.hasNext()) {
				String representative = iter.next();
				List<String> termList = this.globalRepTermListMap.get(representative);
				if(manulTagTrim.contains(representative)) {
					mark = 1;
					if(!termList.contains(manulTagTrim)) {
						termList.add(manulTagTrim);
						this.globalRepTermListMap.put(representative, termList);		//no break;
					}
																						//no break;
				}
			}//while...
			
			if(mark == 0) {							//does not exist in globalRepTermListMap
				List<String> termList = new ArrayList<String>();
				termList.add(manulTagTrim);
				this.globalRepTermListMap.put(manulTagTrim, termList);
			}
		}//for...
		
		FileUtil.writeListMap(this.globalRepTermListMap, ontologyFilePath);
	}//
	
	//main function
	public static void main(String[] args) {
		
		String rootFolderPath = "C:\\Users\\Yueshen\\git\\ProjectRetrieval\\data";
		String website = "xianghadougouhaodou";
		String inFolderPath = rootFolderPath + File.separator + website + File.separator +
				"foodtextbasetaggedfullversion" + File.separator; 
		String outFolderPath = rootFolderPath + File.separator + website + File.separator + "ontology" + File.separator;
		String ontologyFilePath = rootFolderPath + File.separator + website + File.separator + "ontology.csv"; 
		String synonymFilePath = rootFolderPath + File.separator + "lexicon" + File.separator + "synonymdict1.txt";
		
		OntologyBuilder builder = new OntologyBuilder();
		builder.buildOntology(inFolderPath, outFolderPath, ontologyFilePath, synonymFilePath);
		
		System.out.println("Program ends");
	}//main
	
}
