import java.util.ArrayList;
import java.util.HashMap;


public class test {
	
	public static void main(String arg[]){
		HashMap<String,String> map=new HashMap<String,String>();
		map.put("Service1","Client1");
		if (map.containsKey("Service1"))
			System.out.println(map.get("Service1"));
		map.put("Service1","Client2");
		map.put("Service2","Client3");
		map.put("Service2","Client4");
		System.out.println(map);
	}


}
