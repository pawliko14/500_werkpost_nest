
public class Obiekt500 {

	private String status; // 10,20,90
	private String gniazdo ; 
	private String Wydzial;
	private String number500;
	private String seq;
	
	
	
	public String getSeq() {
		return seq;
	}
	public void setSeq(String seq) {
		this.seq = seq;
	}
	public String getNumber500() {
		return number500;
	}
	public void setNumber500(String number500) {
		this.number500 = number500;
	}
	public Obiekt500(String status, String gniazdo, String wydzial, String number,String sequencie) {
		this.status = status;
		this.gniazdo = gniazdo;
		this.Wydzial = wydzial;
		this.number500 = number;
		this.seq = sequencie;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getGniazdo() {
		return gniazdo;
	}
	public void setGniazdo(String gniazdo) {
		this.gniazdo = gniazdo;
	}
	public String getWydzial() {
		return Wydzial;
	}
	public void setWydzial(String wydzial) {
		Wydzial = wydzial;
	}
	
	
	@Override
	public String toString() {
		return this.gniazdo ;
	}
	
	public String toString_werkpost() {
		return this.Wydzial ;
	}
	
	
	public String toString_previous()
	{
		return "Obiekt500 [" +this.number500 + ", status=" + this.status + ", gniazdo=" + this.gniazdo + ", Wydzial=" + this.Wydzial + "]" + ", sequencie= " + this.seq;
	}
	
	
	public void Show()
	{
		
	}
	
	
	
	
}
