import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;


public class createServer {
	
	private static createServer ref;
	
	private createServer(){
		
	}
   	
	public static createServer createServerObject(){
		 if (ref == null)
	          ref = new createServer();
	      return ref;
	}
	
	public ArrayList<Server> generateServer(String path) throws IOException{
		ArrayList<Server> sl=new ArrayList<Server>();
		InputStream fr= new FileInputStream(path);
		BufferedReader br= new BufferedReader(new InputStreamReader(fr));
		String Line=br.readLine();
		Server s=null;
		while (Line!=null){
			
		
			if(Line.contains("Server")){
		
			    s=new Server();
				s.setServer_id(Line.trim());
			}
			if(Line.contains("Price")){
				s.setPrice(Line.trim().substring(Line.indexOf("=")+1).trim());
			}
			if(Line.contains("serviceExecution")){
				s.setServiceExecution(Line.trim().substring(Line.indexOf("=")+1).trim());
				sl.add(s);
				s=null;
			}
			
			Line=br.readLine();
			
		}
		
		return sl;
	}

}
