/*CSCI 246 FINAL PROJECT
 * Nisha Gurunath Bharathi
 * 109896013
 */
package computerArchitectureProject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class CA {
	
	int sum = 0,z=0,OF=0;
	int[] storeresult=new int[32];int icount=0;
	char[] array1=new char[377]; int x=0;
	String regname,rs_regname,rt_regname,rd_regname; int gp=100,pc=0,zero=0;
	
	int numofInstructions;int flag=0;
	int numofData;
	
	String[] memory=new String[32]; //8instructions*4parts each= 32 total mem locations needed
	char[] charmemory=new char[9];
	char[] charregisters=new char[32];
	int[] A=new int[32];
	int[] B=new int[32];
	int[] addPC={0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};  //pc=pc+4 this pc
	int[] addFour={0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0};  //pc=pc+4 this four
	List<String> OriginalmemoryList=new ArrayList<String>();
	List<String> memoryList=new ArrayList<String>();
	List<String> registersList=new ArrayList<String>();
	StringBuilder sb,sb2;//=new StringBuilder();
	
	List<String> list=new ArrayList<String>();
	String storeInstructions = null;int y = 0;
	
	String ALUSrcA,IorD,ALUSrcB,ALUOp,PCSource,RegDst,MemToReg,AluControl;
	int[] PC;
	String opcode,rs,rt,rd,shamt,Imm,OffsetAddedToPC;
	String funct;
	int s1;
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		CA caObj=new CA();
	
		//read a file
		caObj.readFromFile();
		System.out.println("Total clock cycles : "+caObj.icount);
	}
	//and method
	int and(int x,int y)
	{
		int z;
		//z=x & y;
		if(x==0 && y==1){z=0;}
		else if(x==1 && y==0){z=0;}
		else if(x==0 && y==0){z=0;}
		else{z=1;}
		System.out.println("and is "+z);
		return z;
		
	}
	//or method
	int or(int x,int y)
	{
		int z;
		//z=x | y;
		if(x==0 && y==1){z=1;}
		else if(x==1 && y==0){z=1;}
		else if(x==1 && y==1){z=1;}
		else{z=0;}
		System.out.println("or is "+z);
		return z;
		
	}
	//nor
	int nor(int x,int y)
	{
		int z;
		z=x | y;
		z=~z;
		System.out.println("or is "+z);
		return z;
		
	}
	//one bit full adder method
	int OneBitFullAdder(int a,int b,int cin)
	{
		
		if(a==0 && b==0 && cin==0)
		{
			sum=0;
			cin=0;
		}
		else if(a==0 && b==0 && cin==1)
		{
			sum=1;
			cin=0;
		}
		else if(a==0 && b==1 && cin==0)
		{
			sum=1;
			cin=0;
		}
		else if(a==0 && b==1 && cin==1)
		{
			sum=0;
			cin=1;
		}
		else if(a==1 && b==0 && cin==0)
		{
			sum=1;
			cin=0;
		}
		else if(a==1 && b==0 && cin==1)
		{
			sum=0;
			cin=1;
		}
		else if(a==1 && b==1 && cin==0)
		{
			sum=0;
			cin=1;
		}
		else if(a==1 && b==1 && cin==1)
		{
			sum=1;
			cin=1;
		}
		storeresult[z]=sum; z++;
		
		return cin;
		
	}
	//2x1 mux
	String mux_2x1(String x,String y,String control)
	{
		if(control=="0") { return x;}
		else { return y; }
		
	}
	//4x1 mux
	String mux_4x1(String x1,String x2,String x3,String x4,String control)
	{
		if(control=="00"){return x1;}
		else if(control=="01"){return x2;}
		else if(control=="10"){return x3;}
		else if(control=="11"){return x4;}
		else{return null;}
		
	}
	//one bit alu
	int OneBitAlu(int a,int b,int cin,String Ainv,String Binv,int less,String op)
	{
		int result = 0;
	
		switch (op)
		{
		case "0000":
			//	System.out.println("and operation");
				result=and(a,b);
			break;
			
		case "0001":
			//	System.out.println("or operation");
				result=or(a,b);
			break;
			
		case "0010":
			//	System.out.println("add operation");
				if(Ainv.endsWith("1")){a=~a;}
				if(Binv.equals("1")){b=~b;}
				result=OneBitFullAdder(a,b,cin);
			break;
			
		case "0110":
			int c=a-b;
			result=c;
			break;
			
		case "0111":
				if(a<b) s1=1;
				else
					s1=0;
				result=s1;
			break;
			
		case "1100":
		//		System.out.println("nor operation");
				result=or(a,b);
				if(result==0)result=1;
				else result=0;
			break;
			
			
		}
		return result;
		
	}
	//onebit alu with overflow check
	int OneBitAluOF(int a,int b,int cin,String Ainv,String Binv,int less,String op)
	{
		int result = 0,OF=0;
		
		switch (op)
		{
		case "0000":
			//	System.out.println("and operation");
				result=and(a,b);
			break;
			
		case "0001":
			//	System.out.println("or operation");
				result=or(a,b);
			break;
			
		case "0010":
			//	System.out.println("add operation");
				if(Ainv.equals("1")){a=~a;}
				if(Binv.equals("1")){b=~b;}
				result=OneBitFullAdder(a,b,cin);
			break;
			
		case "0110":
			//	System.out.println("sub operation");
				//result=and(a,b);
			break;
			
		case "0111":
			//	System.out.println("slt operation");
				//result=and(a,b);
			break;
			
		case "1100":
			//	System.out.println("nor operation");
				result=or(a,b);
				if(result==0)result=1;
				else result=0;
			break;
				
		}
		//check overflow:
	//	System.out.println("iteration "+z);
		if(z==32)
		{
			if(a==b)
			{
				if(a!=result)
				{
					//overflow
					OF=1;
					//System.out.println("overflow");
				}
			}
		}
		
		return result;//return cout and OF
		
	}
	//32 bit alu mux
	int[] _32BitAlu(int[] a,int[] b,String Ainv,String Binv,String op)
	{
		//System.out.println("\nInside 32 bit alu \n");
		int cin=0;
		for(int i=31;i>0;i--)
		{
			cin=OneBitAluOF(a[i],b[i],cin,Ainv,Binv,cin,op);
		}
		if(OF==1)
		{	//make z 0 now reinitialise
			z=0;
		//	System.out.println("overflow");
		}
		else
		{	z=0;
			//make z 0 now reinitialise
		//	System.out.println("no overflow");
		}
		return storeresult;//return OF and zero
		
	}
	
	//reading inpput from the file
	void readFromFile()
	{
		//specify the path where the input txt file is present 
		File file = new File("C:/Users/NISHA/Desktop/input.txt");
		FileInputStream fis = null;

		try {
			fis = new FileInputStream(file);

			System.out.println("Total file size to read (in bytes) : "
					+ fis.available());
			int i=0;
			int content;
			while ((content = fis.read()) != -1) {
				// convert to char and display it
				array1[x]=(char) content; x++; //array1 has all data from file bit by bit
			//	System.out.println("i="+i+"   "+ (char) content);i++;
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		
/************************started loading all 11 rows of input to list************************/
		//store number of instructions and num of data
		numofInstructions=array1[0];
		numofData=array1[2];

		//take the data from the file and convert to char array and store every32 bits to a list called list
	
		//start from 5 because in array1 i have stored in bit by bit and instruction loading starts from 5th row
		for (int j = 5; j < array1.length; j=y+2)
		{	
			storeInstructions = null;
			for (y = j; y <(j+32); y++)//32 bits is every instruction
			{
				//store 32 bits to a string called storeInstructions
				storeInstructions+=(char)(array1[y]);
			}
			//add 32bits to list//full input of 8 instructions stored in list now
			list.add(storeInstructions);
		}
		
		//print the list of all 11 rows read from file
		System.out.println("Instructions and data read from file is:");
		for(String string : list) {
			//System.out.println("Input data: "+string);
		}
/*********************************************finished loading all 11 rows of input to list******************************/
		
HashMap<Integer, String> memoryMap=new HashMap<Integer, String>();
HashMap<Integer, String> regMap=new HashMap<Integer, String>();
HashMap<String, String> registersNewMap=new HashMap<String, String>();
		
/***********started loading first 8 rows of input(from list) to memorylist in 8 bits each************/
		
int memcount=0;
		//get one instruction at a time from list
		for (int s = 0; s < list.size()-3;	 s++) //list.size()-3 its list-3 because last 3 values of list are data and not instructions
		{	
			charmemory=list.get(s).toCharArray();//convert list(one instruction string) to char array
			for (int x = 4; x < charmemory.length; x=x+8) //start loop from 4 because null is 1st val in my list so that is 4 bits
			{	sb=new StringBuilder(); //reinitialise stringbuilder each time 
				for (int w = x; w < x+8; w++)//access 8 bits from char array and append to string builder and store to memorylist 
				{	
					sb.append(charmemory[w]);//8 bits appended to a string builder sb
				}
				//copy the 8 bits to memoryMap as a single string
				memoryMap.put(memcount, sb.toString()); memcount++;
			}
		}
		
		//copy data to main memory starting address from 100 i.e gp
		int memdatacount=100;
		for (int s = list.size()-3; s < list.size();	 s++) //list.size()-3 its list-3 because last 3 values of list are data and not instructions
		{	
			charmemory=list.get(s).toCharArray();//convert list(one instruction string) to char array
			for (int x = 4; x < charmemory.length; x=x+8) //start loop from 4 because null is 1st val in my list so that is 4 bits
			{	sb=new StringBuilder(); //reinitialise stringbuilder each time 
				for (int w = x; w < x+8; w++)//access 8 bits from char array and append to string builder and store to memorylist 
				{	
					sb.append(charmemory[w]);//8 bits appended to a string builder sb
				}
				//copy the 8 bits to memoryMap as a single string
				memoryMap.put(memdatacount, sb.toString()); memdatacount++;
			}
		}
		
		System.out.println("Memory with instructions and data:\n");
		for (int key : memoryMap.keySet()) {
			System.out.println(key+"--"+memoryMap.get(key));
		}
		
/************************start loading last 3 rows of 32 bit data values from list to registersmap************************/
		
		int regcount=0;
		System.out.println("Register content is:\n");
		for (int i = list.size()-3; i < list.size(); i++) {
			String value=list.get(i); value=value.substring(4, value.length());
			regMap.put(regcount, value); regcount++;
		}
		
		//finished storing 32 bits of 3 rows of data from input file to registersmap now print them
		for (int key : regMap.keySet()) {
			System.out.println(key+"--"+regMap.get(key));
		}
		
	
		
/************************start loading last 3 rows of list 32bit values each to registers************************/
		/*************Find the opcode and funct code now*********************/
		

	//System.out.println("Fetching instruction from Memory\n");
	for (int a = 0; a <32; a=a+4) //pc=pc+4 //if(PC==addFour)
	{	//System.out.println("============================a is "+a);
		
		icount++;		System.out.println("Iteration: clock cycles- "+icount+"\n");
		//1.fetch the instruction from mem and do pc=pc+4 in memory
		//do operation IR=IM[addr] i.e fetch 4 rows from memorylist and put it to IR
		StringBuilder IR=new StringBuilder();
			
			//fetch one by one instruction at a time and do operations
			int count=0;
			for (int p = a; count <4; p++) //pc=pc+4 //if(PC==addFour)
			{
				IR.append(memoryMap.get(p));	
				count++;
			}
			
		//extract 6 bits opcode from IR
		opcode=IR.substring(0,6);
		
		//extract 5 bits funct code from IR
		funct=IR.substring(26, 31);
		
		//convert 6 bits binary to decimal to find the type of instruction
		int opcodeDecimal=ConvertFromBinaryToDecimal(opcode);
		int functCodeDecimal=ConvertFromBinaryToDecimal(funct);	

	
	System.out.println("\n***IF stage ****\n");
	//set needed control signals
	ALUSrcA="0";
	IorD="0";
	ALUSrcB="01";
	ALUOp="00";
	PCSource="00";
	AluControl="0010"; //I set alucontrol as 0010 which means add for pc=pc+4 in IF stage
	
	//call mux_2x1 and stored result in string i.e PC
	String mux_2x1_Result= mux_2x1("PC","A",ALUSrcA); 
	//System.out.println("2x1 mux returned result as: "+mux_2x1_Result+"\n");
	
	//call mux_4x1 and store result in string i.e 4
	String mux_4x1_Result= mux_4x1("B","4","0","0",ALUSrcB);
	//System.out.println("4x1 mux returned result as: "+mux_4x1_Result+"\n");
	
	//call 32bitalu now to add pc=pc+4
	if(mux_2x1_Result.equalsIgnoreCase("PC") && mux_4x1_Result.equalsIgnoreCase("4"))
	{
		PC=_32BitAlu(addPC, addFour,"0", "0", AluControl); 
		
		if(flag!=1){
		pc=pc+4;}
		System.out.println("IR is "+IR);
		System.out.println("\nPC: "+IR+ "   Instruction counter: "+icount);
	}

	
	System.out.println("\n***ID stage ***\n");
	ALUSrcA="0";	
	ALUSrcB="11";
	ALUOp="00";
	
	if(opcodeDecimal==0) //for add,sub,and,or,nor,set opcode is 0 so consider functional code for these cases to go inside switch case
	{
		opcodeDecimal=functCodeDecimal;// i am assigning funct code as opcode now just to go to the switch cases
	}
	
 //System.out.println("\nInstruction decoded\n Opcode in decimal is "+opcodeDecimal);
 //System.out.println("funct code is "+functCodeDecimal);
	switch(opcodeDecimal)
	{
	case 35://load
			System.out.println("\nLoad Instruction\n");
			ALUSrcA="1";	
			ALUSrcB="10";
			ALUOp="00";
			
			//memory read
			IorD="1";
			
			//decode
			//opcode-6, rs-5,rt-5,Imm-16
			
			rs=IR.substring(6,11);
			rt=IR.substring(11,16);
			Imm=IR.substring(16, 32);
			
			
			rs_regname=getRegistername(ConvertFromBinaryToDecimal(rs));
			System.out.println("\nrs is: "+rs_regname);
			rt_regname=getRegistername(ConvertFromBinaryToDecimal(rt));
			System.out.println("\nrt is: "+rt_regname);
			
			
			System.out.println("\n**EX stage ****\n");
			
			//calculate address
			int address;
			
			address=ConvertFromBinaryToDecimal(Imm)+gp;
			System.out.println(getRegistername(ConvertFromBinaryToDecimal(rs))+": "+address);
			
		
			System.out.println("\n****WBstage ****\n");
			
			//write back setup
			RegDst="0";
			MemToReg="1";//RegWrite
			
			regname=getRegistername(ConvertFromBinaryToDecimal(rt));
			StringBuilder s=new StringBuilder();
			for(int i=0;i<4;i++)
			{	
				s.append(memoryMap.get(address+i)); //System.out.println("sb is "+sb);
			}
			registersNewMap.put(regname, s.toString());
			//finished load now, print register contents
			System.out.println("\nRegister contents are:\n");
			for (String key : registersNewMap.keySet()) {
				System.out.println(key+"--"+registersNewMap.get(key));
			}
			
		break;
	case 43://store
		System.out.println("\n Store Instruction\n");
			ALUSrcA="1";	
			ALUSrcB="10";
			ALUOp="00";
			
			//decode
			//opcode-6, rs-5,rt-5,Imm-16
			
			
			rs=IR.substring(6,11);
			rt=IR.substring(11,16);
			Imm=IR.substring(16, 32);
			
			System.out.println("\nRegister contents are:");
			System.out.println("\nrs is: "+getRegistername(ConvertFromBinaryToDecimal(rs)));
			System.out.println("\nrt is: "+getRegistername(ConvertFromBinaryToDecimal(rt)));
			System.out.println("\nImm is: "+ConvertFromBinaryToDecimal(Imm));
			
			//memory write
			IorD="1";int add = 0;
			if(getRegistername(ConvertFromBinaryToDecimal(rs))=="gp"){add=100;}
				add=add+ConvertFromBinaryToDecimal(Imm);
				
				String _32bits=registersNewMap.get(getRegistername(ConvertFromBinaryToDecimal(rt)));
				
				
				memoryMap.put(add, _32bits.substring(0, 8));
				memoryMap.put(add+1, _32bits.substring(8, 16));
				memoryMap.put(add+2, _32bits.substring(16, 24));
				memoryMap.put(add+3, _32bits.substring(24, 32));
				
			System.out.println("add is "+add);
			
			
			System.out.println("\nRegister contents are:\n");
			for (String key : registersNewMap.keySet()) {
				System.out.println(key+"--"+registersNewMap.get(key));
			}
			
			System.out.println("\nMemory contents are:\n");
			for (Integer key : memoryMap.keySet()) {
				System.out.println(key+"--"+memoryMap.get(key));
			}
		break;
	
	case 17://alu- sub
		System.out.println("\n Sub Instruction\n");
		ALUSrcA="1";	
		ALUSrcB="00";
		ALUOp="10";
		
		//decode
		//opcode-6, rs-5,rt-5,rd-5,shamt-5,funct-6
		
		rs=IR.substring(6,11);
		rt=IR.substring(11,16);
		rd=IR.substring(16, 21);
		shamt=IR.substring(21, 26);
		funct=IR.substring(26, 32);
		
		rs_regname=getRegistername(ConvertFromBinaryToDecimal(rs));
		rt_regname=getRegistername(ConvertFromBinaryToDecimal(rt));
		rd_regname=getRegistername(ConvertFromBinaryToDecimal(rd));
		
		System.out.println("rs is "+rs_regname);
		System.out.println("rt is "+rt_regname);
		System.out.println("rd is "+rd_regname);
		//Rtype completion
				RegDst="1";
				MemToReg="0";//RegWrite
		String sub_rt=registersNewMap.get(rs_regname);
		int subresult=ConvertFromBinaryToDecimal(sub_rt);
		subresult=subresult-1;
		String newres=BinaryFormat(subresult);
		registersNewMap.put(rs_regname,newres);
		
		System.out.println("\nRegister contents are:\n");
		for (String key : registersNewMap.keySet()) {
			System.out.println(key+"--"+registersNewMap.get(key));
		}
		
	break;
	
	
	
	case 37://or
		System.out.println("\n Or Instruction\n");
		
		ALUSrcA="1";	
		ALUSrcB="00";
		ALUOp="10";
		
		//decode
		//opcode-6, rs-5,rt-5,rd-5,shamt-5,funct-6
		
		rs=IR.substring(6,11);
		rt=IR.substring(11,16);
		rd=IR.substring(16, 21);
		shamt=IR.substring(21, 26);
		funct=IR.substring(26, 32);
		
		System.out.println("\nRegister contents are:");
		System.out.println("\nrs is: "+rs);
		System.out.println("\nrt is: "+rt);
		System.out.println("\nrd is: "+rd);
		System.out.println("\nshamt is: "+shamt);
		
		//Rtype completion
		RegDst="1";
		MemToReg="0";//RegWrite
		System.out.println("\nRegister contents are:\n");
		for (String key : registersNewMap.keySet()) {
			System.out.println(key+"--"+registersNewMap.get(key));
		}
		
	break;
	
	case 4://branch BEQ
		
		System.out.println("\n Branch Instruction\n");
			ALUSrcA="1";	
			ALUSrcB="00";
			ALUOp="01";
			PCSource="01";
			
			//decode
			//opcode-6, rs-5,rt-5,Imm-16
		
			rs=IR.substring(6,11);
			rt=IR.substring(11,16);
			Imm=IR.substring(16, 32);
			
			System.out.println("\nRegister contents are:");
		
			rs_regname=getRegistername(ConvertFromBinaryToDecimal(rs));
			System.out.println("\nrs is: "+rs_regname);
			rt_regname=getRegistername(ConvertFromBinaryToDecimal(rt));
			System.out.println("\nrt is: "+rt_regname);
			
			if(rs_regname=="s1"){
				if(s1==0){
					//exit
					flag=0;
					a=24;
				}
				//else
			}
			System.out.println("s1 is: "+s1);
			System.out.println("\nRegister contents are:\n");
			for (String key : registersNewMap.keySet()) {
				System.out.println(key+"--"+registersNewMap.get(key));
			}
			
		break;
	
	case 2://jump 
		
		System.out.println("\n Jump Instruction\n");
			PCSource="10";
			
			//decode
			//opcode-6,offset added-26
		
			OffsetAddedToPC=IR.substring(6, 32);//offset added
			flag=1;
			if(s1==1){a=8; }
			System.out.println("\nRegister contents are:\n");
			for (String key : registersNewMap.keySet()) {
				System.out.println(key+"--"+registersNewMap.get(key));
			}
		
		break;
	case 21://set
		System.out.println("\n Set \n");
		rs=IR.substring(6,11);
		rt=IR.substring(11,16);
		rd=IR.substring(16, 21);
		Imm=IR.substring(21, 32);
		System.out.println("imm is "+Imm);
		System.out.println("\nRegister contents are:");
		
		rs_regname=getRegistername(ConvertFromBinaryToDecimal(rs));
		System.out.println("\nrs is: "+rs_regname);
		rt_regname=getRegistername(ConvertFromBinaryToDecimal(rt));
		System.out.println("\nrt is: "+rt_regname);
		rt_regname=getRegistername(ConvertFromBinaryToDecimal(rd));
		System.out.println("\nrd is: "+rd_regname);
		System.out.println("\nImm is: "+Imm);
		
		System.out.println("\n***EX  stage ***\n");
		
		int n1=ConvertFromBinaryToDecimal(registersNewMap.get(getRegistername(ConvertFromBinaryToDecimal(rs))));
		int n2=ConvertFromBinaryToDecimal(registersNewMap.get(getRegistername(ConvertFromBinaryToDecimal(rt))));
		
		System.out.println("n1 is "+n1+" n2 is "+n2);
		if(n1<n2)
		{	
			//set s1=1
			s1=1;
		}
		else{ s1=0;}
		System.out.println("s1 is "+s1);
		System.out.println("\nRegister contents are:\n");
		for (String key : registersNewMap.keySet()) {
			System.out.println(key+"--"+registersNewMap.get(key));
		}
	break;
	}
	
	
	/**********************************ID EX MEM WB stages done **********************************/
	
	
	System.out.println("\n=============================================================Instruction done=====================================================================\n"); 
	
  }//end of outer for loop
}//end of testfile method()
	
String getRegistername(int value)
{
	switch(value)
	{
	case 0: regname="zero"; break;
	case 1: regname="at"; break;
	case 2: regname="v0"; break;
	case 3: regname="v1"; break;
	case 4: regname="a0";break;
	case 5: regname="a1";break;
	case 6: regname="a2";break;
	case 7: regname="a3";break;
	case 8: regname="t0";break;
	case 9: regname="t1";break;
	case 10: regname="t2";break;
	case 11: regname="t3";break;
	case 12: regname="t4";break;
	case 13: regname="t5";break;
	case 14: regname="t6";break;
	case 15: regname="t7";break;
	case 16: regname="s0";break;
	case 17: regname="s1";break;
	case 18: regname="s2";break;
	case 19: regname="s3";break;
	case 20: regname="s4";break;
	case 21: regname="s5";break;
	case 22: regname="s6";break;
	case 23: regname="s7";break;
	case 24: regname="t8";break;
	case 25: regname="t9";break;
	case 26: regname="k0";break;
	case 27: regname="k1";break;
	case 28: regname="gp"; break;
	case 29: regname="sp"; break;
	case 30: regname="fp"; break;
	case 31: regname="ra"; break;
	}
	return regname;
		
}
int ConvertFromBinaryToDecimal(String inputOpcode)
{
		//System.out.println("string opcode is "+opcode);
		int decimalValue = Integer.parseInt(inputOpcode, 2);
       // System.out.print("\nEquivalent Decimal Value of " +decimalValue);
        return decimalValue;
}
String BinaryFormat(int number){
    int binary[] = new int[4];
    int index = 0; String s = null;
    while(number > 0){
        binary[index++] = number%2;
        number = number/2;
    }
    StringBuilder sf=new StringBuilder();
    for(int i=0;i<28;i++){
    	sf.append("0");
    }System.out.println("bin lengtg  "+binary.length);
    for(int i = binary.length-1;i >=0;i--){
    	sf.append(binary[i]);
    }//System.out.println("sf is "+sf);
    
    return sf.toString();
}

}//end of main
