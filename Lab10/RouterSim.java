import java.util.*;
import java.io.*;
import java.net.*;

class RouterSim
{
    public static void main(String argsX[]) throws Exception
    {
    	String[] args = new String[6];

    	for(int i=0;i<argsX.length;i++)
    	{
	         if(argsX[i].equals("-i"))
	            args[0]=argsX[i+1];
	         else if(argsX[i].equals("-f"))
	            args[1]=argsX[i+1];
	         else if(argsX[i].equals("-o"))
	            args[2]=argsX[i+1];
	         else if(argsX[i].equals("-h"))
	            args[3]=argsX[i+1];
	         else if(argsX[i].equals("-a"))
	            args[4]=argsX[i+1];
	         else if(argsX[i].equals("-s"))
	            args[5]=argsX[i+1];
      	}

      	int NODE_IDENTIFIER = Integer.parseInt(args[0]);
      	String INPUT_FILE = args[1];
      	String OUTPUT_FILE = args[2];
      	float HELLO_INTERVAL = Float.parseFloat(args[3]);
      	float LSA_INTERVAL = Float.parseFloat(args[4]);
      	float SPF_INTERVAL = Float.parseFloat(args[5]);
      	int NO_OF_NODES = 1;
      	int NO_OF_EDGES = 0;
      	ArrayList<NodeInfo> nodeInfos = new ArrayList<NodeInfo>();
      	ArrayList<ArrayList<Integer>> AdjMat = new ArrayList<ArrayList<Integer>>();

      	BufferedReader br = null;
      	String line = "";
    	String cvsSplitBy = ",";
      	try {

            br = new BufferedReader(new FileReader(INPUT_FILE));
            line = br.readLine();
            String[] xyz = line.split(cvsSplitBy);
            NO_OF_NODES = Integer.parseInt(xyz[0]);
            NO_OF_EDGES = Integer.parseInt(xyz[1]);
            System.out.println(NO_OF_NODES);
            System.out.println(NO_OF_EDGES);
            for(int i=0;i<NO_OF_NODES;i++)
            {
            	ArrayList<Integer> temp = new ArrayList<Integer>();
            	for(int j=0;j<NO_OF_NODES;j++)
            	{
            		temp.add(-1);
            	}
            	AdjMat.add(temp);
            }
            while ((line = br.readLine()) != null) {
                String[] country = line.split(cvsSplitBy);
                AdjMat.get(Integer.parseInt(country[0])).set(Integer.parseInt(country[1]),10000);
                AdjMat.get(Integer.parseInt(country[1])).set(Integer.parseInt(country[0]),10000);
                if(Integer.parseInt(country[0])==NODE_IDENTIFIER)
                {
		            NodeInfo ni = new NodeInfo(Integer.parseInt(country[0]),Integer.parseInt(country[1]),Integer.parseInt(country[2]),Integer.parseInt(country[3]));
					nodeInfos.add(ni);
				}
				else if(Integer.parseInt(country[1])==NODE_IDENTIFIER)
				{
					NodeInfo ni = new NodeInfo(Integer.parseInt(country[1]),Integer.parseInt(country[0]),Integer.parseInt(country[2]),Integer.parseInt(country[3]));
					nodeInfos.add(ni);
				}
            }
            if (br != null) {
                br.close();
            }
            for(int i=0;i<nodeInfos.size();i++)
            {
            	System.out.println(nodeInfos.get(i));
            }
            DatagramSocket Socket = new DatagramSocket(10000+NODE_IDENTIFIER);
			byte[] receiveData = new byte[1024];
			byte[] sendData = new byte[1024];
			HelloSend hs = new HelloSend(Socket,nodeInfos,NODE_IDENTIFIER,HELLO_INTERVAL);
			Listen hrl = new Listen(Socket,nodeInfos,NODE_IDENTIFIER,NO_OF_NODES,AdjMat);
			LSASend ls = new LSASend(Socket,nodeInfos,NODE_IDENTIFIER,LSA_INTERVAL);
			DetermineTopo dt = new DetermineTopo(OUTPUT_FILE,NODE_IDENTIFIER, AdjMat,SPF_INTERVAL);
			hs.start();
			hrl.start();
			ls.start();
			dt.start();
        }catch (Exception e) {
            e.printStackTrace();
        }

	}
}

