package knowledge.project.testing;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.process.TokenizerFactory;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.PennTreebankLanguagePack;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreebankLanguagePack;
import edu.stanford.nlp.trees.TypedDependency;
import edu.stanford.nlp.trees.international.pennchinese.ChineseTreebankLanguagePack;
import edu.stanford.nlp.util.CoreMap;

public class ParsingTester {
	
	private void testParsing() {
//		Properties props = new Properties();
//		props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse");
//		StanfordCoreNLP pipeLine = new StanfordCoreNLP(props);
//		
		String doc = "猪肉 �? 蛋白�? �? 优质 蛋白�? �? 含有 人体 全部 必需 氨基�?";
		List<String> docList = new ArrayList<String>();
		docList.add("猪肉");
		docList.add("蛋白�?");
		docList.add("人体"); 
		docList.add("全部");
//		
//		Annotation document = new Annotation(doc);
//		pipeLine.annotate(document);
		
	    //LexicalizedParser lp = LexicalizedParser.loadModel("edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz");
		
        MaxentTagger tagger = new MaxentTagger(
                "edu/stanford/nlp/models/pos-tagger/chinese-distsim/chinese-distsim.tagger");
	    String docTag = tagger.tagTokenizedString(doc);
	    System.out.println(docTag);
	    
	    /*
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);
		for(CoreMap sentence: sentences) {
			System.out.println(sentence);
		
			List<CoreLabel> tokenList = sentence.get(TokensAnnotation.class);
			for(CoreLabel token: tokenList) {
				String lemma = token.getString(LemmaAnnotation.class);
				String pos = token.getString(PartOfSpeechAnnotation.class);
				String ner = token.getString(NamedEntityTagAnnotation.class);
				System.out.println("lemma: " + lemma + " pos: " + pos + " ner " + ner);
			}
			
		    Tree parse = lp.apply(tokenList);
		    parse.pennPrint();
		}//for
		*/
	}
	
	//test segment
	private void testSegment() {
		
		String baseDir = "edu/stanford/nlp/models/segmenter/chinese/";
		Properties property = new Properties();
	    property.setProperty("sighanCorporaDict", baseDir);

		property.setProperty("serDictionary", baseDir + "dict-chris6.ser.gz");
		property.setProperty("inputEncoding", "utf-8");
		property.setProperty("sighanPostProcessing", "true");
		
		CRFClassifier<CoreLabel> segmenter = new CRFClassifier<CoreLabel>(property);
		segmenter.loadClassifierNoExceptions(baseDir + "ctb.gz", property);
		segmenter.flags.setProperties(property);
		
		String sentence = "猪肉的蛋白质属优质蛋白质，含有人体全部必�?氨基�?";
		String[] strArray = (String[]) segmenter.segmentString(sentence).toArray();
		for(String str: strArray) {
			System.out.println(str);
		}
	}
	
	private void parseTesting() {
		String baseDir = "edu/stanford/nlp/models/";
		LexicalizedParser lexicalParser = LexicalizedParser.loadModel(baseDir + "lexparser/xinhuaFactored.ser.gz");
		String sent = "猪肉  含有  丰富  �?  蛋白�?  �?  脂肪�?  �? 具有   很多  作用 。猪�?   味道  清淡  �?";
		
	     TokenizerFactory<CoreLabel> tokenizerFactory =
	             PTBTokenizer.factory(new CoreLabelTokenFactory(), "");
	     List<CoreLabel> rawSentence =
	    	      tokenizerFactory.getTokenizer(new StringReader(sent)).tokenize();
		Tree tree = (Tree) lexicalParser.apply(rawSentence);
		List<TaggedWord> taggedWordList = tree.taggedYield();
		for(TaggedWord taggedWord: taggedWordList) {
			//System.out.println(taggedWord.word() + " : " + taggedWord.tag());
		}
		
		TreebankLanguagePack tlp = new ChineseTreebankLanguagePack();
		GrammaticalStructureFactory gramStrtFactory = tlp.grammaticalStructureFactory();
		GrammaticalStructure gramStrt = gramStrtFactory.newGrammaticalStructure(tree);
	    
		Collection<TypedDependency> tdl = null;
		tdl = gramStrt.typedDependenciesCCprocessed();
		for(TypedDependency type: tdl) {
			System.out.print(type.reln().getShortName());
			//System.out.print(" gov.originalText: " + type.gov().originalText() );
			System.out.println("   : " + type.dep().word() + " ; " + type.dep().originalText());
			System.out.println();
//			System.out.println(type.reln()());
		}

	}
	
	public static void main(String[] args) {
		
		ParsingTester tester = new ParsingTester();
		//tester.testParsing();
//		tester.testSegment();
//		String str = "I am a student.";
//		tester.parseTesting();
//		String str = "(loc, 消除#VV, �?#NN)  (case, �?#NN, �?#LC)  (mmod, 消除#VV, �?#VV)  (root, ROOT#null, 消除#VV)  (assmod, 饥饿�?#NN, 肥胖�?#NN) ";
//		Pattern pattern = Pattern.compile("\\(.+\\)");
//		String[] split = pattern.split("\\(.*\\)");
//		for(String splitStr: split) {
////			System.out.println(splitStr);
//		}
		//		Matcher m = pattern.matcher(str);
//		System.out.println(m.groupCount());
//		if(m.matches()) {
//			System.out.println(m.group());
//		}
		String str = " 1. 西瓜清热解毒";
		str = str.replaceFirst("\\d.", "");
		System.out.println(str);
		System.out.println("Program ends");
	}
	
}
