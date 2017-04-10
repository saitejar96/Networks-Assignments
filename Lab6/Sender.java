import java.io.*;
import java.net.*;
import java.util.*;
import java.sql.Timestamp;

class Sender
{
   public static void main(String argsX[]) throws Exception
   {
      BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
      String[] args = new String[20];
      args[7]="false";
      for(int i=0;i<argsX.length;i++){
         if(argsX[i].equals("-s"))
            args[0]=argsX[i+1];
         else if(argsX[i].equals("-p"))
            args[1]=argsX[i+1];
         else if(argsX[i].equals("-l"))
            args[2]=argsX[i+1];
         else if(argsX[i].equals("-r"))
            args[3]=argsX[i+1];
         else if(argsX[i].equals("-b"))
            args[4]=argsX[i+1];
         else if(argsX[i].equals("-w"))
            args[5]=argsX[i+1];
         else if(argsX[i].equals("-n"))
            args[6]=argsX[i+1];
         else if(argsX[i].equals("-d"))
            args[7]=argsX[i+1];
      }
      DatagramSocket clientSocket = new DatagramSocket();
      InetAddress IPAddress = InetAddress.getByName(args[0]);
      byte[] sendData = new byte[1024*32];
      byte[] receiveData = new byte[1024*32];
      float RFF = 0;
      int counter = 0;
      Timestamp timestamp = new Timestamp(System.currentTimeMillis());
      ArrayList<Long> sent = new ArrayList<Long>();
      ArrayList<Long> recv = new ArrayList<Long>();
      //Change input giving in cmd-line
      int PACKET_LENGTH = Integer.parseInt(args[2]);
      int PACKET_GEN_RATE = Integer.parseInt(args[3]);
      int MAX_BUFFER_SIZE = Integer.parseInt(args[4]);
      int WINDOW_SIZE = Integer.parseInt(args[5]);
      int MAX_PACKETS = Integer.parseInt(args[6]);
      boolean debug = false;
      if(args[7].equals("true"))
         debug = true;
      //Setting timeouts in milliseconds
      float timeout = 100;

      //Thread to keep generating pkts
      BufFill tasknew = new BufFill(MAX_BUFFER_SIZE,PACKET_GEN_RATE,PACKET_LENGTH,WINDOW_SIZE);
      Timer timer = new Timer();
      timer.scheduleAtFixedRate(tasknew,500,1);
      AckListen al = new AckListen(clientSocket,tasknew.BUFFER,WINDOW_SIZE,args,sent,recv,PACKET_GEN_RATE,PACKET_LENGTH,MAX_PACKETS,debug);  
      al.start();  
      Thread.sleep(5000);
      for(int t=0;t<WINDOW_SIZE;t++){
         if(tasknew.BUFFER.size()>WINDOW_SIZE ){
            //System.out.println(tasknew.BUFFER.size());
            sendData = new byte[1024*32];
            receiveData = new byte[1024*32];
            //sendData = tasknew.BUFFER.get(0).getBytes();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(baos);
            for (String element : tasknew.BUFFER.get(t)) {
                out.writeUTF(element);
            }
            sendData = baos.toByteArray();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, Integer.parseInt(args[1]));
            long s = System.nanoTime();
            clientSocket.send(sendPacket);
            sent.add(s);
            //System.out.println(sendData);
            timestamp = new Timestamp(System.currentTimeMillis());
            //al.sentTime = new Timestamp(timestamp);
            counter+=1;
            //tasknew.BUFFER.remove(0);
            // DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            // clientSocket.receive(receivePacket);
            // String modifiedSentence = new String(receivePacket.getData());
            // System.out.println("FROM SERVER:" + modifiedSentence);
         }
         else{
            Thread.sleep(5000);
         }
      }
      //clientSocket.close();
   }
}

class BufFill extends TimerTask{

   //Buffer to hold generated packets
   public static ArrayList<ArrayList<String>> BUFFER = new ArrayList<ArrayList<String>>();
   public static ArrayList<String> temp = new ArrayList<String>();
   public static int n = 0;
   public static int k = 0;
   public static int l = 0;
   public static int m = 0;
   public static int actual = 0;
   public static int seq_num = 0;

   public BufFill(int n,int k,int l,int m){
      this.n = n;
      this.k = k;
      this.l = l;
      this.m = m;
   }

   public static void iTob(int i){
      int o=7;
      temp.set(o,(i%2)+"");
      o=o-1;
      i=i/2;
      while(i!=0){
         temp.set(o,(i%2)+"");
         o=o-1;
         i=i/2;
      }
   }

