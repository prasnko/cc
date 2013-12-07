import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.*;


public class Main {

	/**
	 * @param args
	 */
	/*public static void main(String[] args) {
		//1: NumResources: 5
		//2: T1{T2,T3},T4{T5,T6,T7},T8{T9,T10,T11,T12}
		//3: P=1,1.2,1.5,1.8,2
		//4: ExecutionTimeMatrix = 6 5 4 3.5 3 5 4.2 3.6 3 2.8 4 3.5 3.2 2.8 2.4
		//5: Wm=.5
		//6: Wt=.5
		//7: T0=100
		//8: M0=100
		ArrayList<String> input = FileUtils.readInFile("input.txt");
		String regex = "\t//";
		int r = Integer.parseInt(input.get(0).split(regex)[0]);
		String taskStr = input.get(1).split(regex)[0];
		String[] pStr = input.get(2).split(regex)[0].split(",");
		double[] p = new double[r];
		for(int i=0; i<pStr.length; i++){
			p[i] = Double.parseDouble(pStr[i]);
		}
		String[] etStr = input.get(3).split(regex)[0].split(",");
		
		double wm = Double.parseDouble(input.get(4).split(regex)[0]);
		double wt = Double.parseDouble(input.get(5).split(regex)[0]);
		double t0 = Double.parseDouble(input.get(6).split(regex)[0]);
		double m0 = Double.parseDouble(input.get(7).split(regex)[0]);
		
		
		ArrayList<task> tasks = makeTaskList(taskStr, r);
		double[] et = new double[r*tasks.size()];
		for(int i=0; i<etStr.length; i++){
			et[i] = Double.parseDouble(etStr[i]);
		}
		boolean[] a = new boolean[et.length];
		
		for(int i=0; i<tasks.size(); i++){
			double[] tci = new double[r];
			for(int j=0; j<r; j++){
				tci[j] = et[i*r+j];
			}
			tasks.get(i).allocate(tci,p, t0, m0, wm);
			for(int j=0; j<r; j++){
				a[i*r+j] = tasks.get(i).allocation[j];
			}
		}
		
		
	}*/
	
	public static void main(String args[]){
		Main main=new Main(); 
		System.out.println(main.getInitialAllocation());
	}
	
	/**
	 * @param args
	 */
	/*public boolean[] getInitialAllocation() {
		//1: NumResources: 5
		//2: T1{T2,T3},T4{T5,T6,T7},T8{T9,T10,T11,T12}
		//3: P=1,1.2,1.5,1.8,2
		//4: ExecutionTimeMatrix = 6 5 4 3.5 3 5 4.2 3.6 3 2.8 4 3.5 3.2 2.8 2.4
		//5: Wm=.5
		//6: Wt=.5
		//7: T0=100
		//8: M0=100
		ArrayList<String> input = FileUtils.readInFile("input.txt");
		String regex = "\t//";
		int r = Integer.parseInt(input.get(0).split(regex)[0]);
		String taskStr = input.get(1).split(regex)[0];
		String[] pStr = input.get(2).split(regex)[0].split(",");
		double[] p = new double[r];
		for(int i=0; i<pStr.length; i++){
			p[i] = Double.parseDouble(pStr[i]);
		}
		String[] etStr = input.get(3).split(regex)[0].split(",");
		
		double wm = Double.parseDouble(input.get(4).split(regex)[0]);
		double wt = Double.parseDouble(input.get(5).split(regex)[0]);
		double t0 = Double.parseDouble(input.get(6).split(regex)[0]);
		double m0 = Double.parseDouble(input.get(7).split(regex)[0]);
		
		
		ArrayList<task> tasks = makeTaskList(taskStr, r);
		double[] et = new double[r*tasks.size()];
		for(int i=0; i<etStr.length; i++){
			et[i] = Double.parseDouble(etStr[i]);
		}
		boolean[] a = new boolean[et.length];
		
		for(int i=0; i<tasks.size(); i++){
			double[] tci = new double[r];
			for(int j=0; j<r; j++){
				tci[j] = et[i*r+j];
			}
			tasks.get(i).allocate(tci,p, t0, m0, wm);
			for(int j=0; j<r; j++){
				a[i*r+j] = tasks.get(i).allocation[j];
			}
		}
		
		return a;
	}*/
	
