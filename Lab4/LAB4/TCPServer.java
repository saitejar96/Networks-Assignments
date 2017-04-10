import java.io.*;
import java.net.*;
import java.util.*;
import java.io.FileWriter;

class TCPServer
{
   public static String clientSentence="";
   public static String capitalizedSentence="";
   public static String answer="";
   public static ServerSocket welcomeSocket;
   public static ArrayList<String> users = new ArrayList<String>();
   public static int curr;
   public static int currmail;
   public static ArrayList<Spile> spoolFileList = new ArrayList<Spile>();
   public static BufferedReader inFromClient;
   public static boolean active = false;
   public static Socket connectionSocket;
   public static DataOutputStream outToClient;

   public static void main(String argv[]) throws Exception
      {
         welcomeSocket = new ServerSocket(Integer.parseInt(argv[0]));
         connectionSocket = welcomeSocket.accept();
         while(true)
         {
            inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
            outToClient = new DataOutputStream(connectionSocket.getOutputStream());
            clientSentence = inFromClient.readLine();
            if(clientSentence != null){
               System.out.println("Received: " + clientSentence);
               answer = commandProcessor(clientSentence);
               System.out.println("Processed to: "+answer);
               outToClient.writeBytes(answer+"\n");
            }
            else{
               connectionSocket = welcomeSocket.accept();
            }
         }
      }

   public static void updateFile(String u){
      try{
         FileWriter writer = new FileWriter(u);
         int n = users.indexOf(u);
         for(String str: spoolFileList.get(n).mailList) {
            if(!str.equals("INVALID"))
               writer.write(str);
         }
         writer.close();
      }
      catch(IOException ioe)
      {
         System.out.println("Error while creating a new empty file :" + ioe);
      }
   }

   public static String commandProcessor(String s)
   {
      if(s.equals("LSTU"))
      {
         String uList = "";
         if(users.size()==0)
            uList = "No Users Yet!";
         else{
            for(int i=0;i<users.size()-1;i++){
               uList = uList + users.get(i)+",";
            }
            uList = uList + users.get(users.size()-1);
         }
         return uList;
      }
      else if(s.length()>5 && (s.substring(0,4)).equals("ADDU")){
         String mssg = "";
         String uId = s.substring(5);
         if(users.contains(uId))
            mssg = "Userid already present";
         else{
            File file = new File(uId);
            boolean bcreate = false;
            try
            {
               bcreate = file.createNewFile();
               users.add(uId);
               Spile sf = new Spile();
               sf.uId = uId;
               sf.no_of_mails = 0;
               sf.mailList = new ArrayList<String>();
               sf.mailList.add("INVALID");
               spoolFileList.add(sf);
               mssg = "Userid successfully created";
            }
            catch(IOException ioe)
            {
               System.out.println("Error while creating a new empty file :" + ioe);
            }  
         }
         return mssg;
      }
      else if(s.length()>5 && (s.substring(0,4)).equals("USER")){
         String mssg = "";
         String uId = s.substring(5);
         if(users.contains(uId)){
            Spile sf;
            curr = users.indexOf(uId);
            sf = spoolFileList.get(curr);
            int k = sf.no_of_mails;
            currmail = sf.mailList.size()-1; 
            active = true;
            mssg = "User exists , has "+k+" mails and current user changed to "+sf.uId;
         }
         else{
            mssg = "Userid doesn't exist";
         }
         return mssg;
      }
      else if(s.equals("READM") && active){
         String mssg = "";
         if((spoolFileList.get(curr).mailList.get(currmail)).equals("INVALID")){
            mssg = "No more mail";
         }
         else{
            mssg = spoolFileList.get(curr).mailList.get(currmail);
            currmail = currmail -1;
            try{
               outToClient.writeBytes("READM\n");
            }
            catch(IOException ioe){
               System.out.println("Error while reading :" + ioe);
            }
         }
         return mssg;
      }
      else if(s.equals("DELM") && active){
         String mssg = "";
         if((spoolFileList.get(curr).mailList.get(currmail)).equals("INVALID")){
            mssg = "No more mail";
         }
         else{
            spoolFileList.get(curr).mailList.remove(currmail);
            spoolFileList.get(curr).no_of_mails += -1;
            updateFile(users.get(curr));
            mssg = "Deleted mail";
         }
         return mssg;
      }
      else if(s.length()>5 && (s.substring(0,4)).equals("SEND")){
         String mssg = "";
         String uId = s.substring(5);
         String subject = "";
         String message = "";
         String mail = "";
         String dummy = "";
         Date date = new Date();
         try{
            outToClient.writeBytes("SEND "+users.get(curr)+"\n");
            subject = inFromClient.readLine();
            dummy = inFromClient.readLine();
            message = message+dummy+"\n";
            while(!dummy.contains("###")){
               dummy = inFromClient.readLine();
               message = message+dummy+"\n";
            }

         }
         catch(IOException ioe)
         {
            System.out.println("Error while reading :" + ioe);
         }
         message = message +"\n";
         if(users.contains(uId)){
            mail = mail+"From: "+users.get(curr)+"\nTo: "+uId+"\nDate: "+date.toString()+"\nSubject: "+subject+"\nContents :\n"+message;
            spoolFileList.get(users.indexOf(uId)).mailList.add(mail);
            spoolFileList.get(users.indexOf(uId)).no_of_mails++;
            updateFile(uId);
            mssg = "Sent";
         }
         else{
            mssg = "User doesn't exist";
         }
         return mssg;
      }
      else if(s.equals("DONEU")){
         active = false;
         return "DONEU";
      }
      else if(s.equals("QUIT")){
         return "QUIT";
      }
      return "ERROR";
   }

}
class Spile{
   public String uId;
   public int no_of_mails;
   ArrayList<String> mailList;
}