import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ObjectInputStream.GetField;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ResourceAllocation {
	static Map<Integer,ArrayList<String> > servClient = new HashMap<Integer, ArrayList<String> >();
	static ArrayList<Resource> Resources = new ArrayList<Resource>();
	static ArrayList<Resource> AllocationResources;
	static ArrayList<Subtask> Subtasks = new ArrayList<Subtask>();
	static ArrayList<Subtask> AllocationSubtasks;			
	static Map<Integer,ArrayList<Resource>> AllocationK = new HashMap<Integer,ArrayList<Resource>>(); 
	static Map<Integer,ArrayList<Subtask>> AllocationR = new HashMap<Integer,ArrayList<Subtask>>();
	private static ArrayList<ArrayList<Resource>> AllocationResourcesArray = new ArrayList<ArrayList<Resource>>();
	private static ArrayList<ArrayList<Subtask>> AllocationSubtasksArray = new ArrayList<ArrayList<Subtask>>();
	//static double[][] turnaroundTime = new double[50][50];
	///static double[][] expense = new double[50][50];
	//static int c[][] = {{0,0,0,1,1},{0,0,1,1,1},{1,1,1,0,1}};
	static double turnaroundTime[][] = new double[3][5]; //= {{0,0,0,7,9},{0,0,7.2,6,8.4},{4,3.5,6.4,0,7.2}};
	static double expense[][] = new double[3][5];//={{0,0,0,6.3,6},{0,0,5.4,5.4,5.6},{4,4.2,4.8,0,4.8}};	
	static double tHat[][] ={{6,5,4,3.5,3},{5,4.2,3.6,3,2.8},{4,3.5,3.2,2.8,2.4}};
	static double price[] = {1,1.2,1.5,1.8,2};
	private static double wt = 0.5;
	private static double we = 0.5;
	public static double computeUtility2(int[][] b,int i)
	{
		double maxTT = 0;
		double totalExp = 0;
		for(int j=0;j<Resources.size();j++)
		{
			double allocTT = (b[i][j])*(turnaroundTime[i][j]);
			if(maxTT<allocTT)
				maxTT = allocTT;
			totalExp+=(b[i][j])*(expense[i][j]);			
		}
		double cost = (we*totalExp)+(wt*maxTT);
		double utility = 1.0/cost;
		return utility;
	}
	
	public static double computeUtility(int[][] b,int i)
	{
		double maxTT = 0;
		double totalExp = 0;
		double numR;
		
		for(int j=0;j<Resources.size();j++)
		{
			numR = 0;
			for(int k=0;k<Subtasks.size();k++)
				numR += b[k][j];
			if(numR==0)
				numR=1;
			double allocTT = (b[i][j])*(tHat[i][j])*(numR);
			if(maxTT<allocTT)
				maxTT = allocTT;
			totalExp+=(b[i][j])*(tHat[i][j])*(price[j]);			
		}
		double cost = (totalExp)+(maxTT);
		double utility = 1.0/cost;
		return utility;		
	}
	
	public static void reallocate(int[][] c,int i,int p, int q)
	{
		/*for(int x=0;x<b.length;x++)
		{
			for(int y=0;y<b[x].length;y++)
			{
				c[x][y] = b[x][y];
			}
		}*/
		if(c[i][q]!=1)
		{
			c[i][p] = 0;
			c[i][q] = 1;
		}
		
	}

	public static void printMatrix(int[][] b,String method,String matrix)
	{
		System.out.println("");
		System.out.println("");
		System.out.println(method);
		System.out.println(matrix);
		for(int i=0;i<b.length;i++)
		{
			System.out.println("");
			for(int j=0;j<b[i].length;j++)
				System.out.print(b[i][j]);
		}
		System.out.println("");
	}
	
	public static double computeSPELR(int[][] b,int i,int p,int q)
	{
	//	printMatrix(b,"computeSPELR","b");
		double utility1 = computeUtility(b, i);
		int[][] c = new int[3][5];
		//System.arraycopy(b, 0, c, 0, b.length);
		for(int x=0;x<b.length;x++)
		{
			for(int y=0;y<b[x].length;y++)
				c[x][y] = b[x][y];
		}
		reallocate(c,i,p,q);
	//	System.out.println("Subtask id: "+i);
	//	System.out.println("Allocating "+p+" to "+q);
	//	printMatrix(b,"computeSPELR","b");
	//	printMatrix(c,"computeSPELR","c");
		double utility2 = computeUtility(c, i);
		double SPELR = utility1 - utility2;
		return SPELR;
	}
	
	public static double computeGELR(int[][] b,int[][] c,ArrayList<Subtask> subtasks)
	{
		double utility1,utility2;
		utility1 = utility2 = 0;
		for(int k=0;k<subtasks.size();k++)
		{
			utility1 += computeUtility(b, k);
			utility2 += computeUtility(c, k);			
		}
		return (utility1-utility2);
	}
	
	public static int MinSingle(int[][] b, int i,int p)
	{
		double currentSPELR,minSPELR;
		minSPELR = 0;
		int q= -1;
		for(int j=0;j<Resources.size();j++)
		{
			if(j!=p)
			{
				currentSPELR = computeSPELR(b, i, p, j);
				if(minSPELR==0)
					{
						minSPELR = currentSPELR;
						if(minSPELR<0)
						 q=j;
					}
				else if(minSPELR>currentSPELR)
				{
					minSPELR = currentSPELR;
					if(minSPELR<0)
					 q = j;
				}
				
			}			
		}
		return q;
	}
	
	public static int  MinGlobal(int[][] b,int j)
	{
		ArrayList<Subtask> subtasks = AllocationR.get(Resources.get(j).getID());
		ArrayList<Integer> nsts = new ArrayList<Integer>();
		ArrayList<Double> val = new ArrayList<Double>();
		int i = -1;
		double[] GELR = new double[50];
		
		int q = -1;	
		for(int k=0;k<subtasks.size();k++)
		{
			//System.out.println("MinGlobal");
			q = MinSingle(b, subtasks.get(k).getID()-1, j);
			if(q!=-1)
			{
				//printMatrix(b,"MinGlobal","b");
				int[][] c = new int[3][5];
				for(int x=0;x<b.length;x++)
				{
					for(int y=0;y<b[x].length;y++)
						c[x][y] = b[x][y];
				}
				double utility1 = computeUtility(b, subtasks.get(k).getID()-1);
				reallocate(c, subtasks.get(k).getID()-1, j, q);
				//printMatrix(b,"MinGlobal","b");
				//printMatrix(c,"MinGlobal","c");
				double utility2 = computeUtility(c, subtasks.get(k).getID()-1);
				if(utility1-utility2<0)
				{
					nsts.add(subtasks.get(k).getID()-1);
					val.add(utility1-utility2);
				}
				GELR[k] = computeGELR(b,c,Subtasks);
			}
		}
		double minGELR = 0;
			for(int n=0;n<nsts.size();n++)
			{
				if(minGELR == 0)
				{ 
					minGELR = GELR[nsts.get(n)];
					if(minGELR<0)
						i = nsts.get(n);
				}
				else if(minGELR>GELR[nsts.get(n)])
				{
					minGELR = GELR[nsts.get(n)];
					if(minGELR<0)
						i =nsts.get(n);
				}
			}
		
		return i;		
	}
	
	
	public static void EvolutionaryOptimize(int[][] b)
	{
		int i=0;
		//int[][] b = new int[3][5];
		Boolean flag = true;
		ArrayList<Resource> ms;
		int q=-1;
		int p=-1;
		int s=0;
		
				
		printMatrix(b, "", "Initial subtask allocation Matrix:");
		while(flag && i< Subtasks.size())
		{
			if(i==0 && s>0)
				flag = false;
			ms = AllocationK.get((Subtasks.get(i).getID()));
			//System.out.println(AllocationK);
			for(int j=0;j<ms.size();j++)
			{
				//to sort bi based on ti 
				//System.out.println(j);
				q = MinGlobal(b, ms.get(j).getID()-1);
				if(q!=-1)
				{
					//System.out.println("EvolutionaryOptimize");
					int first = ms.get(j).getID()-1;
					p = MinSingle(b, q, ms.get(j).getID()-1);
					if(p!=-1)
					 {
						reallocate(b, q, ms.get(j).getID()-1, p);
						System.out.println("");
						System.out.println("Allocating Subtask "+(q+1)+" from Resource "+(ms.get(j).getID())+" to Resource  "+(p+1));
						flag = true;
						printMatrix(b, "Evolutionary Optimize", "b");
					 }			
					
				}				
			}
			if(i==Subtasks.size()-1)
			{
				if(!flag)
					break;
				else
					{
						i=0;
						s++;
					}
			}
			else
				i+=1;
		}
		//printMatrix(b, "EO", "final");
		printOutput(b);
	}
	
	public static void printOutput(int[][] b)
	{
		int k;
		int[] count = new int[b.length];
		for(int c=0;c<count.length;c++)
			count[c] = 0;
		try {
		File file = new File("info.txt");
		if(!file.exists())
		{
			file.createNewFile();
		}
		PrintWriter pw = new PrintWriter(file);
		for(int i=0;i<b[0].length;i++)
		{
			System.out.println("");
			pw.println("Platform "+(i+1));
			for(int j=0;j<b.length;j++)
			{
				if(b[j][i]==1)
				{
					ArrayList<String> arrClients = servClient.get(j+1);
					pw.println("\tSeriviceType "+(j+1));
					pw.println("\t\tCPU 10.00");
					pw.println("\t\tMemory 10.00");
					pw.println("\t\tDisk 10.00");
					pw.println("\t\tEstimatedTotalAccessRate 0.2500");
					String[] clientIndex = arrClients.get(count[j]++).split(" ");
					pw.println("\t\tClientIndex "+(clientIndex[1]));
				}
			}
		}
		pw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	public static void main(String[] args)
	{		
		//int b[][] = new int[50][50];
		int n,m;
		n=m=0;
	//	Main main = new Main();
//		boolean[] isAllocated = main.getInitialAllocation();
		/*turnaroundTime = new double[50][50];
		expense = new double[50][50];
		Subtasks = new ArrayList<Subtask>();
		Resources = new ArrayList<Resource>();
		AllocationK = new HashMap<Subtask,ArrayList<Resource>>();
		AllocationR = new HashMap<Resource,ArrayList<Subtask>>();
		AllocationSubtasks = new ArrayList<Subtask>();
		AllocationResources = new ArrayList<Resource>();*/
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		/*System.out.println("Enter number of tasks and resources.");
		try {
			n = System.in.read();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			m = System.in.read();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		/*n = 3;
		m = 5;
		int b[][] = {{0,0,0,1,1},{0,0,1,1,1},{1,1,1,0,1}};*/
		
		Main main = new Main();	
		boolean[] boolArr = main.getInitialAllocation(args[0],args[1],args[2]);
		String taskStr = FileUtils.getTaskStr();
		String[] services = taskStr.split(";");	
		ArrayList<String> clients;
		
		for(int s=1;s<=services.length;s++)
		{
			clients = new ArrayList<String>();
			String[] values = services[s-1].split(":");
			String[] clientValues = values[1].split(",");
			for(int cv=0;cv<clientValues.length;cv++)
			 clients.add(clientValues[cv]);
			servClient.put(s,clients);
		}
		n= services.length;
		m = boolArr.length/n;		
		System.out.println(n);
		System.out.println(m);
		int[][] b= new int[n][m];
		for(int i=0;i<n;i++)
		{
			Subtask t = new Subtask();
			t.setID(i+1);
			Subtasks.add(t);
		}
		for(int j=0;j<m;j++)
		{
			Resource r = new  Resource();
			r.setID(j+1);
			r.rt = ResourceType.CPU;
			Resources.add(r);
		}		
		int x,y;
		x=y=0;
		for(int c=0;c<Subtasks.size();c++)
		{
			for(int d=0;d<Resources.size();d++)
			{
				b[c][d]=0;
			}
		}
		for(int j=0;j<boolArr.length;j++)
		{
			if(boolArr[j])
				b[y][x] = 1;
			else
				b[y][x]= 0;
			
			if(y<Subtasks.size())
			{
				if(x<Resources.size()-1)
				{
					x++;					
				}
				else
				{
					y++;
					x=0;
				}				
			}
			else
			{
				y++;
				x=0;
			}
		}
		/*for(int i=0;i<n;i++)
		{
			System.out.println("Allocate Resources for subtask "+(i+1));
			AllocationResources.clear();
			for(int j=0;j<m;j++)
			{
				System.out.println("Allocation for Resource: "+(j+1));
				int in = 0;
				try {
					in = Integer.parseInt(br.readLine());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				b[i][j]=in;
				if(in==1)
					AllocationResources.add(Resources.get(j));
			}
			AllocationK.put(Subtasks.get(i), AllocationResources);				
		}*/
		
		for(int i=0;i<n;i++)
		{
			AllocationResources = new ArrayList<Resource>();
			AllocationResourcesArray.add(AllocationResources);
			for(int j=0;j<m;j++)
			{
				if(b[i][j]==1)
					AllocationResourcesArray.get(i).add(Resources.get(j));
			}
			AllocationK.put(Subtasks.get(i).getID(), AllocationResourcesArray.get(i));
		}
		
		for(int j=0;j<m;j++)
		{
			AllocationSubtasks = new ArrayList<Subtask>();
			AllocationSubtasksArray.add(AllocationSubtasks);
			for(int i=0;i<n;i++)
			{
				if(b[i][j]==1)
					AllocationSubtasksArray.get(j).add(Subtasks.get(i));
			}	
			AllocationR.put(Resources.get(j).getID(), AllocationSubtasksArray.get(j));
		}
		
	
	/*	for(int i=0;i<n;i++)
		{
			//System.out.println("Turnaround times for subtask "+(i+1));
			for(int j=0;j<m;j++)
			{
				System.out.println("Turnaround time for subtask "+(i+1) +" and Resource: "+(j+1));
				double in = 0;
				try {
					in = Double.parseDouble(br.readLine());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			turnaroundTime[i][j]=in;
			}		
		}
		for(int i=0;i<n;i++)
		{
			//System.out.println("Turnaround times for subtask "+(i+1));
			for(int j=0;j<m;j++)
			{
				System.out.println("Expense for subtask "+(i+1) +" and Resource: "+(j+1));
				double in = 0;
				try {
					in = Double.parseDouble(br.readLine());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			expense[i][j]=in;
			}				
		}
		try {
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
*/		
		EvolutionaryOptimize(b);
	}
}