	public boolean[] getInitialAllocation() {
		//1: NumResources: 5
		//2: T1{T2,T3},T4{T5,T6,T7},T8{T9,T10,T11,T12}
		//3: P=1,1.2,1.5,1.8,2
		//4: ExecutionTimeMatrix = 6 5 4 3.5 3 5 4.2 3.6 3 2.8 4 3.5 3.2 2.8 2.4
		//5: Wm=.5
		//6: Wt=.5
		//7: T0=100
		//8: M0=100
		
		
		ArrayList<Client> client=null;
		ArrayList<Server> server=null;
		ArrayList<Service> service=null;
		
		createClient cl=createClient.createClientObject();
		try{
			client=cl.generateClient("client.cfg");
		}catch(IOException E){
			System.out.println("ErrorGenerateClient:"+ E.getLocalizedMessage());
		}
		
		createServer sl=createServer.createServerObject();		
		try{
			server=sl.generateServer("server.cfg");
		}catch(IOException E){
			System.out.println("ErrorGenerateServer:"+ E.getLocalizedMessage());
		}
		
		createService sr=createService.createServiceObject();
		try{
			service=sr.generateService("service.cfg");
		}catch(IOException E){
			System.out.println("ErrorGenerateService:"+ E.getLocalizedMessage());
		}
		
		/*System.out.println(client);
		System.out.println(server);
		System.out.println(service);*/
		
		Iterator<Client> iterate=client.iterator();
		Client c=null;
		Map<String,String> clienttsk=new TreeMap<String,String>();
		String multiclient="";
		while(iterate.hasNext()){
			c=iterate.next();
			if (clienttsk.isEmpty())
				clienttsk.put(c.getService_id(),c.getClient_id());
			else {
					if (clienttsk.containsKey(c.getService_id()))
				{
				multiclient=clienttsk.get(c.getService_id())+","+c.getClient_id();
				clienttsk.put(c.getService_id(),multiclient);
				
				}else{
					clienttsk.put(c.getService_id(),c.getClient_id());
				} }
				
				
		}
		
		String taskStr="";
		for (Map.Entry<String, String> entry : clienttsk.entrySet()) {
		    System.out.println("key=" + entry.getKey() + ", value=" + entry.getValue());
		    if(taskStr!=""){
		    taskStr=taskStr+";"+entry.getKey()+":"+entry.getValue();
		    }else{
		    	taskStr=entry.getKey()+":"+entry.getValue();
		    }
		}
		
		//System.out.println(taskStr);
		FileUtils.setTaskStr(taskStr);
		//System.exit(0);
		System.out.println(FileUtils.getTaskStr());
		ArrayList<String> input = FileUtils.readInFile("input.txt");
		String regex = "\t//";
		//int r = Integer.parseInt(input.get(0).split(regex)[0]);
		int r = server.size();
		//System.out.println(r+"Server:"+server);
		//taskStr = input.get(1).split(regex)[0];
		String[] pStr = input.get(2).split(regex)[0].split(",");
		double[] p = new double[r];
		for(int i=0; i<pStr.length; i++){
			p[i] = Double.parseDouble(pStr[i]);
		}
		
		String[] etStr = FileUtils.returnExecutionvector(server, service.size()).split(",");
		//String[] etStr = input.get(3).split(regex)[0].split(",");
		
		double wm = Double.parseDouble(input.get(4).split(regex)[0]);
		double wt = Double.parseDouble(input.get(5).split(regex)[0]);
		double t0 = Double.parseDouble(input.get(6).split(regex)[0]);
		double m0 = Double.parseDouble(input.get(7).split(regex)[0]);
		
		
		ArrayList<task> tasks = makeTaskList(taskStr, r);
		double[] et = new double[r*tasks.size()];
		for(int i=0; i<etStr.length; i++){
			et[i] = Double.parseDouble(etStr[i]);
		}
		boolean[] a = new boolean[et.length];
		
		for(int i=0; i<tasks.size(); i++){
			double[] tci = new double[r];
			for(int j=0; j<r; j++){
				tci[j] = et[i*r+j];
			}
			tasks.get(i).allocate(tci,p, t0, m0, wm);
			for(int j=0; j<r; j++){
				a[i*r+j] = tasks.get(i).allocation[j];
			}
		}
		
		return a;
	}
	
	
	public static boolean[] processTask(task t, int r){
		boolean[] result = new boolean[r];
		if(t.subtasks.isEmpty()){
			return result;
		}
		else{
			
		}
		
		
		return result;
	}
	
	/*public static ArrayList<task> makeTasks(ArrayList<String> taskStrings, int r){
		ArrayList<task> tasks = new ArrayList<task>();
		Iterator<String> itr = taskStrings.iterator();
		while(itr.hasNext()){
			String task = itr.next();
			String[] taskSpl = task.split(":");
			if (taskSpl.length == 1){
				tasks.add(new task(taskSpl[0], r));
			}
			else if (taskSpl.length == 2){
				ArrayList<task> subtasks = new ArrayList<task>();
				String[] subtaskSpl = taskSpl[1].split(",");
				for(int i=0; i< subtaskSpl.length; i++){
					subtasks.add(new task(subtaskSpl[i], r));
				}
				tasks.add(new task(taskSpl[0], subtasks, r));
			}
		}
		Iterator<task> tItr = tasks.iterator();
		//while(tItr.hasNext()){
			//tItr.next().print("");
		//}
		return tasks;
	}*/
	
	
	public static ArrayList<task> makeTaskList(String taskStr, int r){
		ArrayList<task> tasks = new ArrayList<task>();
		
		String[] majorTaskStr = taskStr.split(";");
		for(int i=0; i<majorTaskStr.length; i++){
			ArrayList<task> subtasks = new ArrayList<task>();
			String totalTask = (majorTaskStr[i]);
			String[] taskSpl = totalTask.split(":");
			String id = taskSpl[0].replaceAll(",","");
			String[] subtaskSpl = taskSpl[1].split(",");
			for(int j=0; j< subtaskSpl.length; j++){
				subtasks.add(new task(subtaskSpl[j], r));
			}
			tasks.add(new task(id, subtasks, r));
		}
		
		return tasks;
	}
	
}