   public void run(){  
      //System.out.println("thread is running..."+n+" "+k+" "+l);
      for(int i=0;i<k;i++){
         temp = new ArrayList<String>();
         for(int k=0;k<8;k++)
            temp.add("0");
         for(int j=0;j<(l-1)*8;j++){
            Random r = new Random();
            int Low = 0;
            int High = 2;
            int Result = r.nextInt(High-Low) + Low;
            temp.add(Result+"");
         }
         if(BUFFER.size()<n){
            actual = seq_num%m;
            iTob(actual);
            //temp.set(0,actual+"");
            seq_num+=1;
            BUFFER.add(temp);
         }
      }
      //System.out.println(BUFFER);
   }   
}

class AckListen extends Thread {
   public static DatagramSocket clientSocket1 ; 
   public static byte[] receiveData1 = new byte[1024*32];
   public static int recAck = 0;
   public static int expAck = 1;
   public static int tout = 100;
   public static int WINDOW_SIZE = 0;
   public static int PACKET_GEN_RATE = 0;
   public static int PACKET_LENGTH = 0;
   public static int MAX_PACKETS = 0;
   public static ArrayList<ArrayList<String>> BUFFER = new ArrayList<ArrayList<String>>();
   public static String[] args;
   public static byte[] sendData = new byte[1024*32];
   public static String element ="";
   public static ArrayList<Long> sent;
   public static ArrayList<Long> recv;
   public static int rtt_avg = 5000;
   public static long abc = 0;
   public int retrans = 0;
   public static boolean debug= false;

   public AckListen(DatagramSocket cs,ArrayList<ArrayList<String>> BUFFER1,int WINDOW_SIZE1,String[] ARGS,ArrayList<Long> sent,ArrayList<Long> recv,int PACKET_GEN_RATE,int PACKET_LENGTH,int MAX_PACKETS,boolean debug){
      this.clientSocket1 = cs;
      this.BUFFER = BUFFER1;
      this.WINDOW_SIZE =  WINDOW_SIZE1;
      this.args = ARGS;
      this.sent = sent;
      this.recv = recv;
      this.PACKET_GEN_RATE = PACKET_GEN_RATE;
      this.PACKET_LENGTH = PACKET_LENGTH;
      this.MAX_PACKETS= MAX_PACKETS;
      this.debug = debug;
   }

   public long rtt(){
      long rxt=0;
      try{
       // System.out.println("00000000000000000000");
       // System.out.println(sent.size());
       // System.out.println(recv.size());
      for(int i=0;i<recv.size();i++){
         if(recv.get(i)==0){
            rxt=rxt+abc;
         }
         else
            rxt=rxt+(recv.get(i)-sent.get(i));
      }
      rxt = rxt/(recv.size());
   } catch(Exception e){
   }
      return rxt;
   }

