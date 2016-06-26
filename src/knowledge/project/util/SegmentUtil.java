package knowledge.project.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;

/**
 * Finish the segmenting task for a sentence in Chinese
 * */
public class SegmentUtil {
	
	private String Base_Dir = null;
	private Properties Property_Instance = null;
	private CRFClassifier<CoreLabel> Segmenter = null;
	private List<String> replacedPUList = null;
	
	//the constructor
	public SegmentUtil () {
		
		Base_Dir ="edu/stanford/nlp/models/segmenter/chinese/";
		Property_Instance = new Properties();
	    Property_Instance.setProperty("sighanCorporaDict", Base_Dir);

		Property_Instance.setProperty("serDictionary", Base_Dir + "dict-chris6.ser.gz");
		Property_Instance.setProperty("inputEncoding", "utf-8");
		Property_Instance.setProperty("sighanPostProcessing", "true");
		
		this.Segmenter = new CRFClassifier<CoreLabel>(Property_Instance);
		this.Segmenter.loadClassifierNoExceptions(Base_Dir + "ctb.gz", Property_Instance);
		this.Segmenter.flags.setProperties(Property_Instance);
		
		this.replacedPUList = new ArrayList<String>();
		this.replacedPUList.add(".");
		this.replacedPUList.add("、");
		this.replacedPUList.add("《");
		this.replacedPUList.add("》");
		this.replacedPUList.add("<");
		this.replacedPUList.add(">");
	}
	
	//segment a doc
	public String segmentDoc(String text) {
		
		StringBuffer segmentedText = new StringBuffer(""); 
		String[] splitText = text.trim().split("\n");
		for(String sentence: splitText) {
			
			for(String replacedPU: this.replacedPUList) {
				if(sentence.contains(replacedPU)) {
					if(replacedPU.equals(".") || replacedPU.equals("、")) {			//To alleviate the side effect brought by the period 
						sentence = sentence.replaceFirst("\\d"+replacedPU, " ");
					}else{
						sentence = sentence.replace(replacedPU, " ");
					}
				}
			}//for...
			
			String segmentedSentence = this.segmentSentence(sentence.trim());
			segmentedText.append(segmentedSentence + "\n");
		}//for...
		return segmentedText.toString();
	}
	
	// segment a sentence
	private String segmentSentence(String sentence) {
		StringBuffer segmentedSentence = new StringBuffer("");
		String[] strArray = (String[]) Segmenter.segmentString(sentence).toArray();
		for(String str: strArray) {
			segmentedSentence.append(str + "  ");
		}
		
		return segmentedSentence.toString();
	}
	
	//main function
	public static void main(String[] args) {
		
		SegmentUtil parsingUtil = new SegmentUtil();
//		tester.testParsing();
//		parsingUtil.testSegment();
		String str = " 1. 补充蛋白质和脂肪酸 \n\n" +
        "猪肉为人类提供优质蛋白质和必需的《脂肪酸》。猪肉可提供血红素。";
		
		System.out.println(parsingUtil.segmentDoc(str));
		System.out.println("Program ends");
	}

}
