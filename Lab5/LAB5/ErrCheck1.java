import java.io.*;
import java.net.*;
import java.util.*;
import java.io.FileWriter;
import java.nio.file.Files;

class ErrCheck1{

	public static ArrayList<Integer> binary;

	public static void main(String args[]) throws Exception{
		ArrayList<Integer> p1 = new ArrayList<Integer>();
		ArrayList<Integer> p2 = new ArrayList<Integer>();
		ArrayList<Integer> p4 = new ArrayList<Integer>();
		ArrayList<Integer> p8 = new ArrayList<Integer>();
		ArrayList<Integer> p16 = new ArrayList<Integer>();
		int n;
		ArrayList<String> str = new ArrayList<String>();
		for(int i=0;i<31;i++){
			iTob(i+1);
			n = binary.size();
			if(binary.get(0)==1){
				p1.add(i+1);
			}
			if(n>1 && binary.get(1)==1){
				p2.add(i+1);
			}
			if(n>2 && binary.get(2)==1){
				p4.add(i+1);
			}
			if(n>3 && binary.get(3)==1){
				p8.add(i+1);
			}
			if(n>4 && binary.get(4)==1){
				p16.add(i+1);
			}
		}
		// System.out.println(p1);
		// System.out.println(p2);
		// System.out.println(p4);
		// System.out.println(p8);
		// System.out.println(p16);
		
		String input = readFile(args[0]);

		FileWriter writer = new FileWriter(args[1]);
		int x = input.length()/26;
		String[] inputs =input.split("\n",-1);
		// for(int i=0;i<x;i++){
		// 	String temp = "";
		// 	for(int j=0;j<26;j++){
		// 		temp=temp+input.charAt(i*26+j);
		// 	}
		// 	inputs.add(temp);
		// }
		System.out.println(inputs.length+"");
		for(int i=0;i<inputs.length-1;i++){
			str = new ArrayList<String>();
			for(int m=0;m<31;m++){
				str.add("0");
			}
			String sn = inputs[i];
			int count=0;
			int l=0;
			for(int m=0;m<31;m++){
				if(m+1!=1 && m+1!=2 && m+1!=4 && m+1!=8 && m+1!=16){
					str.set(m,sn.charAt(l)+"");
					l++;
				}
			}
			//System.out.println(str);
			for(int i1=0;i1<p1.size();i1++){
				int idx = p1.get(i1)-1;
				if(str.get(idx).equals("1"))
					count++;
			}
			//System.out.println(count+"");
			if(count%2==1)
				str.set(0,"1");
			count=0;
			for(int i1=0;i1<p2.size();i1++){
				int idx = p2.get(i1)-1;
				if(str.get(idx).equals("1"))
					count++;
			}
			//System.out.println(count+"");
			if(count%2==1)
				str.set(1,"1");
			count=0;
			for(int i1=0;i1<p4.size();i1++){
				int idx = p4.get(i1)-1;
				if(str.get(idx).equals("1"))
					count++;
			}
			//System.out.println(count+"");
			if(count%2==1)
				str.set(3,"1");
			count=0;
			for(int i1=0;i1<p8.size();i1++){
				int idx = p8.get(i1)-1;
				if(str.get(idx).equals("1"))
					count++;
			}
			//System.out.println(count+"");
			if(count%2==1)
				str.set(7,"1");
			count=0;
			for(int i1=0;i1<p16.size();i1++){
				int idx = p16.get(i1)-1;
				if(str.get(idx).equals("1"))
					count++;
			}
			//System.out.println(count+"");
			if(count%2==1)
				str.set(15,"1");
			count=0;
			//System.out.println(str);
			String ans="";
			for(int x1=0;x1<31;x1++){
				ans=ans+str.get(x1);
			}
			writer.write("Input:"+sn+"\n");
			writer.write("Output:"+ans+"\n");
         	
		}
		writer.close();

	}
	public static void iTob(int i){
		binary = new ArrayList<Integer>();
		binary.add(i%2);
		i=i/2;
		while(i!=0){
			binary.add(i%2);
			i=i/2;
		}
	}
	public static String readFile(String s){
		File f = new File(s);
		try{
			byte[] bytes = Files.readAllBytes(f.toPath());
			return new String(bytes,"UTF-8");
		}catch(IOException e){
			e.printStackTrace();
		}
		return null;
	}
}