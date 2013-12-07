import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;


public class createClient {
	
	private static createClient ref;
	
	private createClient(){
		
	}
   	
	public static createClient createClientObject(){
		 if (ref == null)
	          ref = new createClient();
	      return ref;
	}
	
	public ArrayList<Client> generateClient(String path) throws IOException{
		ArrayList<Client> cl=new ArrayList<Client>();
		InputStream fr= new FileInputStream(path);
		BufferedReader br= new BufferedReader(new InputStreamReader(fr));
		String Line=br.readLine();
		Client c=null;
		while (Line!=null){
			
		
			if(Line.contains("Client")){
		
			    c=new Client();
				c.setClient_id(Line.trim());
			}
			if(Line.contains("Chosen Service")){
				c.setService_id(Line.trim().substring(Line.indexOf("=")+1));
				cl.add(c);
				c=null;
			}
			
		Line=br.readLine();
			
		}
		
	
		return cl;
	}

}
