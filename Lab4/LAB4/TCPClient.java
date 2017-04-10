import java.io.*;
import java.net.*;

class TCPClient
{
 public static void main(String argv[]) throws Exception
 {
  String sentence;
  String modifiedSentence;
  Socket clientSocket;
  boolean flag = false;
  String setU = "";
  String u1 = "";
  String host = argv[0];
  int port = Integer.parseInt(argv[1]);
  clientSocket = new Socket(host,port);
  while(true)
  {
    BufferedReader inFromUser = new BufferedReader( new InputStreamReader(System.in));
    DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
    BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    if(!flag)
      System.out.print("Main-Prompt>");
    else
      System.out.print("Sub-Prompt-"+setU+">");
    sentence = inFromUser.readLine();
    if(sentence.equals("Listusers")){
      sentence = "LSTU";
    }
    else if(sentence.length()>8 && (sentence.substring(0,7)).equals("Adduser")){
      u1 = sentence.substring(8);
      sentence = "ADDU "+u1;
    }
    else if(sentence.length()>8 && (sentence.substring(0,7)).equals("SetUser")){
      u1 = sentence.substring(8);
      sentence = "USER "+u1; 
    }
    else if(setU.length()>0 && sentence.equals("Read")){
      sentence = "READM";
    }
    else if(setU.length()>0 && sentence.equals("Delete")){
      sentence = "DELM";
    }
    else if(setU.length()>0 && sentence.equals("Done")){
      sentence = "DONEU";
    }
    else if(setU.length()>0 && sentence.length()>5 && (sentence.substring(0,4)).equals("Send")){
      u1 = sentence.substring(5);
      sentence = "SEND "+u1; 
    }
    else if(sentence.equals("Quit")){
      sentence = "QUIT";
    }
    else{
      sentence = "ERROR";
    }
    outToServer.writeBytes(sentence + '\n');
    modifiedSentence = inFromServer.readLine();
    System.out.println(modifiedSentence);
    if(modifiedSentence.equals("QUIT")){
      clientSocket.close();
      System.out.println("Connection closed");
      clientSocket = new Socket(host,port); 
      System.exit(0); 
    }
    else if(modifiedSentence.equals("READM")){
      modifiedSentence = inFromServer.readLine();
      System.out.println(modifiedSentence);
      while(!modifiedSentence.contains("###"))
      {
        modifiedSentence = inFromServer.readLine();
        System.out.println(modifiedSentence);
      }
      modifiedSentence = inFromServer.readLine();
      System.out.println(modifiedSentence);
    }
    else if(modifiedSentence.equals("DONEU")){
      flag = false;
      setU = "";
    }
    else if(modifiedSentence.length()>12 && (modifiedSentence.substring(0,11)).equals("User exists")){
      setU = u1;
      flag = true;
    }
    else if(modifiedSentence.length()>5 && (modifiedSentence.substring(0,4)).equals("SEND")){
      System.out.print(modifiedSentence.substring(5)+"> Type Subject: ");
      String subject = inFromUser.readLine();
      outToServer.writeBytes(subject+ '\n');
      System.out.print(modifiedSentence.substring(5)+"> Type Content: ");
      String dummy = inFromUser.readLine();
      String message = "";
      while(!dummy.contains("###")){
        message = message + dummy + "\n";
        dummy = inFromUser.readLine();
      }
      message = message + dummy + "\n";
      outToServer.writeBytes(message+ '\n');
      modifiedSentence = inFromServer.readLine();
      System.out.println(modifiedSentence);
    }
  }
 }
}