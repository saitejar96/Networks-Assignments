import java.io.*;
import java.net.*;
import java.util.*;

class Receiver
{
   public static void main(String argsX[]) throws Exception
      {     
            String[] args = new String[20];
            args[4]="false";
            //System.out.println(argsX);
            for(int i=0;i<argsX.length;i++){
               if(argsX[i].equals("-p"))
                  args[0]=argsX[i+1];
               else if(argsX[i].equals("-e"))
                  args[1]=argsX[i+1];
               else if(argsX[i].equals("-w"))
                  args[2]=argsX[i+1];
               else if(argsX[i].equals("-n"))
                  args[3]=argsX[i+1];
               else if(argsX[i].equals("-d"))
                  args[4]=argsX[i+1];
            }
            DatagramSocket serverSocket = new DatagramSocket(Integer.parseInt(args[0]));
            byte[] receiveData = new byte[1024*32];
            byte[] sendData = new byte[1024*32];
            String noack = "NACK";
            String ack = "ACK";
            float RANDOM_DROP_PROB = Float.parseFloat(args[1]);
            int WINDOW_SIZE = Integer.parseInt(args[2]);
            int MAX_PACKETS = Integer.parseInt(args[3]);
            String pktd = "false";
            boolean debug = false;
            if(args[4].equals("true"))
               debug = true;
            ArrayList<String> pkt = new ArrayList<String>();
            int cunt = 0;
            int next_exp=0;
            int portX = 0;
            InetAddress IPAddress=null;
            while(true && cunt!=MAX_PACKETS)
               {
                  pkt = new ArrayList<String>();
                  receiveData = new byte[1024*32];
                  sendData = new byte[1024*32];
                  DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                  serverSocket.receive(receivePacket);
                  long tm = System.nanoTime();
                  float tm1 = 1000000;
                  float tm2 = tm/tm1; 
                  IPAddress = receivePacket.getAddress();
                  int port = receivePacket.getPort();
                  portX =port;
                  double random = Math.random();
                  ByteArrayInputStream bais = new ByteArrayInputStream(receiveData);
                  DataInputStream in = new DataInputStream(bais);
                  int cnt =0;
                  while (in.available() > 0 && cnt!=8) {
                      String element = in.readUTF();
                      pkt.add(element);
                      cnt+=1;
                  }
                  int rec_seq = 0;
                  for(int i=7;i>=0;i--){
                     double num = Double.parseDouble(pkt.get(i))*Math.pow(2,7-i);
                     rec_seq+=num;
                  }
                  // System.out.println(rec_seq+"");
                  // System.out.println(next_exp+"");
                  if(random<RANDOM_DROP_PROB){
                     //rec_seq=WINDOW_SIZE+1;
                     //System.out.println("DROppeDDD");
                     pktd = "true";
                  }
                  else{
                     pktd = "false";
                     if(rec_seq==next_exp){
                        next_exp=(next_exp+1)%WINDOW_SIZE;
                     }
                     else{
                        rec_seq=next_exp;
                     }
                     ack=ack+" "+next_exp;
                     sendData = ack.getBytes();
                     DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
                     serverSocket.send(sendPacket);
                     cunt++;
                     //System.out.println("RECEIVED "+ack);
                     //String capitalizedSentence = sentence.toUpperCase();
                     //sendData = capitalizedSentence.getBytes();
                     ack="ACK";
                  }
                  if(debug){
                     System.out.println("Seq No:"+rec_seq+" Time Received:"+tm2+" Packet dropped:"+pktd);
                  }
               }
               int non =-1;
               ack=ack+" "+non;
                     sendData = ack.getBytes();
                     DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, portX);
                     serverSocket.send(sendPacket);

      }
}