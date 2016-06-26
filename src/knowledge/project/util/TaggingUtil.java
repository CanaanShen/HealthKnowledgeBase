package knowledge.project.util;

import knowledge.project.type.DependencyTuple;
import knowledge.project.type.TaggedTuple;

import java.util.List;

public class TaggingUtil {
	
	public static DependencyTuple getDependencyTuple(String splitStr, List<String> concernedTypeList) {
		
		DependencyTuple dependencyTuple = new DependencyTuple();
		
		if(!splitStr.contains(")")) {
			return null;
		}
		splitStr = splitStr.replace(")", "");
		String[] splitTypePOS = splitStr.split(",");
		if(splitTypePOS.length != 3) {
			return null;
		}
		
		String type = splitTypePOS[0].trim();
		if(!concernedTypeList.contains(type)) {			//not in the list that is concerned
			return null;
		}
		
		String govAndPOS = splitTypePOS[1].trim();
		String[] splitGovPOS = govAndPOS.split("#");
		if(splitGovPOS.length != 2) {
			ExceptionUtil.throwAndCatchException("The length of splitGovPOS is not equal to 2");
			return null;
		}
		String govTerm = splitGovPOS[0].trim();
		String govPOS = splitGovPOS[1].trim();
		
		String depAndPOS = splitTypePOS[2].trim();
		String[] splitDepPOS = depAndPOS.split("#");
		if(splitDepPOS.length != 2) {
			System.out.println("The length of splitDepPOS is not equal to 2");
			return null;
		}
		String depTerm = splitDepPOS[0].trim(); 
		String depPOS = splitDepPOS[1].trim();
		
		dependencyTuple.setType(type);
		dependencyTuple.setGovTerm(govTerm);
		dependencyTuple.setGovPOS(govPOS);
		dependencyTuple.setDepTerm(depTerm);
		dependencyTuple.setDepPOS(depPOS);
		return dependencyTuple;
	}

