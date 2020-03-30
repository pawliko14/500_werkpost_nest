import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main_v2 {

	private static String path = Parameters.getPathToSave();
	private static FileOutputStream  fos ;
	private static  OutputStreamWriter osw ;
	private static PrintWriter Nest_out ;
	private static PrintWriter WerkPost_out;
	
	private static List<String> project_list;
	
	public static void main(String[] args) throws SQLException, IOException {
		Connection conn=DriverManager.getConnection("jdbc:mariadb://192.168.90.123/fatdb","listy","listy1234");

		project_list = ReadProjectListFromFile();
		
		Nest_out = new PrintWriter("Nest.txt");
		WerkPost_out = new PrintWriter("Worknotes.txt");
		
//		for(String l : project_list)
//		{
//			System.out.println(l);
//		}
		
		for(int i = 0 ; i < project_list.size() ; i++)
		{	
			search_over(conn,i);
		}
	
	//	search_over(conn, 1);
		
		Nest_out.close();
		WerkPost_out.close();
		
	}
	
	static List<String> ReadProjectListFromFile() throws IOException
	{
		 List<String> list = null;
		    URI uri = null;

		    try {
		        uri = ClassLoader.getSystemResource("List_of_500.txt").toURI();
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
	
	
	static void search_over(Connection conn, int index) throws SQLException
	{
		List<Obiekt500> ListaObiekt = null;
		
		String[] s = project_list.get(index).split("/");
		String p1 = s[0];
		String p2 = s[1];
		
	//	 p1 = "500";
	//	 p2 = "187490";
		
		String sql2 = "select AFDELINGSEQ, SEQ, WERKPOST,STATUS, HOEVEELHEID,NEST from werkbon\r\n" + 
				"where afdeling = '"+p1+"'\r\n" + 
				"and AFDELINGSEQ = '"+p2+"'\r\n" + 
				"order by SEQ";
		
		Statement s2 = conn.createStatement();
		ResultSet rs2 = s2.executeQuery(sql2);

		if(!rs2.isBeforeFirst())
		{
			System.out.println("there is no data for that 500!");
			Nest_out.println("No data");
			WerkPost_out.println("No data");

		}
		else
		{
			ListaObiekt = new ArrayList<>();
			
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
			}
			s2.close();
			rs2.close();
			
			
			
//			for(Obiekt500 o : ListaObiekt)
			//	{
				//	System.out.println(o.toString());
				//}
				
				System.out.println(	SearchAlgorythm(ListaObiekt));
				System.out.println(	SearchAlgorythm_werkpost(ListaObiekt));

				Nest_out.println(		SearchAlgorythm(ListaObiekt));
				WerkPost_out.println(    SearchAlgorythm_werkpost(ListaObiekt));
				
	}
		
	
	}
	
	
	public static String SearchAlgorythm(List<Obiekt500> lista)
	{

		// if all records are White
		
	//	int size = lista.size();
		
	//	String temporary = null;
		
		int sumof10 = 0;
		int sumof90 = 0;
		int sumof20 = 0;
		for(int i = 0 ; i < lista.size(); i++)
		{
			
			if(lista.get(i).getStatus().equals("10"))
					{
						sumof10++;
					}
				else if ( lista.get(i).getStatus().equals("90"))
					{
						sumof90++;
					}
				else if( lista.get(i).getStatus().equals("20"))
					{
						sumof20++;
					}
		}
		
		// if only G
		if(sumof90 == lista.size() &&  sumof10 == 0 && sumof20 == 0)
		{
			return lista.get(lista.size()-1).toString();
		}
		else if (sumof10 == lista.size() &&  sumof90 == 0 && sumof20 == 0)
		{
			return "only white";
		}
		else if (sumof20 == lista.size() &&  sumof90 == 0 && sumof10 == 0)
		{
			return lista.get(lista.size()-1).toString();
		}
		else // MIXED
		{
			if(sumof20 >= 1)
			{
				if( sumof20 == 1)
				{
					String temp = null;
						for(int i = 0 ; i < lista.size();i++)
						{
							if(lista.get(i).getStatus().equals("20"))
							{
								temp = lista.get(i).toString();
							}
						}
					return temp;
				}
				else if(sumof20 >= 2)
				{
					Map<String,String>  temp_map= new TreeMap<String,String>();
					
					List<String> listaa_wydzial = new ArrayList<>();
					List<String> listaa_seq = new ArrayList<>();
					
					for(int i = 0 ; i < lista.size();i++)
					{
						if(lista.get(i).getStatus().equals("20"))
						{
							
							if(lista.get(i).toString() == null)
							{
									System.out.println("problem"  );
									String kop = "KOP prblm";
									temp_map.put(kop,lista.get(i).getSeq());
							}
							else
							{
								temp_map.put(lista.get(i).toString(),lista.get(i).getSeq());
							}
							
						}
					}		

					
			
					
					return maxUsingCollectionsMax(temp_map);
					
				}
			}
			else // ONLY GREENS AND WHITES
			{
				
				if(sumof10 == 1 && sumof90 >=1)
				{
					String LastOne90 = lista.get(lista.size()-1).getStatus();
					
					if(LastOne90.equals("10"))
					{
						for(int i = 1 ; i < lista.size(); i++)
						{
						 if(lista.get(i-1).getStatus().equals("90") && lista.get(i).getStatus().equals("10"))
							{			 
								return lista.get(i).toString();
							}	
						 }
						}
					else
					{
						Map<String,String>  temp_map= new TreeMap<String,String>();
						
						for(int i = 0 ; i < lista.size();i++)
						{
							if(lista.get(i).getStatus().equals("90"))
							{
								
								if(lista.get(i).toString() == null)
								{
										System.out.println("problem"  );
										String kop = "val prblm";
										temp_map.put(kop,lista.get(i).getSeq());
								}
								else
								{
									temp_map.put(lista.get(i).toString(),lista.get(i).getSeq());
								}
								
							
							}
						}						
					//	System.out.println(maxUsingCollectionsMax(temp_map));
						
						return maxUsingCollectionsMax(temp_map);
					}
				}
				else if(sumof10 >= 2  && sumof90 >=1)
				{
					
						Map<String,String>  temp_map= new TreeMap<String,String>();
						List<Integer> seq_list = new ArrayList<>();
					
					for(int i = 0 ; i < lista.size();i++)
					{
						if(lista.get(i).getStatus().equals("90"))
						{
							
							
							
							if(lista.get(i).toString() == null)
							{
									System.out.println("problem"  );
									String kop = "KOP prblm";
							//		temp_map.put(kop,lista.get(i).getSeq());
									
									temp_map.put(kop,lista.get(i).getSeq());
									seq_list.add(Integer.parseInt(lista.get(i).getSeq()));
									
							}
							else
							{
								//temp_map.put(lista.get(i).toString(),lista.get(i).getSeq());
								
								temp_map.put(lista.get(i).toString(),lista.get(i).getSeq());
								seq_list.add(Integer.parseInt(lista.get(i).getSeq()));
							}
							
						}
					}	
					
					int maxxx = Collections.max(seq_list) / 10;
					
					
					
					
				
					String werkopost_temp = maxUsingCollectionsMax(temp_map);
					String seq_temp = temp_map.get(werkopost_temp);
					
					
					int seq_temp_divided_by_10 = Integer.parseInt(seq_temp) / 10;
					
					
					if(lista.size() > maxxx)
					{

						return(lista.get(maxxx ).toString());
					}
					else
					{

						return maxUsingCollectionsMax(temp_map);
					}
					
				//	return maxUsingCollectionsMax(temp_map);
					
				}
			}
			
		}
		
		
		
		
		return null;
	
		
		
	}
	
	public static String SearchAlgorythm_werkpost(List<Obiekt500> lista)
	{
		// if all records are White
		
	//	int size = lista.size();
		
	//	String temporary = null;
		
		int sumof10 = 0;
		int sumof90 = 0;
		int sumof20 = 0;
		for(int i = 0 ; i < lista.size(); i++)
		{
			
			if(lista.get(i).getStatus().equals("10"))
					{
						sumof10++;
					}
				else if ( lista.get(i).getStatus().equals("90"))
					{
						sumof90++;
					}
				else if( lista.get(i).getStatus().equals("20"))
					{
						sumof20++;
					}
		}
		
		// if only G
		if(sumof90 == lista.size() &&  sumof10 == 0 && sumof20 == 0)
		{
			return lista.get(lista.size()-1).toString_werkpost();
		}
		else if (sumof10 == lista.size() &&  sumof90 == 0 && sumof20 == 0)
		{
			return "only white";
		}
		else if (sumof20 == lista.size() &&  sumof90 == 0 && sumof10 == 0)
		{
			return lista.get(lista.size()-1).toString_werkpost();
		}
		else // MIXED
		{
			if(sumof20 >= 1)
			{
				if( sumof20 == 1)
				{
					String temp = null;
						for(int i = 0 ; i < lista.size();i++)
						{
							if(lista.get(i).getStatus().equals("20"))
							{
								temp = lista.get(i).toString_werkpost();
							}
						}
					return temp;
				}
				else if(sumof20 >= 2)
				{
					Map<String,String>  temp_map= new TreeMap<String,String>();
					
					for(int i = 0 ; i < lista.size();i++)
					{
						if(lista.get(i).getStatus().equals("20"))
						{
							if(lista.get(i).toString() == null)
							{
									System.out.println("problem"  );
									String kop = "KOP prblm";
									temp_map.put(kop,lista.get(i).getSeq());
							}
							else
							{
								temp_map.put(lista.get(i).toString_werkpost(),lista.get(i).getSeq());
							}
						}
					}						

					
					
					
				
					
					
					return maxUsingCollectionsMax(temp_map);
					
				}
			}
			else // ONLY GREENS AND WHITES
			{
				
				if(sumof10 == 1 && sumof90 >=1)
				{
					String LastOne90 = lista.get(lista.size()-1).getStatus();
					
					if(LastOne90.equals("10"))
					{
						for(int i = 1 ; i < lista.size(); i++)
						{
						 if(lista.get(i-1).getStatus().equals("90") && lista.get(i).getStatus().equals("10"))
							{			 
								return lista.get(i).toString_werkpost();
							}	
						 }
						}
					else
					{
						Map<String,String>  temp_map= new TreeMap<String,String>();
						
						for(int i = 0 ; i < lista.size();i++)
						{
							if(lista.get(i).getStatus().equals("90"))
							{
								temp_map.put(lista.get(i).toString_werkpost(),lista.get(i).getSeq());
							}
						}						
					//	System.out.println(maxUsingCollectionsMax(temp_map));
						
						return maxUsingCollectionsMax(temp_map);
					}
				}
				else if(sumof10 >= 2  && sumof90 >=1)
				{
					
						Map<String,String>  temp_map= new TreeMap<String,String>();
						List<Integer> seq_list = new ArrayList<>();
					
					for(int i = 0 ; i < lista.size();i++)
					{
						if(lista.get(i).getStatus().equals("90"))
						{
							try
							{
							temp_map.put(lista.get(i).toString_werkpost(),lista.get(i).getSeq());
							seq_list.add(Integer.parseInt(lista.get(i).getSeq()));
							}
							catch(Exception e)
							{
								System.out.println("jakis problem?");
							}
						}
					}	
					
					int maxxx = Collections.max(seq_list) / 10;
					
					
					
					
				
					String werkopost_temp = maxUsingCollectionsMax(temp_map);
					String seq_temp = temp_map.get(werkopost_temp);
					
					
					int seq_temp_divided_by_10 = Integer.parseInt(seq_temp) / 10;
					
					
					if(lista.size() > maxxx)
					{

						return(lista.get(maxxx ).toString_werkpost());
					}
					else
					{

						return maxUsingCollectionsMax(temp_map);
					}
					
				//	return maxUsingCollectionsMax(temp_map);
					
				}
			}
			
		}
		
		
		
		
		return null;
	}
	
	public static <K, V extends Comparable<V>> K maxUsingCollectionsMax(Map<K, V> map) {
	    Entry<K, V> maxEntry = Collections.max(map.entrySet(), new Comparator<Entry<K, V>>() {
	        public int compare(Entry<K, V> e1, Entry<K, V> e2) {
	            return e1.getValue()
	                .compareTo(e2.getValue());
	        }
	    });
	    return maxEntry.getKey();
	}
	
}
