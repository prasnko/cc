import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;


public class createService {


private static createService ref;

private createService(){
	
}
	
public static createService createServiceObject(){
	 if (ref == null)
          ref = new createService();
      return ref;
}

public ArrayList<Service> generateService(String path) throws IOException{
	ArrayList<Service> sl=new ArrayList<Service>();
	InputStream fr= new FileInputStream(path);
	BufferedReader br= new BufferedReader(new InputStreamReader(fr));
	String Line=br.readLine();
	Service sr=null;
	while (Line!=null){
		
	
		if(Line.contains("Service ")){
	
		    sr=new Service();
			sr.setService_id(Line.trim());
			sl.add(sr);
			sr=null;
		}
		
		Line=br.readLine();
		
	}
	
	return sl;
}

}