class NodeInfo
{
	public int i;
	public int j;
	public int minC;
	public int maxC;
	public int cost;

	public NodeInfo(int i1,int i2,int i3,int i4)
	{
		this.i = i1;
		this.j = i2;
		this.minC = i3;
		this.maxC = i4;
		this.cost = 10000;
	}

	public String toString()
	{
		return this.i+" "+this.j+" "+this.minC+" "+this.maxC+" "+this.cost;
	}

}

class HelloSend extends Thread
{
	public ArrayList<NodeInfo> nodeInfos;
	public int NODE_IDENTIFIER;
	public float HELLO_INTERVAL;
	DatagramSocket Socket;

	public HelloSend(DatagramSocket Socket, ArrayList<NodeInfo> nodeInfos,int NODE_IDENTIFIER,float HELLO_INTERVAL)
	{
		this.nodeInfos = nodeInfos;
		this.NODE_IDENTIFIER = NODE_IDENTIFIER;
		this.HELLO_INTERVAL = HELLO_INTERVAL;
		this.Socket = Socket;
	}

	public void run()
	{
		try{
			while(true)
			{
				for(int i=0;i<nodeInfos.size();i++)
				{	
					InetAddress IPAddress = InetAddress.getByName("localhost");
					String outputStream = "HELLO"+NODE_IDENTIFIER;
					byte[] data = outputStream.getBytes();
					DatagramPacket sendPacket = new DatagramPacket(data, data.length, IPAddress,10000+nodeInfos.get(i).j);
					Socket.send(sendPacket);
					System.out.println("SEnt HELLO "+outputStream);
				}
				Thread.sleep(1000*Math.round(HELLO_INTERVAL));
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
} 

class Listen extends Thread
{
	public int NODE_IDENTIFIER;
	public ArrayList<NodeInfo> nodeInfos;
	public int NO_OF_NODES;
	public ArrayList<Integer> last_seq;
	ArrayList<ArrayList<Integer>> AdjMat;
	DatagramSocket Socket;

	public Listen(DatagramSocket Socket, ArrayList<NodeInfo> nodeInfos,int NODE_IDENTIFIER,int NO_OF_NODES,ArrayList<ArrayList<Integer>> AdjMat)
	{
		this.NODE_IDENTIFIER = NODE_IDENTIFIER;
		this.nodeInfos = nodeInfos;
		this.NO_OF_NODES = NO_OF_NODES;
		this.AdjMat = AdjMat;
		this.Socket = Socket;
		last_seq = new ArrayList<Integer>();
	}
	public byte[] receiveData = new byte[1024];

	public void run()
	{
		try{
			for(int k =0;k<NO_OF_NODES;k++)
			{
				last_seq.add(-1);
			}
			while(true)
			{
				receiveData = new byte[1024];
				DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
		        Socket.receive(receivePacket);
		        InetAddress ip = receivePacket.getAddress();
		        int port = receivePacket.getPort();
		        String s = new String(receiveData);
		        if(s.substring(0,10).equals("HELLOREPLY"))
		        {
					System.out.println("REcv HELLOREPLY "+s);
		        	String[] splited = s.split("\\s+");
		        	int src = Integer.parseInt(splited[1]);
		        	for(int i=0;i<nodeInfos.size();i++)
		        	{
		        		if(nodeInfos.get(i).j==src)
		        		{
		        			nodeInfos.get(i).cost=Integer.parseInt(splited[3].trim());
		        			AdjMat.get(NODE_IDENTIFIER).set((nodeInfos.get(i).j),nodeInfos.get(i).cost);
		        			
		        			break;
		        		}
		        	}
		        }
		        else if(s.substring(0,5).equals("HELLO"))
		        {
					System.out.println("REcv HELLO "+s);
		        	int src = Integer.parseInt(s.substring(5).trim());
		        	for(int i=0;i<nodeInfos.size();i++)
		        	{
		        		if(nodeInfos.get(i).j==src)
		        		{
		        			Random r = new Random();
							int Low = nodeInfos.get(i).minC;
							int High = nodeInfos.get(i).maxC;
							int Result = r.nextInt(High-Low) + Low;
							InetAddress IPAddress = InetAddress.getByName("localhost");
							String outputStream = "HELLOREPLY "+NODE_IDENTIFIER+" "+src+" "+Result;
							byte[] data = outputStream.getBytes();
							DatagramPacket sendPacket = new DatagramPacket(data, data.length, IPAddress,10000+src);
							Socket.send(sendPacket);
							System.out.println("SENT HELLO*");
		        			break;
		        		}
		        	}
		        }
		        else if(s.substring(0,3).equals("LSA"))
		        {
					System.out.println("REcv LSA "+s);
		        	String[] splited = s.split("\\s+");
		        	int mainsrc = Integer.parseInt(splited[1]);
		        	int dummysrc = port-10000;
		        	int entries = Integer.parseInt(splited[3]);
		        	System.out.println(last_seq);
		        	if(Integer.parseInt(splited[2])>last_seq.get(mainsrc))
		        	{
		        		for(int i=0;i<entries;i++)
		        		{
		        			int maindest = Integer.parseInt(splited[4+i*2]);
		        			int maincost = Integer.parseInt(splited[5+i*2].trim());

		        			System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
		        			System.out.println(AdjMat);
		        			AdjMat.get(mainsrc).set((maindest),maincost);
		        			System.out.println(AdjMat);
		        			System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
		        		}
			        	for(int i=0;i<NO_OF_NODES;i++)
			        	{
			        		if(i!=NODE_IDENTIFIER && i!=dummysrc  && i!=dummysrc)
			        		{
			        			last_seq.set(mainsrc,Integer.parseInt(splited[2]));
								InetAddress IPAddress = InetAddress.getByName("localhost");
								String outputStream = s;
								byte[] data = outputStream.getBytes();
								DatagramPacket sendPacket = new DatagramPacket(data, data.length, IPAddress,10000+i);
								Socket.send(sendPacket);
								System.out.println("FWD LSA "+dummysrc);
							}
			        	}
			        }
		        }
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}

class LSASend extends Thread
{
	public ArrayList<NodeInfo> nodeInfos;
	public int NODE_IDENTIFIER;
	public float LSA_INTERVAL;
	public int lsa_count;
	DatagramSocket Socket;

	public LSASend(DatagramSocket Socket, ArrayList<NodeInfo> nodeInfos,int NODE_IDENTIFIER,float LSA_INTERVAL)
	{
		this.nodeInfos = nodeInfos;
		this.NODE_IDENTIFIER = NODE_IDENTIFIER;
		this.LSA_INTERVAL = LSA_INTERVAL;
		this.lsa_count = 0;
		this.Socket = Socket;
	}

	public void run()
	{
		try{
			while(true)
			{
				for(int i=0;i<nodeInfos.size();i++)
				{	
					InetAddress IPAddress = InetAddress.getByName("localhost");
					String outputStream = "LSA "+NODE_IDENTIFIER+" "+lsa_count+" "+nodeInfos.size();
					for(int j=0;j<nodeInfos.size();j++)
					{
						outputStream = outputStream+" "+nodeInfos.get(j).j+" "+nodeInfos.get(j).cost;
					}
					byte[] data = outputStream.getBytes();
					DatagramPacket sendPacket = new DatagramPacket(data, data.length, IPAddress,10000+nodeInfos.get(i).j);
					Socket.send(sendPacket);
					System.out.println("Sent LSa "+outputStream+" to "+nodeInfos.get(i).j);
					lsa_count++;
				}
				Thread.sleep(1000*Math.round(LSA_INTERVAL));
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}

class DetermineTopo extends Thread
{
	ArrayList<ArrayList<Integer>> AdjMat;
	float SPF_INTERVAL;
	public int NO_OF_NODES;
	public int NODE_IDENTIFIER;
	public String OUTPUT_FILE;

	public DetermineTopo( String OUTPUT_FILE, int NODE_IDENTIFIER, ArrayList<ArrayList<Integer>> AdjMat,float SPF_INTERVAL)
	{
		this.SPF_INTERVAL = SPF_INTERVAL;
		this.AdjMat = AdjMat;
		this.NO_OF_NODES = AdjMat.size();
		this.NODE_IDENTIFIER = NODE_IDENTIFIER;
		this.OUTPUT_FILE = OUTPUT_FILE;
	}

	public int minD(int dist[], boolean prev[])
	{
		int min = 10000;
		int idx=-1;
 
        for (int v = 0; v < NO_OF_NODES; v++)
        {
            if (prev[v] == false && dist[v] <= min)
            {
                min = dist[v];
                idx = v;
            }
        }
 
        return idx;
	}

	public void run()
	{
		try{
			String FILENAME = OUTPUT_FILE+"-"+NODE_IDENTIFIER+".txt";
			while(true){
					System.out.println("DEt Topo");
				for(int i=0;i<AdjMat.size();i++)
				{
					for(int j=0;j<AdjMat.get(i).size();j++)
					{
						System.out.print(AdjMat.get(i).get(j)+" ");
					}
					System.out.print("\n");
				}
				System.out.println("------------------------------------------------------------");

				int dist[] = new int[NO_OF_NODES];
				boolean prev[] = new boolean[NO_OF_NODES];
				int prex[] = new int[NO_OF_NODES];
				int[] parent = new int[NO_OF_NODES];
				for (int k = 0; k < NO_OF_NODES; k++)
		        {
		            dist[k] = 10000;
		            prev[k] = false;
		            prex[k] = -1;
		            parent[k]=-1;
		        }
		        dist[NODE_IDENTIFIER] = 0;
		        for (int k= 0; k < NO_OF_NODES-1; k++)
		        {
		        	int u = minD(dist,prev);
		        	prev[u] = true;
		        	for (int v = 0; v < NO_OF_NODES; v++)
		        	{
		        		if (!prev[v] && AdjMat.get(u).get(v)!=-1 && dist[u] != 10000 && dist[u]+AdjMat.get(u).get(v)<dist[v])
		        		{
		        			parent[v]=u;
                    		dist[v]=dist[u]+AdjMat.get(u).get(v);
                    		prex[v]=u;
                    	}
		        	}
		        }


		        BufferedWriter bw = null;
				FileWriter fw = null;

				fw = new FileWriter(FILENAME);
				bw = new BufferedWriter(fw);

				bw.write(" Routing Table for Node No. "+NODE_IDENTIFIER+" at "+new Date()+"\n");
				for (int i = 0; i < NO_OF_NODES; i++)
				{
					ArrayList<Integer> xall = new ArrayList<Integer>();
			    	int a = i;
			    	xall.add(a);
			    	while(parent[a]!=-1)
			    	{
			    		xall.add(parent[a]);
			    		a = parent[a];
			    	}

			    	String sp = ""+NODE_IDENTIFIER;
			    	for(int jc=xall.size()-2;jc>=0;jc--)
			    		sp=sp+"->"+xall.get(jc);
			    	for(int jc=0;jc<100-sp.length();jc++)
			    		sp=sp+" ";
			        bw.write(i+"      "+sp+dist[i]+"\n");
				}
				bw.write("-----------------------------------------------------------\n");
				
				// for(int i=0;i<NO_OF_NODES;i++)
				// 	bw.write(parent[i]+" ");
				// bw.write("\n");	

		        System.out.println("Routing Table at "+new Date());
			    for (int i = 0; i < NO_OF_NODES; i++){
			        System.out.println(i+" \t "+dist[i]);
			    }

			    bw.close();
			    fw.close();

				Thread.sleep(1000*Math.round(SPF_INTERVAL));
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}

}

