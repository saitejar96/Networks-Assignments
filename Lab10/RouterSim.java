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
            while ((line = br.readLine()) != null) {
                String[] country = line.split(cvsSplitBy);
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
            DatagramSocket receiveSocket = new DatagramSocket(10000+NODE_IDENTIFIER);
			byte[] receiveData = new byte[1024];
			byte[] sendData = new byte[1024];
			HelloSend hs = new HelloSend(nodeInfos,NODE_IDENTIFIER,HELLO_INTERVAL);
			HelloReplyListen hrl = new HelloReplyListen();
			LSASend ls = new LSASend();
			DetermineTopo dt = new DetermineTopo();
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
		this.cost = 0;
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

	public HelloSend(ArrayList<NodeInfo> nodeInfos,int NODE_IDENTIFIER,float HELLO_INTERVAL)
	{
		this.nodeInfos = nodeInfos;
		this.NODE_IDENTIFIER = NODE_IDENTIFIER;
		this.HELLO_INTERVAL = HELLO_INTERVAL;
	}

	public void run()
	{
		while(true)
		{
			for(int i=0;i<nodeInfos.size();i++)
			{	
				Socket = new DatagramSocket();
				InetAddress IPAddress = InetAddress.getByName("localhost");
				
				byte[] data = outputStream.toByteArray();
				DatagramPacket sendPacket = new DatagramPacket(data, data.length, IPAddress,10000+nodeInfos.get(i).j);
				Socket.send(sendPacket);
			}
			Thread.sleep(1000*Math.round(HELLO_INTERVAL));
		}
	}
} 

class HelloReplyListen extends Thread
{

}

class LSASend extends Thread
{

}

class DetermineTopo extends Thread
{

}

