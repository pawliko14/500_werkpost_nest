import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class main {
	
	private static FileWriter fileWriter;
	private static File file;
	
	
	private static FileOutputStream  fos ;
	private static  OutputStreamWriter osw ;
	
	
	private static String path = Parameters.getPathToSave();
	
	private static List<String> project_list;
	
	private static List<HashMap<String,String>> completeList;

	public static void main(String[] args) throws SQLException, IOException {
		
		 file = new File(path);
		 
		  fos = new FileOutputStream(file);
		 
		  osw = new OutputStreamWriter(fos);
		 
	//	 fileWriter = new FileWriter(file);
		
		project_list = new ArrayList<String>();

		completeList = new ArrayList<HashMap<String,String>>();
		
		
		// TODO Auto-generated method stub
		System.out.println("work");
		Connection conn=DriverManager.getConnection("jdbc:mariadb://192.168.90.123/fatdb","listy","listy1234");
	//	fun(conn);
	//	getAllProjects(conn);   // <- directly from database
		
		
		
		project_list = ReadProjectListFromFile();
		
		// temporary to speed up process of retriving data from database
		printList(project_list);
		
		String pnumer = "15/59";  //   4/751   // 5/916
		System.out.println("Data from project: "+ pnumer);
	
		SearchOverOneProject(conn, pnumer);
		
		
		
//		for(int i = 0; i < project_list.size();i++) {
//			SearchOverOneProject(conn, project_list.get(i));
//		}
		osw.close();

	}

	
//	static void fun(Connection conn) throws SQLException
//	{
//		String ss= null;	
//		String sql = "select * from werkbon\r\n" + 
//				"where afdeling = '500'\r\n" + 
//				"and AFDELINGSEQ = '183266'\r\n" + 
//				"order by SEQ ;";
//	
//		Statement e = conn.createStatement();
//		try {
//			e = conn.createStatement();
//		} catch (SQLException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//		ResultSet rs = e.executeQuery(sql);
//		while(rs.next()) {
//			ss= rs.getString("WERKPOST");
//			System.out.println(ss);
//		}		
//		rs.close();
//		e.close();
//	}
	
	static void getAllProjects(Connection conn) throws SQLException
	{
		String ss = null;
		String sql = "select OrderNo from partsoverview group by OrderNo";
		
		Statement e = conn.createStatement();
		try {
			e = conn.createStatement();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		ResultSet rs = e.executeQuery(sql);
		while(rs.next()) {
			ss= rs.getString("OrderNo");
			System.out.println(ss);
		}		
		rs.close();
		e.close();
		
	}
	
	static List<String> ReadProjectListFromFile() throws IOException
	{
		 List<String> list = null;
		    URI uri = null;

		    try {
		        uri = ClassLoader.getSystemResource("List_of_projects.txt").toURI();
		    } catch (URISyntaxException e) {
		     //   LOGGER.error("Failed to load file.", e);
		    }

		    try (Stream<String> lines = Files.lines(Paths.get(uri))) {
		        list = lines.collect(Collectors.toList());
		    } catch (IOException e) {
		     //   LOGGER.error("Failed to load file.", e);
		    }
		    return list;
		
	}
	
	static void printList(List<String> l)
	{
		for(String list : l)
		{
			System.out.println(list);
		}
	}
	
	static void SearchOverOneProject(Connection conn, String project) throws SQLException, IOException
	{
		List<String> list_of_podprojects = new ArrayList<String>();
		
		String sql = "select * from partsoverview\r\n" + 
				"where OrderNo = '"+project+"'\r\n" + 
				"and MatSource <> 'Nie uruchomione'\r\n" + 
				"and MatSource <> 'Na magazynie'\r\n" + 
				"and MatSource like '500/%'";
		
		Statement e = conn.createStatement();
		try {
			e = conn.createStatement();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		ResultSet rs = e.executeQuery(sql);
		
		if(!rs.isBeforeFirst())
		{
			System.out.println("there is no data for that project!");
		}
		else
		{
			while(rs.next()) 
			{
				String sql2  = rs.getString("MatSource");
				list_of_podprojects.add(sql2);
			}	
		}
		
		for( String l : list_of_podprojects)
		{
			System.out.println(l);
		}
		e.close();
		rs.close();
		
		// search over project
		
		for(int i = 0 ; i < list_of_podprojects.size(); i++)
		{
			String[] s = list_of_podprojects.get(i).split("/");
			String p1 = s[0];
			String p2 = s[1];
			
			
			//System.out.println("SS:" + p1 + " ss2: "+ p2);
			
			String sql2 = "select AFDELINGSEQ, SEQ, WERKPOST,STATUS, HOEVEELHEID,NEST from werkbon\r\n" + 
					"where afdeling = '"+p1+"'\r\n" + 
					"and AFDELINGSEQ = '"+p2+"'\r\n" + 
					"order by SEQ";
			
			Statement s2 = conn.createStatement();
			ResultSet rs2 = s2.executeQuery(sql2);

			if(!rs2.isBeforeFirst())
			{
				System.out.println("there is no data for that 500!");
			}
			else
			{
				List<Obiekt500> ListaObiekt = new ArrayList<>();
				
				while(rs2.next()) 
				{
					
					String werkpost  = rs2.getString("WERKPOST");
					String status  = rs2.getString("STATUS");
					String afdelingseg = rs2.getString("AFDELINGSEQ");
					String seq = rs2.getString("SEQ");
					String nest = rs2.getString("NEST");
					String hoeveelheid = rs2.getString("HOEVEELHEID");
					
					
					Obiekt500 obiekt = new Obiekt500( status, nest, werkpost, p2,seq);
					
					ListaObiekt.add(obiekt);
					
//					
//					if(status.equals("10"))
//					{
//						System.out.println("werkpost: "+ werkpost + " status: " + status + " 500: "+ list_of_podprojects.get(i) + " seq: " + seq + " NEST: "+ nest);
//						
//						String sss= "werkpost: "+ werkpost + " status: " + status + " 500: "+ list_of_podprojects.get(i) ;
//						
//						osw.write(sss);
//						
//						
//					//	fileWriter.write(sss);
//						//fileWriter.write("\n");
//					//	fileWriter.flush();
//						
//						break;
//					}
//					else if(status.equals(("20")))
//					{
//							System.out.println("werkpost: "+ werkpost + " status: " + status + " 500: "+ list_of_podprojects.get(i) + " seq: " + seq + " NEST: "+ nest);
//						
//						String sss= "werkpost: "+ werkpost + " status: " + status + " 500: "+ list_of_podprojects.get(i) ;
//					}
//					else
//					{
//						if(rs2.isLast())
//						{
//							System.out.println("werkpost: "+ werkpost + " status: " + status + " 500: "+ list_of_podprojects.get(i) + " seq: " + seq + " NEST: "+ nest);
//						}
//					}
					
				}
				
				// print object in list
				
//				System.out.println("ilosc w liscie: " + ListaObiekt.size());
//				
//				for(Obiekt500 o : ListaObiekt)
//				{		
//					System.out.println(o.toString());
//				}
				
			System.out.println(	SearchAlgorythm(ListaObiekt));
				
				
			}
			
			
		}
		
		
	}
	
	public static String SearchAlgorythm(List<Obiekt500> lista)
	{
		// if all records are White
		
		int size = lista.size();
		
		
		int sumOf10 = 0;
		int sumof90 = 0;
		for(int i = 0 ; i < lista.size(); i++)
		{
			
			if(lista.get(i).getStatus().equals("10"))
					{
					sumOf10++;
					}
			else if ( lista.get(i).getStatus().equals("90"))
				{
					sumof90++;
				}
		}
		
		
		// all white records
			if( size == sumOf10)
			{
				System.out.println("all white record");
				
				// if white completly remove whole record
				// only started worknotes matter
			}
			else
			{
				
				// all green - 90  -> G G G ||
				if( size == sumof90)
				{
					System.out.println("all Green");
					
				//	System.out.println(lista.get(lista.size()-1).toString());
					// break here
					return lista.get(lista.size()-1).toString();
				}
				
				//  G G ( W )  W ||
			
				
				// if  serie of green occurs yellow reocrd, take yellow location
				
				for(int i = 1 ; i < lista.size()-1; i++)
				{

					
					if(lista.get(i-1).getStatus().equals("90") && lista.get(i).getStatus().equals("20"))
					{
						System.out.println("serie of green occurs yellow reocrd, take yellow location");
					//	System.out.println(lista.get(i).toString());
						
						// break here
						return lista.get(i).toString();
					}
					
					//if serie of green occurs White take first one

					else if(lista.get(i-1).getStatus().equals("90") && lista.get(i).getStatus().equals("10"))
					{
						System.out.println("serie of green occurs yellow reocrd, take yellow location");
					//	System.out.println(lista.get(i).toString());
						
						// break here
						return lista.get(i).toString();
					}
					
				}
				
				
		
				
				
				
			}
			return null;
		
		
	}
	
	
}
