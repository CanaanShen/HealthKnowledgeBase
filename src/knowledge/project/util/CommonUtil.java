package knowledge.project.util;

import org.w3c.dom.NodeList;

public class CommonUtil {
	
	public static int countChildNodeNumber(NodeList nodeList) {
		
		if(nodeList == null || nodeList.getLength() == 0) {
			return 0;
		}
		
		int number = 0;
		for(int i = 0; i < nodeList.getLength(); i++) {
			if(nodeList.item(i).getNodeName().contains("text")) {
				continue;
			}
			number = number + 1;
		}
		return number;
	}
	
	public static String formatedText(String text, String format) {
		
		String[] splitText = text.trim().split("\n");
		StringBuffer textBuffer = new StringBuffer("");
		for(int i = 0; i < splitText.length - 1; i++) {
			textBuffer.append(format + splitText[i].trim() + "\n");
		}//for
		
		textBuffer.append(format + splitText[splitText.length-1].trim());
		
		return textBuffer.toString();
	}
	
	public static void main(String[] args) {

	}

}
