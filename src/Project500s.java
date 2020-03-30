import java.util.ArrayList;
import java.util.HashSet;

public class Project500s {
	
	private String projectNumber;
	
	private ArrayList<HashSet<String>> list;
	private String number500;
	private String taktSerie;
	
	
	
	public Project500s(String projectNumber, String number500, String taktSerie) {
		
		list = new ArrayList<>();
		
		this.projectNumber = projectNumber;
		this.number500 = number500;
		this.taktSerie = taktSerie;
	}
	
	
	public String getProjectNumber() {
		return projectNumber;
	}
	public void setProjectNumber(String projectNumber) {
		this.projectNumber = projectNumber;
	}
	public String getNumber500() {
		return number500;
	}
	public void setNumber500(String number500) {
		this.number500 = number500;
	}
	public String getTaktSerie() {
		return taktSerie;
	}
	public void setTaktSerie(String taktSerie) {
		this.taktSerie = taktSerie;
	}
	
	
	
	

}
