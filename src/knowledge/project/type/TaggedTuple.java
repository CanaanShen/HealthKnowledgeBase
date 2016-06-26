package knowledge.project.type;

/**
 * 
 * */
public class TaggedTuple {
	private String type = null;
	private String taggedTerm = null;
	private String representativeTerm1 = null;
	private String representativeTerm2 = null;
	
	public void setTaggedTerm(String taggedTerm) {
		this.taggedTerm = taggedTerm;
	}
	
	public void setRepresentativeTerm1(String representativeTerm1) {
		this.representativeTerm1 = representativeTerm1;
	}
	
	public void setRepresentativeTerm2(String representativeTerm2) {
		this.representativeTerm2 = representativeTerm2;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public String getTaggedTerm() {
		return this.taggedTerm;
	}
	
	public String getRepresentativeTerm1() {
		return this.representativeTerm1;
	}
	
	public String getRepresentativeTerm2() {
		return this.representativeTerm2;
	}
	
	public String getType() {
		return this.type;
	}

}
