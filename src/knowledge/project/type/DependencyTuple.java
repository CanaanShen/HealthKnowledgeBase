package knowledge.project.type;

/**
 * 
 * */
public class DependencyTuple {
	
	private String type = null;
	private String govTerm = null;
	private String govPOS = null;
	private String depTerm = null;
	private String depPOS = null;
	
	public void setType(String type) {
		this.type = type;
	}
	
	public void setGovTerm(String govTerm) {
		this.govTerm = govTerm;
	}
	
	public void setGovPOS(String govPOS) {
		this.govPOS = govPOS;
	}
	
	public void setDepTerm(String depTerm) {
		this.depTerm = depTerm;
	}
	
	public void setDepPOS(String depPOS) {
		this.depPOS = depPOS;
	}
	
	public String getType() {
		return this.type;
	}
	
	public String getGovTerm() {
		return this.govTerm;
	}
	
	public String getGovPOS() {
		return this.govPOS;
	}
	
	public String getDepTerm() {
		return this.depTerm;
	}
	
	public String getDepPOS() {
		return this.depPOS;
	}
}
