import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

/**
 * This is a class for the input and output of files.  It is intended it be generic
 * so that it can be used for future projects and expanded as needed.
 * @author patriciaanderson and Debraj Maity
 *
 */
public class FileUtils {

	private static String taskStr="";
	/**
	 * This method reads in a file at the given filePath location and puts each 
	 * line in a entry in an arrayList	
	 * @param filePath The path to the file (can be relative)
	 * @return each line of the file as its own entry in the arrayList
	 */
	public static ArrayList<String> readInFile(String filePath){
		ArrayList<String> fileLines = new ArrayList<String>();
		File file = new File(filePath);
		Scanner fileScanner;
		try {
			fileScanner = new Scanner(file);
			while(fileScanner.hasNext()){
				String nextLine = fileScanner.nextLine();
				fileLines.add(nextLine);
			}
			
			fileScanner.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		return fileLines;
	}
	
	/**
	 * This method goes through an arrayList and outputs one string per line to a 
	 * given file location
	 * @param filePath The path including the name of the file to be output
	 * @param fileOutput The arraylist containing the content of the file
	 */
	public static void outputFile(String filePath, ArrayList<String> fileOutput){
		try {
			PrintStream ps = new PrintStream(filePath);
		
		for(int i=0; i<fileOutput.size()-1;i++){
			ps.println(fileOutput.get(i));
		}
		if(!fileOutput.isEmpty()){
			ps.print(fileOutput.get(fileOutput.size()-1));
		}
		ps.close();
		
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public static String returnExecutionvector(ArrayList<Server> server , int numofService){
		Iterator<Server> iterate=server.iterator();
		Server s=null;
		String serviceExe="";
		String[] servicetypeExe=null;
		String[] serviceExeVal=new String[numofService];
		while(iterate.hasNext()){
			s=iterate.next();
			serviceExe=s.getServiceExecution();
			servicetypeExe=serviceExe.trim().split(";");
			//System.out.println("service:"+servicetypeExe.toString());
					
			for(int i=0;i<servicetypeExe.length;i++){
				if(serviceExeVal[i]!=null)
					serviceExeVal[i]+=","+servicetypeExe[i].substring(servicetypeExe[i].indexOf(",")+1,servicetypeExe[i].indexOf(")"));
				else
					serviceExeVal[i]=servicetypeExe[i].substring(servicetypeExe[i].indexOf(",")+1,servicetypeExe[i].indexOf(")"));
			}
			s=null;
			serviceExe="";
		}
		
		for(int i=0;i<numofService;i++){
			if(serviceExe!="")
				serviceExe=serviceExe+","+serviceExeVal[i];
			else
				serviceExe=serviceExeVal[i];
			
			//System.out.println(serviceExeVal[i]);
		}
		
		//System.out.println(serviceExe);
		return serviceExe;
	}

	public static String getTaskStr() {
		return taskStr;
	}

	public static void setTaskStr(String taskStr) {
		FileUtils.taskStr = taskStr;
	}
	
	

}
