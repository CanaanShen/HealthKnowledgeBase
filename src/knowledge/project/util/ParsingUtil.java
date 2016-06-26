package knowledge.project.util;

import java.io.StringReader;
import java.util.List;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.process.TokenizerFactory;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreebankLanguagePack;
import edu.stanford.nlp.trees.TypedDependency;
import edu.stanford.nlp.trees.international.pennchinese.ChineseTreebankLanguagePack;

public class ParsingUtil {

	private String Base_Dir = null; 
	private LexicalizedParser lexicalParser = null;
    TokenizerFactory<CoreLabel> tokenizerFactory = null; 
            	
	public ParsingUtil() {
		this.Base_Dir = "edu/stanford/nlp/models/";
		this.lexicalParser = LexicalizedParser.loadModel(this.Base_Dir + "lexparser/xinhuaFactored.ser.gz");
		this.tokenizerFactory = PTBTokenizer.factory(new CoreLabelTokenFactory(), "");
	}
	
	public int subStringCount(String wholeStr, String subStr) {
		int subStrCount = 0;
		int wholeSize = wholeStr.length();
		int subIndex = 0;
		while(subIndex < wholeSize) {
			subIndex = wholeStr.indexOf(subStr, subIndex+1);
			if(subIndex > 0) {
				subStrCount ++;
			} else {
				break;
			}
		}//while...
		
		return subStrCount;
	}
	
	//
	public String taggingPOSDoc(String doc){
		
		if(this.Base_Dir == null) {
			this.Base_Dir = "edu/stanford/nlp/models/";
		}
		
		if(this.lexicalParser == null) {
			this.lexicalParser = LexicalizedParser.loadModel(this.Base_Dir + "lexparser/xinhuaFactored.ser.gz");
		}
		
		if(this.tokenizerFactory == null) {
			this.tokenizerFactory = PTBTokenizer.factory(new CoreLabelTokenFactory(), "");
		}
		
		StringBuffer docBuffer = new StringBuffer("");
		
		String[] splitDoc = doc.trim().split("\n");
		for(String sentence: splitDoc) {
			
			int periodNum = this.subStringCount(sentence, "�?");
			if(periodNum < 2) {						// ~ <= 2 sentences
				String taggedSentence = taggingPOSSentence(sentence.trim());
				docBuffer.append(taggedSentence + "\n");
			} else {
				String[] splitSent = sentence.split("�?");
				for(String periodSent : splitSent) {
					String taggedPeriodSent = taggingPOSSentence(periodSent.trim());
					docBuffer.append(taggedPeriodSent + "  �?#PU  ");
				}
				docBuffer.append("\n");
			}
		}//
		
		return docBuffer.toString();
	}//
	
	//
	private String taggingPOSSentence(String sentence) {
		StringBuffer sentenceBuffer = new StringBuffer("");
		
		try{
			List<CoreLabel> rawSentence =
	    	      this.tokenizerFactory.getTokenizer(new StringReader(sentence)).tokenize();
			Tree tree = (Tree) this.lexicalParser.apply(rawSentence);
			List<TaggedWord> taggedWordList = tree.taggedYield();
			for(TaggedWord taggedWord: taggedWordList) {
				sentenceBuffer.append(taggedWord.word() + "#" + taggedWord.tag() + "  ");
			}//for...
		} catch(Exception e) {
			e.printStackTrace();
		}
		return sentenceBuffer.toString();
	}
	