	//
	public static TaggedTuple generateOneTaggedTerm(DependencyTuple dependencyTuple, String foodName, 
			List<String> containedExcludeList, List<String> equalExcludedList) {
		
		if(dependencyTuple == null) {
			ExceptionUtil.throwAndCatchException("dependencyTuple is null");
			return null;
		}
		
		String type = dependencyTuple.getType();
		String govTerm = dependencyTuple.getGovTerm();
		String govPOS = dependencyTuple.getGovPOS();
		String depTerm = dependencyTuple.getDepTerm();
		String depPOS = dependencyTuple.getDepPOS();
		
		if(govTerm.contains(foodName) || depTerm.contains(foodName)) {
			return null;
		}
		
		boolean contained = false;
		for(String excludedTerm: containedExcludeList) {
			if(govTerm.contains(excludedTerm) || depTerm.contains(excludedTerm)) {
				contained = true;
				break;
			}
		}//for...
		
		if(contained) {					//contain one or more excluded terms
			return null;
		}//if...
		
		boolean equal = false;
		for(String equalTerm: equalExcludedList){
			if(govTerm.equals(equalTerm) || depTerm.equals(equalTerm)) {
				equal = true;
				break;
			}
		}
		
		if(equal) {
			return null;
		}
		
		String taggedTerm = null;

		TaggedTuple taggedObject = null;
		if(type.equals("root")) {
			if(depPOS.equals("VV") && depTerm.length() == 2) {
				taggedTerm = depTerm;
				
				taggedObject = new TaggedTuple();
				taggedObject.setType(type);
				taggedObject.setTaggedTerm(taggedTerm);
				taggedObject.setRepresentativeTerm1(depTerm);
			}
		}
		
		if(type.equals("dobj")) {
			if(govTerm.length() > 1 && depPOS.equals("NN")) {
				taggedTerm = govTerm + depTerm;
				
				if(govTerm.equals("丰富")) {
					taggedTerm = depTerm + govTerm;
				}
				
				taggedObject = new TaggedTuple();
				taggedObject.setType(type);
				taggedObject.setTaggedTerm(taggedTerm);
				taggedObject.setRepresentativeTerm1(depTerm);
			}
		}//if...
		
		if(type.equals("relcl")) {
			if(depPOS.equals("VA") && depTerm.length() > 1) {
				taggedTerm = govTerm + depTerm;
			}
			
			if(depPOS.equals("VV")) {
				taggedTerm = depTerm + govTerm;
			}
			
			if(taggedTerm != null) {
				
				taggedObject = new TaggedTuple();
				taggedObject.setType(type);
				taggedObject.setTaggedTerm(taggedTerm);
				taggedObject.setRepresentativeTerm1(govTerm);
			}
		}//if...
		
		if(type.equals("conj")) {
			if(govPOS.equals("NN") && depPOS.equals("NN") 
					&& govTerm.length() > 1 && depTerm.length() > 1) {
				taggedTerm = govTerm;
				
				taggedObject = new TaggedTuple();
				taggedObject.setTaggedTerm(taggedTerm);
				taggedObject.setType(type);
				taggedObject.setRepresentativeTerm1(govTerm);
				taggedObject.setRepresentativeTerm2(depTerm);
			}
			
			if(govPOS.equals("NR") && depPOS.equals("NR") 
					&& govTerm.length() > 1 && depTerm.length() > 1) {
				taggedTerm = govTerm;
				
				taggedObject = new TaggedTuple();
				
				taggedObject.setTaggedTerm(taggedTerm);
				taggedObject.setType(type);
				taggedObject.setRepresentativeTerm1(govTerm);
				taggedObject.setRepresentativeTerm2(depTerm);
			}
		}//if...
		
		if(type.equals("ccomp")) {
			if(govPOS.equals("VA") && depPOS.equals("VA")) {
				taggedTerm = govTerm + depTerm;
				
				taggedObject = new TaggedTuple();
				taggedObject.setType(type);
				taggedObject.setTaggedTerm(taggedTerm);
				taggedObject.setRepresentativeTerm1(depTerm);
			}
		}//if...
		
		if(type.equals("nsubj")) {
			if(govPOS.equals("VA") && depPOS.equals("NN") && govTerm.length() > 1) {
				taggedTerm = depTerm + govTerm;
				
				taggedObject = new TaggedTuple();
				taggedObject.setType(type);
				taggedObject.setTaggedTerm(taggedTerm);
				taggedObject.setRepresentativeTerm1(govTerm);
			}
		}//if...
		
		if(type.equals("amod")) {
			if(govPOS.equals("NN") && depPOS.equals("JJ")) {  //only one character
				taggedTerm = depTerm + govTerm;
				
				taggedObject = new TaggedTuple();
				taggedObject.setType(type);
				taggedObject.setTaggedTerm(taggedTerm);
				taggedObject.setRepresentativeTerm1(govTerm);
			}
		}//if...
		
		if(type.equals("comod")) {
			
			if(govPOS.equals("VA") && depPOS.equals("VA") && depTerm.length() > 1 
					&& govTerm.length() > 1) {
				taggedTerm = depTerm + govTerm;
				
				taggedObject = new TaggedTuple();
				taggedObject.setType(type);
				taggedObject.setTaggedTerm(taggedTerm);
				taggedObject.setRepresentativeTerm1(depTerm);
				
				if(depTerm.equals("丰富")) {
					taggedTerm = govTerm + depTerm;
					
					taggedObject.setType(type);
					taggedObject.setTaggedTerm(taggedTerm);
					taggedObject.setRepresentativeTerm1(govTerm);
				}
			}//if...
			
		}//if...
		
		if(type.equals("nn")) {
			if(govPOS.equals("NN") && depPOS.equals("NN")) {
				if(govTerm.length() > 1 && depTerm.length() > 1) {
					taggedTerm = depTerm + govTerm;
					
					taggedObject = new TaggedTuple();
					taggedObject.setType(type);
					taggedObject.setTaggedTerm(taggedTerm);
					taggedObject.setRepresentativeTerm1(govTerm);
				}
			}
		}//if...
		
		if(type.equals("advmod")) {
			if(govPOS.equals("VE") && depPOS.equals("AD")) {
				if(govTerm.length() > 1 && depTerm.length() > 1) {
					taggedTerm = depTerm + govTerm; 
					
					taggedObject = new TaggedTuple();
					taggedObject.setType(type);
					taggedObject.setTaggedTerm(taggedTerm);
					taggedObject.setRepresentativeTerm1(govTerm);
				}
			}
		}//if...
		
		if(type.equals("assmod")) {
			if(govPOS.equals("NN") && depPOS.equals("NN") ) {
				if(govTerm.length() >=3) {
					taggedTerm = govTerm;
					
					taggedObject = new TaggedTuple();
					taggedObject.setType(type);
					taggedObject.setTaggedTerm(taggedTerm);
					taggedObject.setRepresentativeTerm1(govTerm);
				}
				
				if(depTerm.length() >= 3) {					//only one
					taggedTerm = depTerm;
					
					taggedObject = new TaggedTuple();
					taggedObject.setType(type);
					taggedObject.setTaggedTerm(taggedTerm);
					taggedObject.setRepresentativeTerm1(depTerm);
				}
			}//if...
		}//if...
		
		return taggedObject;
	}//function ends
	
	
	//
	public static void generateOneTaggedTerm(StringBuffer taggedText, DependencyTuple dependencyTuple,
			List<String> singleTermList, String foodName, List<String> containedExcludeList,
			List<String> equalExcludedList) {
	
		String type = dependencyTuple.getType();
		String govTerm = dependencyTuple.getGovTerm();
		String govPOS = dependencyTuple.getGovPOS();
		String depTerm = dependencyTuple.getDepTerm();
		String depPOS = dependencyTuple.getDepPOS();
		
		if(type == null || govTerm == null || govPOS == null || depTerm == null || depPOS == null) {
			ExceptionUtil.throwAndCatchException("type or govTerm or govPOS or depTerm or depPOS is null");
			return;
		}
		
		if(govTerm.contains(foodName) || depTerm.contains(foodName)) {
			return;
		}
	
		boolean contained = false;
		for(String excludedTerm: containedExcludeList) {
			if(govTerm.contains(excludedTerm) || depTerm.contains(excludedTerm)) {
				contained = true;
				break;
			}
		}//for...
	
		if(contained) {					//contain one or more excluded terms
			return;
		}//if...
	
		boolean equal = false;
		for(String equalTerm: equalExcludedList){
			if(govTerm.equals(equalTerm) || depTerm.equals(equalTerm)) {
				equal = true;
				break;
			}
		}
	
		if(equal) {
			return;
		}
	
		String taggedTerm = null;
	
		if(type.equals("root")) {
			if(depPOS.equals("VV") && depTerm.length() == 2) {
				taggedTerm = depTerm;
				
				if(!taggedText.toString().contains(taggedTerm)) {
					taggedText.append(taggedTerm + "  ");
				}
				if(!singleTermList.contains(depTerm)) {
					singleTermList.add(depTerm);
				}
			}
		}//if root
	
		if(type.equals("dobj")) {
			if(govTerm.length() > 1 && depPOS.equals("NN")) {
				taggedTerm = govTerm + depTerm;
			
				if(govTerm.equals("丰富")) {
					taggedTerm = depTerm + govTerm;
				}
				
				if(!taggedText.toString().contains(taggedTerm)) {
					taggedText.append(taggedTerm + "  ");
				}
			}
		}//if dobj
	
		if(type.equals("relcl")) {
			if(depPOS.equals("VA") && depTerm.length() > 1) {
				taggedTerm = govTerm + depTerm;
			}
		
			if(depPOS.equals("VV")) {
				taggedTerm = depTerm + govTerm;
			}
			
			if(taggedTerm != null && !taggedText.toString().contains(taggedTerm)) {
				taggedText.append(taggedTerm + "  ");
			}
		}//if relcl
	
		if(type.equals("conj")) {
			if(govPOS.equals("NN") && depPOS.equals("NN") 
				&& govTerm.length() > 1 && depTerm.length() > 1) {
				
				if(!singleTermList.contains(govTerm)) {
					singleTermList.add(govTerm);			
				}
				if(!taggedText.toString().contains(govTerm)) {
					taggedText.append(govTerm + "  ");
				}
			
				if(!singleTermList.contains(depTerm)) {
					singleTermList.add(depTerm);			
				}
				if(!taggedText.toString().contains(depTerm)) {
					taggedText.append(depTerm + "  ");
				}	
			}
		
			if(govPOS.equals("NR") && depPOS.equals("NR") 
				&& govTerm.length() > 1 && depTerm.length() > 1) {
				if(!singleTermList.contains(govTerm)) {
					singleTermList.add(govTerm);		
				}
				if(!taggedText.toString().contains(govTerm)) {
					taggedText.append(govTerm + "  ");
				}		
			
				if(!singleTermList.contains(depTerm)) {
					singleTermList.add(depTerm);		
				}
				if(!taggedText.toString().contains(depTerm)) {
					taggedText.append(depTerm + "  ");
				}		
			}
		
		}//if conj
	
		if(type.equals("ccomp")) {
			if(govPOS.equals("VA") && depPOS.equals("VA")) {
				taggedTerm = govTerm + depTerm;
				if(!taggedText.toString().contains(taggedTerm)) {
					taggedText.append(taggedTerm + "  ");
				}
			}
		}//if ccomp
	
		if(type.equals("nsubj")) {
			if(govPOS.equals("VA") && depPOS.equals("NN") && govTerm.length() > 1) {
				taggedTerm = depTerm + govTerm;
				if(!taggedText.toString().contains(taggedTerm)) {
					taggedText.append(taggedTerm + "  ");
				}
			}
		}//if nsubj
	
		if(type.equals("amod")) {
			if(govPOS.equals("NN") && depPOS.equals("JJ")) {  //only one character
				taggedTerm = depTerm + govTerm;
				if(!taggedText.toString().contains(taggedTerm)) {
					taggedText.append(taggedTerm + "  ");
				}
			}
		}//if amod
	
		if(type.equals("comod")) {
		
			if(govPOS.equals("VA") && depPOS.equals("VA") && depTerm.length() > 1 
				&& govTerm.length() > 1) {
				taggedTerm = depTerm + govTerm;
			
				if(depTerm.equals("丰富")) {
					taggedTerm = govTerm + depTerm;
				}
			
				if(!taggedText.toString().contains(taggedTerm)) {
					taggedText.append(taggedTerm + "  ");
				}
			}//if...
		}//if comod
	
		if(type.equals("nn")) {
			if(govPOS.equals("NN") && depPOS.equals("NN")) {
				if(govTerm.length() > 1 && depTerm.length() > 1) {
					taggedTerm = depTerm + govTerm;
				
					if(!taggedText.toString().contains(taggedTerm)) {
						taggedText.append(taggedTerm + "  ");
					}
				}
			}
		}//if nn
	
		if(type.equals("advmod")) {
			if(govPOS.equals("VE") && depPOS.equals("AD")) {
				if(govTerm.length() > 1 && depTerm.length() > 1) {
					taggedTerm = depTerm + govTerm;

					if(!taggedText.toString().contains(taggedTerm)) {
						taggedText.append(taggedTerm + "  ");
					}
				}
			}
		}//if advmod
	
		if(type.equals("assmod")) {
			if(govPOS.equals("NN") && depPOS.equals("NN") ) {
				if(govTerm.length() >=3) {
					if(!taggedText.toString().contains(govTerm)) {
						taggedText.append(govTerm + "  ");
					}
					if(!singleTermList.contains(govTerm)) {
						singleTermList.add(govTerm);
					}
				}
			
				if(depTerm.length() >= 3) {		//only depTerm
					if(!taggedText.toString().contains(depTerm)) {
						taggedText.append(depTerm + "  ");
					}
					if(!singleTermList.contains(depTerm)) {
						singleTermList.add(depTerm);
					}
				}
			}
		}//assmod
	}	
}

//if(type.equals("assmod")) {
//if(govPOS.equals("NN") && depPOS.equals("NN")) {
//	if(depTerm.length() <=2) {
//		taggedTerm = govTerm + depTerm;
//	}else {
//		taggedTerm = depTerm + govTerm;
//
//	}
//	
//	if(!taggedText.toString().contains(taggedTerm)) {
//		taggedText.append(taggedTerm + "  ");
//	}
//}
//}//if...

//if(govPOS.equals("VV") && depPOS.equals("AD")) {
//taggedTerm = govTerm + depTerm;
//if(!taggedText.toString().contains(taggedTerm)) {
//	taggedText.append(govTerm + depTerm + "  ");
//}
//}