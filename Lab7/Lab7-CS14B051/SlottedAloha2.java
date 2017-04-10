import java.util.*;

class SlottedAloha2
{
    public static void main(String argsX[]) throws Exception{
   		String[] args = new String[4];

   		//Parsing cmd line args
        
      for(int i=0;i<argsX.length;i++){
         if(argsX[i].equals("-N"))
            args[0]=argsX[i+1];
         else if(argsX[i].equals("-W"))
            args[1]=argsX[i+1];
         else if(argsX[i].equals("-p"))
            args[2]=argsX[i+1];
         else if(argsX[i].equals("-M"))
            args[3]=argsX[i+1];
      }
      int NO_OF_NODES = Integer.parseInt(args[0]);
      int INITIAL_COLLISION_WINDOW = Integer.parseInt(args[1]);
      float PKT_GEN_PROB = Float.parseFloat(args[2]);
      int MAX_PACKETS = Integer.parseInt(args[3]);
      
      ArrayList<Node> nodes = new ArrayList<Node>();
      ArrayList<Slot> slots = new ArrayList<Slot>();
      long SimTime = 0;
      int successfull = 0;
      int num_packets = 0;

      for(int i=0;i<NO_OF_NODES;i++){
          Node node = new Node();
          node.COLLISION_WINDOW = INITIAL_COLLISION_WINDOW;
          node.pkts = new ArrayList<Packet>();
          node.backoff_counter = 0;
          node.backlogged = false;
          nodes.add(node);
      }

      long total_time = 0;

      while(true){

        boolean exit = false;
          for(int i=0;i<NO_OF_NODES;i++){
           for(int j=0;j<nodes.get(i).pkts.size();j++){
             if(nodes.get(i).pkts.get(j).retrans>10){
               //System.out.println(i+" "+j+" "+nodes.get(i).pkts.get(j).retrans);
               exit=true;
               break;
             }
           }
          }
          if(exit || successfull>=MAX_PACKETS){
            break;
          }


          boolean flag = false;
          Slot slot = new Slot();
          slot.ATTEMPTED_TRANSMITTERS = new ArrayList<Integer>();
          slot.SUCCESSFULL_TXN = NO_OF_NODES;
          slot.success = false;
          num_packets++;
          for(int i=0;i<NO_OF_NODES;i++){
            flag = false;
            double random = Math.random();
            if(nodes.get(i).pkts.size()<2){
              if(random<PKT_GEN_PROB){
                Packet pkt = new Packet();
                pkt.gen_time = SimTime;
                pkt.retrans = 0;
                nodes.get(i).pkts.add(pkt);
                if(nodes.get(i).pkts.size()==1)
                  flag = true;
              }
            }
            if(flag || (nodes.get(i).backoff_counter==0 && nodes.get(i).backlogged)){
              slot.ATTEMPTED_TRANSMITTERS.add(i);
            }
          }
          //System.out.println(nodes);
          //System.out.println(slot.ATTEMPTED_TRANSMITTERS);
          for(int j=0;j<slot.ATTEMPTED_TRANSMITTERS.size();j++){
            if(nodes.get(slot.ATTEMPTED_TRANSMITTERS.get(j)).pkts.size()>0)
              nodes.get(slot.ATTEMPTED_TRANSMITTERS.get(j)).pkts.get(0).retrans++;
          }
        
          if(slot.ATTEMPTED_TRANSMITTERS.size()==1 && nodes.get(slot.ATTEMPTED_TRANSMITTERS.get(0)).pkts.size()>0){
            total_time = total_time+(SimTime-nodes.get(slot.ATTEMPTED_TRANSMITTERS.get(0)).pkts.get(0).gen_time);
            successfull++;
            slot.success = true;
            slot.SUCCESSFULL_TXN = slot.ATTEMPTED_TRANSMITTERS.get(0);
            nodes.get(slot.SUCCESSFULL_TXN).pkts.remove(0);
            nodes.get(slot.SUCCESSFULL_TXN).COLLISION_WINDOW = (int)Math.max(2,0.75*nodes.get(slot.SUCCESSFULL_TXN).backoff_counter);
            if(nodes.get(slot.SUCCESSFULL_TXN).pkts.size()==0)
              nodes.get(slot.SUCCESSFULL_TXN).backlogged=false;
            else
              nodes.get(slot.SUCCESSFULL_TXN).backlogged=true;
          }
          else{
            slot.success = false;
            for(int j=0;j<slot.ATTEMPTED_TRANSMITTERS.size();j++){
              nodes.get(slot.ATTEMPTED_TRANSMITTERS.get(j)).backlogged = true;
              Random ran = new Random();
              //System.out.println(""+nodes.get(slot.ATTEMPTED_TRANSMITTERS.get(j)).COLLISION_WINDOW);
              int abc = (int)nodes.get(slot.ATTEMPTED_TRANSMITTERS.get(j)).COLLISION_WINDOW;
              int x = ran.nextInt(abc+1);
              //System.out.println(x+"*");
              nodes.get(slot.ATTEMPTED_TRANSMITTERS.get(j)).backoff_counter = x;
              nodes.get(slot.ATTEMPTED_TRANSMITTERS.get(j)).COLLISION_WINDOW = Math.min(256,2*nodes.get(slot.ATTEMPTED_TRANSMITTERS.get(j)).COLLISION_WINDOW);
            }
          }
          for(int i=0;i<NO_OF_NODES;i++){
            if(nodes.get(i).backlogged && nodes.get(i).backoff_counter>0)
              nodes.get(i).backoff_counter--;
          }
          slots.add(slot);
          SimTime++; 
          //break;
        }

            // System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%"+successfull);
            // System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%"+SimTime);
            // System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%"+total_time);
        float ans = (float)successfull/(float)SimTime;
        long ans2 = total_time/successfull;
        System.out.println("No. of nodes: "+NO_OF_NODES);
        System.out.println("W: "+INITIAL_COLLISION_WINDOW);
        System.out.println("Probability of Generating: "+PKT_GEN_PROB);
        System.out.println("Utilization: "+ans);
        System.out.println("Average Packet Delay: "+ans2);
      }
}

class Packet{
  public  int retrans;
  public long gen_time;
  public String toString(){
    return "pkt";
  }
}

class Node{
  public  int COLLISION_WINDOW;
  public  ArrayList<Packet> pkts;
  public  int backoff_counter;
  public  boolean backlogged;

  public String toString(){
    return backoff_counter+"  "+pkts+"****";
  }
}

class Slot{
  public  ArrayList<Integer> ATTEMPTED_TRANSMITTERS;
  public  int SUCCESSFULL_TXN;
  public  boolean success;
}