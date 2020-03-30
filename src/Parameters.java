

public class Parameters {



	
	
	// do prob
//	private static String PathToSave = "C:/Users/el08/Desktop/programiki/godziny";
//	private static String PathToSaveHours = "C:/Users/el08/Desktop/programiki/godziny";
//	private static String PathToDB = "C:/Users/el08/Desktop/programiki/godziny";
	
	
	
	// proba zapisu do \\dataserver\Common\Raporty_godzin_test - sprawdzic czy
	 // przejdzie dla uzytkownika na serwerze .123
	private static String PathToSave = "//192.168.90.203/Common/TESTTEST/Excel/document.txt";

	
	
	public static String getPathToSave() {
		return PathToSave;
	}

	
}
