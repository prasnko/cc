import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;


public class task {
	String id = "";
	ArrayList<task> subtasks = new ArrayList<task>();
	boolean finished = false;
	boolean actAlloc = false;
	boolean[] allocation;
	int r = 0;

	public task(String s, int rs){
		id = s;
		allocation = new boolean[rs];
		r = rs;
	}

	public task(String s, ArrayList<task> st, int rs){
		id = s;
		subtasks = st;
		allocation = new boolean[rs];
		r = rs;
	}

	public void allocate(double[] tc, double[] p, double t0, double m0, double wm){
		if(subtasks.isEmpty()){
			finished = true;
		}
		else{
			Iterator<task> itr = subtasks.iterator();
			while(itr.hasNext()){
				itr.next().allocate(tc, p, t0, m0, wm);
			}

			double[] pt = new double[r];
			for(int i=0; i<r; i++){
				pt[i] = tc[i]*p[i];
			}
			ArrayList<String> constraints = new ArrayList<String>();
			constraints.add(generateMinFunction(pt, wm));

			constraints.addAll(generateBasicConstraints());
			constraints.addAll(generateSpecialConstraints());
			constraints.addAll(generateMaximumConstraints(tc));
			constraints.addAll(generateLimitConstraints(pt, m0, t0));
			constraints.addAll(generateVariables());
			printArray(constraints);
			FileUtils.outputFile("input.lp" , constraints);
			executeAlgorithm();
		}
	}

	private void executeAlgorithm(){
		ArrayList<String> output = new ArrayList<String>();
		Process p;
		try {
			String[] cmd = {"./lp_solve", "-S3", "input.lp", ">output.txt"};

			p = Runtime.getRuntime().exec(cmd);
			//p = Runtime.getRuntime().exec("pwd");
			String s;
			BufferedReader stdInput = new BufferedReader(new 
					InputStreamReader(p.getInputStream()));

			BufferedReader stdError = new BufferedReader(new 
					InputStreamReader(p.getErrorStream()));

			boolean inImportantSection = false;
			// read the output from the command
			while ((s = stdInput.readLine()) != null) {
				//System.out.println(s);
				if(inImportantSection){
					if(s.equals("")){
						break;
					}
					output.add(s);
				}
				else if(s.equals("Actual values of the variables:")){
					inImportantSection=true;
				}
			}

			// read any errors from the attempted command
			System.out.println("Here is the standard error of the command (if any):\n");
			while ((s = stdError.readLine()) != null) {
				System.out.println(s);
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		processOutput(output);

	}
	
	private void processOutput(ArrayList<String> output){
		
		int numVars = r*subtasks.size();
		for(int i=0; i<numVars; i++){
			if(output.get(i).substring(output.get(i).length()-1).equals("1")){
				allocation[i%r] = true;
			}
		}
		System.out.println(Arrays.toString(allocation));
	}
	public ArrayList<String> generateLimitConstraints(double[] pt, double m0, double t0){
		ArrayList<String> constraints = new ArrayList<String>();
		int numVars = r*subtasks.size();
		constraints.add("2 x" + numVars + " <= " + t0 + ";" );
		String constraint = "";
		for(int i=0; i<numVars; i++){
			constraint = constraint + pt[i%r] + " x" + i + " + ";
		}
		constraint = constraint.substring(0, constraint.length()-2) + "<=" + m0 +";";
		constraints.add(constraint);
		return constraints;
	}
	public ArrayList<String> generateSpecialConstraints(){
		ArrayList<String> constraints = new ArrayList<String>();
		for(int i=0; i<subtasks.size(); i++){
			task t = subtasks.get(i);
			if(t.actAlloc){
				for(int j=0; j<r; j++){
					int x = r*i+j;
					String constraint = "x" + x + " = " + allocation[j] + ";";
					constraints.add(constraint);
				}
			}
		}




		return constraints;
	}

	public String generateMinFunction(double[] pt, double wm){
		int numVars = r*subtasks.size();
		String function = "min: ";
		for(int i=0; i<numVars; i++){
			function = function + (wm*pt[i%r]) + " x" + i + " + ";
		}
		function = function + "x" + numVars + ";";


		return function;
	}

	public ArrayList<String> generateBasicConstraints(){
		ArrayList<String> constraints = new ArrayList<String>();
		String constraint = "";

		//generate horizontal constraints
		for(int j=0; j<subtasks.size(); j++){
			for (int i=0; i<r-1; i++){
				constraint = constraint + "x" + (j*r + i) + " + ";
			}
			constraint = constraint + "x" + (j*r + r-1) + " = 1;";
			constraints.add(constraint);
			constraint = "";
		}

		//generate vertical constraints
		for(int i=0; i<r; i++){
			for (int j=0; j<subtasks.size()-1; j++){
				constraint = constraint + "x" + (i + r*j) + " + ";
			}
			constraint = constraint + "x" + (i + r*(subtasks.size()-1)) + " <= 1;";
			constraints.add(constraint);
			constraint = "";
		}

		return constraints;
	}

	public ArrayList<String> generateMaximumConstraints(double[] tc){
		ArrayList<String> constraints = new ArrayList<String>();
		int numVars = r*subtasks.size();
		for(int i=0; i<numVars; i++){
			int j = i%r;
			constraints.add("x" + numVars + " >= " + tc[j] + " x" + i + ";");
		}
		return constraints;
	}

	public ArrayList<String> generateVariables(){
		ArrayList<String> variables = new ArrayList<String>();
		int numVar = r*subtasks.size();
		String bin = "bin ";
		for(int i=0; i<numVar; i++){
			bin = bin + " x" + i + ","; 
		}
		bin = bin.substring(0, bin.length()-1) + ";";
		variables.add(bin);

		bin = "sec x" + numVar + ";";
		variables.add(bin);
		return variables;

	}


	public void print(String spacing){
		System.out.println(spacing + id);
		Iterator<task> itr = subtasks.iterator();
		while(itr.hasNext()){
			itr.next().print(spacing + "\t");
		}
	}

	public void printArray(ArrayList array){
		Iterator itr = array.iterator();
		while(itr.hasNext()){
			System.out.println(itr.next());
		}
	}
}
