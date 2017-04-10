import java.util.*;
import java.io.*;

class CongestionControl
{
	public static float minimum(float f1,float f2)
	{
		if(f1<f2)
			return f1;
		else
			return f2;
	}

	public static float maximum(float f1,float f2)
	{
		if(f1<f2)
			return f2;
		else
			return f1;
	}

    public static void main(String argsX[]) throws Exception{
    	try{
	    	String[] args = new String[7];

	    	for(int i=0;i<argsX.length;i++)
	    	{
		         if(argsX[i].equals("-i"))
		            args[0]=argsX[i+1];
		         else if(argsX[i].equals("-m"))
		            args[1]=argsX[i+1];
		         else if(argsX[i].equals("-n"))
		            args[2]=argsX[i+1];
		         else if(argsX[i].equals("-f"))
		            args[3]=argsX[i+1];
		         else if(argsX[i].equals("-s"))
		            args[4]=argsX[i+1];
		         else if(argsX[i].equals("-T"))
		            args[5]=argsX[i+1];
		         else if(argsX[i].equals("-o"))
		            args[6]=argsX[i+1];
	      	}



	      	float INIIAL_CW_MULTIPLIER = Float.parseFloat(args[0]);
	      	float CW_MULTIPLIER_EXP = Float.parseFloat(args[1]);
	      	float CW_MULTIPLIER_LIN = Float.parseFloat(args[2]);
	      	float TIMEOUT_MULTIPLIER = Float.parseFloat(args[3]);
	      	float PROB_OF_ACK = Float.parseFloat(args[4]);
	      	int TOTAL_SEGMENTS = Integer.parseInt(args[5]);
	      	String OUTFILE = args[6];

	      	FileWriter fw1 = new FileWriter(OUTFILE);
	  		BufferedWriter bw1 = new BufferedWriter(fw1);
	  		bw1.write("iter,CW\n");
	  		System.out.println("iter,CW");

	      	//in KiloBytes
	      	int MSS = 1;
	      	int RWS = 1024;

	      	float CW = INIIAL_CW_MULTIPLIER*MSS;
	      	float CONGESTION_THRESHOLD = RWS/2;

	      	int total = 0;

	      	while(total<TOTAL_SEGMENTS)
	      	{
	      		int N = Math.round(((float)CW/MSS)+1);
	      		for(int i=0;i<N;i++){
		      		total += 1;
		      		double rand = Math.random();
		      		if(total<CONGESTION_THRESHOLD && rand>PROB_OF_ACK){
		      			//System.out.println("EXP");
		      			CW = minimum(CW+CW_MULTIPLIER_EXP*MSS,RWS);
		      		}
		      		else if(total>CONGESTION_THRESHOLD && rand>PROB_OF_ACK){
		      			//System.out.println("lINEAR");
		      			CW = minimum(CW+CW_MULTIPLIER_LIN*MSS*MSS/CW,RWS);
		      		}
		      		else if(rand<PROB_OF_ACK){
		      			//System.out.println("Timeout");
		      			CONGESTION_THRESHOLD = CW/2;
		      			CW = maximum(1,TIMEOUT_MULTIPLIER*CW);      		
		      		}
		      		System.out.println(total+","+CW);
		      		//System.out.println("CT: "+CONGESTION_THRESHOLD);
		      		bw1.write(total+","+CW+"\n");
		      		//bw2.write(total+","+CONGESTION_THRESHOLD+"\n");
		      	}
	      	}
	      	bw1.close();
	      	fw1.close();
	    }catch(Exception e){
	    	e.printStackTrace();
	    }
    }
}