	public String parsingDependencyDoc(String doc) {
		
		if(this.Base_Dir == null) {
			this.Base_Dir = "edu/stanford/nlp/models/";
		}
		
		if(this.lexicalParser == null) {
			this.lexicalParser = LexicalizedParser.loadModel(this.Base_Dir + "lexparser/xinhuaFactored.ser.gz");
		}
		
		if(this.tokenizerFactory == null) {
			this.tokenizerFactory = PTBTokenizer.factory(new CoreLabelTokenFactory(), "");
		}
		
		StringBuffer docBuffer = new StringBuffer("");
		
		String[] splitDoc = doc.trim().split("\n");
		for(String sentence: splitDoc) {
			
			int periodNum = this.subStringCount(sentence, "�?");
//			System.out.println("period number: " + periodNum);
			if(periodNum < 2) {						// ~ <= 2 sentences
				String parsedSentence = parsingDependencySentence(sentence.trim());
				docBuffer.append(parsedSentence + "\n");
			} else {
				String[] splitSent = sentence.split("�?"); 
				for(String periodSent : splitSent) {
					String parsedPeriodSent = parsingDependencySentence(periodSent.trim());
					docBuffer.append(parsedPeriodSent + " ");
				}
				docBuffer.append("\n");
			}
		}//
		
		return docBuffer.toString();
	}
	
	private String parsingDependencySentence(String sentence) {
		StringBuffer sentenceBuffer = new StringBuffer("");
		
	     List<CoreLabel> rawSentence =
	    	      this.tokenizerFactory.getTokenizer(new StringReader(sentence)).tokenize();
		Tree tree = (Tree) lexicalParser.apply(rawSentence);
		TreebankLanguagePack tlp = new ChineseTreebankLanguagePack();
		GrammaticalStructureFactory gramStrtFactory = tlp.grammaticalStructureFactory();
		GrammaticalStructure gramStrt = gramStrtFactory.newGrammaticalStructure(tree);
	    
		List<TypedDependency> tdl = gramStrt.typedDependenciesCCprocessed();//.typedDependenciesCCprocessed();
		for(TypedDependency type : tdl) {
			sentenceBuffer.append("(" + type.reln().getShortName() + ", " + type.gov().word() + "#" + 
					type.gov().tag() + ", " + type.dep().word() + "#" + type.dep().tag() + ")" + "  ");
		}
		
		return sentenceBuffer.toString();
	}
	
	//main
	public static void main(String[] args) {
		ParsingUtil parsingUtil = new ParsingUtil();
		String str = "猪肉是维生素的主要膳食来源，特别是精猪肉中维生素B1的含量丰富�?�猪肉中还含有较多的对脂肪合成和分解有重要作用的维生素B2�?";
		System.out.println(parsingUtil.subStringCount(str, "�?"));
	}//main
	
	//
	private void parseTesting() {
		String baseDir = "edu/stanford/nlp/models/";
		LexicalizedParser lexicalParser = LexicalizedParser.loadModel(baseDir + "lexparser/xinhuaFactored.ser.gz");
		String sent = "猪肉   �?   蛋白�?   �?   优质   蛋白�?   �? 含有   人体   全部   必需   氨基�?";
		
	     TokenizerFactory<CoreLabel> tokenizerFactory =
	             PTBTokenizer.factory(new CoreLabelTokenFactory(), "");
	     List<CoreLabel> rawSentence =
	    	      tokenizerFactory.getTokenizer(new StringReader(sent)).tokenize();
		Tree tree = (Tree) lexicalParser.apply(rawSentence);
		List<TaggedWord> taggedWordList = tree.taggedYield();
		for(TaggedWord taggedWord: taggedWordList) {
			System.out.println(taggedWord.word() + " : " + taggedWord.tag());
		}
		
		TreebankLanguagePack tlp = new ChineseTreebankLanguagePack();
		GrammaticalStructureFactory gramStrtFactory = tlp.grammaticalStructureFactory();
		GrammaticalStructure gramStrt = gramStrtFactory.newGrammaticalStructure(tree);
	    
		List<TypedDependency> tdl = null;
		tdl = gramStrt.typedDependenciesCCprocessed();
		for(TypedDependency type: tdl) {
			System.out.println(type);
			System.out.println(type.reln().getParent());
			System.out.println(type.reln().getLongName());
		}
	}
	
}