   public void run(){  
      try{
         //clientSocket1 = new DatagramSocket(Integer.parseInt(args[1]));
         clientSocket1.setSoTimeout(rtt_avg);
         InetAddress IPAddress = InetAddress.getByName(args[0]);
         while(true){
            //System.out.println("thread is running...");
            DatagramPacket receivePacket = new DatagramPacket(receiveData1, receiveData1.length);
            //System.out.println("receiving");
            clientSocket1.receive(receivePacket);
            long r = System.nanoTime();
            recv.add(r);
            //System.out.println("received "+receivePacket);
            //String modifiedSentence = new String(receivePacket.getBytes());
            ByteArrayInputStream bais = new ByteArrayInputStream(receiveData1);
            double random = Math.random();
            long att = Math.round(random*2)+2;
            if(Math.random()>0.15)
               att=1;
            DataInputStream in = new DataInputStream(bais);
            //int cnt =0;
            element = in.readUTF();
            //System.out.println(element);
            // pkt.add(element);
            // cnt+=1;
            //System.out.println(element+"(((((((((((((((((((((((((((((((((");
            recAck = Integer.parseInt(element.substring(2).trim());
            if(recAck==-1){
               System.out.println("PACKET_GEN_RATE: "+PACKET_GEN_RATE);
               System.out.println("PACKET_LENGTH: "+PACKET_LENGTH);
               float ratio = Float.parseFloat((retrans+MAX_PACKETS)+"")/Float.parseFloat(MAX_PACKETS+"");
               System.out.println("RETRANSMISSION RATIO: "+ratio);
               float ot = 1000000;
               float rtty = rtt()/ot;
               System.out.println("AVG. RTT: "+rtty+"ms");
               System.exit(0);
            }
            else{
               if(debug){
                  int altAck = recAck-1;
                  if(recAck==0)
                     altAck=WINDOW_SIZE-1;
                  float xty = 1000000;
                  float show = rtt()/xty;
                  System.out.println("Seq No:"+altAck+" Time Generated:"+System.nanoTime()+" RTT:"+show);
               }
            }
            //System.out.println("in ack: "+recAck);
            if(expAck != recAck){
                  //REQUIRES RETRANSMISSION
               retrans++;
               long ass =0;
               //recv.add(ass);
               for(int t=0;t<WINDOW_SIZE;t++){
                  if(BUFFER.size()>0 ){
                     //System.out.println(tasknew.BUFFER.size());
                     sendData = new byte[1024*32];
                     //sendData = tasknew.BUFFER.get(0).getBytes();
                     ByteArrayOutputStream baos = new ByteArrayOutputStream();
                     DataOutputStream out = new DataOutputStream(baos);
                     for (String element : BUFFER.get(t)) {
                         out.writeUTF(element);
                     }
                     sendData = baos.toByteArray();
                     DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, Integer.parseInt(args[1]));
                     long s = System.nanoTime();
                     sent.add(s);
                     clientSocket1.send(sendPacket);
                     //System.out.println(sendData);
                     //timestamp = new Timestamp(System.currentTimeMillis());
                     //al.sentTime = new Timestamp(timestamp);
                     //counter+=1;
                     //tasknew.BUFFER.remove(0);
                     // DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                     // clientSocket.receive(receivePacket);
                     // String modifiedSentence = new String(receivePacket.getData());
                     // System.out.println("FROM SERVER:" + modifiedSentence);
                  }
                  else{
                     Thread.sleep(5000);
                  }
               }
            }
            else{
               expAck=(expAck+1)%WINDOW_SIZE;
               BUFFER.remove(0);
               Thread.sleep(2);
               ByteArrayOutputStream baos = new ByteArrayOutputStream();
               DataOutputStream out = new DataOutputStream(baos);
               for (String element : BUFFER.get(WINDOW_SIZE-1)) {
                   out.writeUTF(element);
               }
               sendData = baos.toByteArray();
               DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, Integer.parseInt(args[1]));
               long s = System.nanoTime();
               sent.add(s);
               clientSocket1.send(sendPacket);
               //System.out.println(sendData);
            }
            //System.out.println("FROM SERVER:");
            abc=rtt();
            //System.out.println(rtt());
         }  
      }catch(Exception e){
         //e.printStackTrace();
         System.out.println("PACKET_GEN_RATE: "+PACKET_GEN_RATE);
         System.out.println("PACKET_LENGTH: "+PACKET_LENGTH);
               float ratio = Float.parseFloat((retrans+MAX_PACKETS)+"")/Float.parseFloat(MAX_PACKETS+"");
               System.out.println("RETRANSMISSION RATIO: "+ratio);    
         float ot = 1000000;
         float rtty = rtt()/ot;
         System.out.println("AVG. RTT: "+rtty+"ms");     
         System.exit(0);
      }
   }

}

// class AckListen extends Thread{ 
//    public static DatagramSocket clientSocket1 = new DatagramSocket(); 
//    public static byte[] receiveData1 = new byte[1024*32];
//    public static int recAck = 0;
//    public static int expAck = 0;
//    public int tout = 100;
//    public static Timestamp sentTime = new Timestamp(System.currentTimeMillis());
//    public static Timestamp recvTime = new Timestamp(System.currentTimeMillis());

//    public AckListen(DatagramSocket cs){
//       this.clientSocket1 = cs;
//    }

//    public void run(){  
//       while(true){
//          System.out.println("thread is running...");
//          DatagramPacket receivePacket = new DatagramPacket(receiveData1, receiveData1.length);
//          clientSocket1.setSoTimeout(tout);
//          try{
//             clientSocket1.receive(receivePacket);
//             recvTime = new Timestamp(System.currentTimeMillis());
//             String modifiedSentence = new String(receivePacket.getData());
//             recAck = Integer.parseInt(modifiedSentence.charAt(4)+"");
//             if(expAck != recAck){
//                //REQUIRES RETRANSMISSION
//             }
//             System.out.println("FROM SERVER:" + modifiedSentence);
//          }catch (SocketTimeoutException e) {
//                 // timeout exception.
//                 System.out.println("Timeout reached!!! " + e);
//                 continue;
//          }
//       }  
//    }
// }