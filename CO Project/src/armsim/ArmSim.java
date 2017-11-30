
package armsim;
import java.util.Scanner;
import java.util.Stack;
import java.io.*;
public class ArmSim extends ArmVariables {

	//CONSOLE INPUT
	Scanner in=new Scanner(System.in);
	//FILE outPut
	File f=new File("output.txt");
	PrintStream outFile;
	ArmSim() {
		try{
			this.outFile=new PrintStream(f);
		}
		catch(Exception e){
			System.out.println("NO FILE FOR INPUT TERMINATE EXECUTION IF YOU WANT!");
		}

		for (int i = 0; i < 16; i++){
			this.R[i] = 0;
			this.programStack.add(new Stack<Long>());
		}
		for (int i = 0; i < 4000; i++) {
			this.MEMFORDATA[i] = 0L;
			this.MEMFORINST[i] = new String("0");
		}
		this.isLinkBranch=false;
		this.BranchInstruction = false;
		this.DataProcessInstruction = false;
		this.DataTransferInstruction = false;
		this.swi_exit = false;
		this.swi_print=false;
		this.swi_read=false;
		this.takeBranch = false;
		this.toLoad = false;
		this.toStore = false;
		this.NegativeFalg=false;
		this.OverflowFlag=false;
		this.ZeroFlag=false;
		this.instruction_word = "";
		this.executedCalc = 0;
		this.conditionValue = 0;
		this.immediateValue = 0;
		this.opcodeValue = 0;
		this.operand1 = 0;
		this.operand2 = 0;
		this.register1 = 0;
		this.register2 = 0;
		this.destinationRegister = 0;


	}

	@Override
	void swi_exit() {

		System.exit(0);

	}

	@Override
	void swi_print() {

		if(this.R[0]==1){
			try{
				outFile.print(this.R[1]);
				outFile.println();
			}
			catch(Exception e){
				System.out.println("output file was not updated");
			}
			System.out.println("SWI_PRINT : value in r1 is "+this.R[1]);
		}
		else
			System.out.println("SWI_PRINT : CAN'T PRINT");

	}

	@Override
	void swi_read() {

		if(this.R[0]==0){

			this.R[0]=this.in.nextLong();
			System.out.println("SWI_READ : read value is "+ this.R[0]);	
			/*
			try{
				//this code is if want to read from file
				//this.R[0]=this.readFile.nextLong();
				System.out.println("SWI_READ : READ VALUE IS "+ this.R[0]);	
			}
			catch(Exception e){
				System.out.println("SWI_READ : CAN'T READ FROM FILE SOME ERORRRR!");
				System.exit(0);
			}
			 */

		}
		else{
			System.out.println("SWI_READ : can't read, some error!");
		}

	}

	@Override
	String fetch() {
		String hexString = new String(this.read_word_Instruction((int) this.R[PCregister]));
		String temp = Long.toBinaryString(Long.parseLong(hexString, 16));

		this.instruction_word = String.format("%32s", temp).replace(" ", "0");
		System.out.println("");
		// System.out.println(this.instruction_word);
		// System.out.println(temp+" temp");
		// System.out.println(this.instruction_word);
		System.out.println("Fetch instruction 0x" + hexString + " from address 0x" + Long.toHexString(R[PCregister]));
		this.R[PCregister] += 4;
		return this.instruction_word;
	}

	@Override
	void decode() {
		// TODO Auto-generated method stub
		//System.out.println(this.swi_exit+" SWI_EXIT");
		int function;

		function = Integer.parseInt(this.instruction_word.substring(4, 6), 2);
		//
		switch (function) {
		case 0:
			this.DataProcessInstruction = true;
			//System.out.println("DECODE :DATA PROCESS");
			/*
			 * code to decode data process instructions here
			 */

			this.immediateValue = Integer.parseInt(this.instruction_word.substring(6, 7), 2);
			this.opcodeValue = Integer.parseInt(this.instruction_word.substring(7, 11), 2);
			this.register1 = Integer.parseInt(this.instruction_word.substring(12, 16), 2);
			// System.out.println(register1+" "+this.instruction_word);
			this.destinationRegister = Integer.parseInt(this.instruction_word.substring(16, 20), 2);
			//System.out.println("DESTINATION REGISTER :"+this.registerDest+" "+this.instruction_word);
			//System.out.println(this.registerDest+" REGISTER DEST " + this.instruction_word);
			if (this.register1 != 0)
				this.operand1 = this.R[register1];
			if (this.immediateValue == 1) {
				this.operand2 = Long.parseLong(this.instruction_word.substring(24, 32), 2);
				int rotateImmediateBy = Integer.parseInt(this.instruction_word.substring(20, 24), 2);
				this.operand2 = operand2 << rotateImmediateBy;
			} else {

				this.register2 = Integer.parseInt(this.instruction_word.substring(28, 32), 2);
				// System.out.println(register2+" register2
				// "+this.instruction_word);
				this.operand2 = this.R[register2];
				// NOW SHIFTINH OPERAND @ THERE IF NEED TO SHIFT
				String shiftCodeWord = this.instruction_word.substring(20, 28);// size
				
				int shiftingType = Integer.parseInt(shiftCodeWord.substring(7, 8), 2);
				// for shift by immediate or by register value

				int rs = 0, shiftAmount = 0;
				if (shiftingType == 1) {
					rs = Integer.parseInt(shiftCodeWord.substring(0, 4), 2);
					shiftAmount = (int) this.R[rs];
				} else if (shiftingType == 0) {
					shiftAmount = Integer.parseInt(shiftCodeWord.substring(0, 5), 2);
				}

				String shift = shiftCodeWord.substring(5, 7);
				/*
				 * need to see this roation is done here
				 */
				if (shift.equals("11")) {
					this.operand2 = this.operand2 >> shiftAmount;
				this.operand2 = this.operand2 | this.operand2 << (64 - shiftAmount);
				} else if (shift.equals("00"))// left shifting
					this.operand2 = this.operand2 << shiftAmount;
				else if (shift.equals("01"))// right shift
					this.operand2 = this.operand2 >> shiftAmount;

			}
			System.out.println("DECODE : r1 is " + this.register1 + " r2 is " + this.register2
					+ " rd is " + this.destinationRegister);


			break;
		case 1:
			this.DataTransferInstruction = true;
			System.out.println("DECODE : Decoded instruction is of type data transfer");
			/*
			 * code to decode transfer instruction here
			 */
			// taking last value of 6 bit opcode as for store it will
			// be 0 and for load it will be 1(24 & 25 are opcodes)
			int LoadOrStore = Integer.parseInt(this.instruction_word.substring(11, 12), 2);
			//System.out.println(this.opcode + " " + this.instruction_word);
			if (LoadOrStore == 1)
				this.toLoad = true;
			else if (LoadOrStore == 0)
				this.toStore = true;
			else {
				//System.out.println("No Load Store");
			}
			break;

		case 2:
			this.BranchInstruction = true;
			System.out.println("DECODE : Instruction is branch");
			/*
			 * code to decode branch instruction here
			 */
			this.conditionValue = Integer.parseInt(this.instruction_word.substring(0, 4), 2);

			if ( ( this.conditionValue == 0 && this.ZeroFlag ) || ( this.conditionValue == 1 && !this.ZeroFlag )
					|| ( this.conditionValue == 10 && ( this.NegativeFalg == this.OverflowFlag )) 
					|| ( this.conditionValue == 11 && ( this.NegativeFalg != this.OverflowFlag ))
					|| (this.conditionValue == 12 && ( this.ZeroFlag == false && this.NegativeFalg == this.OverflowFlag))
					|| (this.conditionValue == 13 && ( this.ZeroFlag == true && this.NegativeFalg != this.OverflowFlag) )
					|| (this.conditionValue == 14 ) ){
				this.takeBranch = true;
				if(Integer.parseInt(this.instruction_word.substring(4, 7),2)==5){
					int linkBit=Integer.parseInt(this.instruction_word.substring(7, 8),2);
				//	System.out.println(linkBit);
					if(linkBit==1)
						this.isLinkBranch=true;
				}
				//System.out.println();
			}
			else{
				this.takeBranch=false;
				System.out.println("BRANCH :Can't be taken");
			}
			break;
		case 3:
			int swiType=Integer.parseInt(this.instruction_word.substring(24, 32),2);
			if(swiType==108){
				this.swi_read=true;
				System.out.println("DECODE : instruction is SWI_READ");

			}
			else if(swiType==107){
				this.swi_print=true;
				System.out.println("DECODE : instruction is SWI_PRINT");

			}
			else if(swiType==17){
				this.swi_exit = true;
				System.out.println("DECODE : instruction is SWI_EXIT");
				this.swi_exit();
			}
			break;
		}

	}

	@Override
	int execute() {

		if (this.DataProcessInstruction) {
			/*
			 * code for data process
			 */
			switch(this.opcodeValue){
			case 0://AND
				//24 se 28
				int accumlate=Integer.parseInt(this.instruction_word.substring(10, 11),2);
				int multiplyCondition1=Integer.parseInt(this.instruction_word.substring(4, 10),2);
				int multiplyCondition2=Integer.parseInt(this.instruction_word.substring(24, 28),2);
				if(accumlate==0 && multiplyCondition1==0 && multiplyCondition2==9){
					int rm=Integer.parseInt(this.instruction_word.substring(28, 32),2);
					int rs=Integer.parseInt(this.instruction_word.substring(20, 24),2);
					this.operand1=this.R[rs];
					this.operand2=this.R[rm];
					this.destinationRegister=Integer.parseInt(this.instruction_word.substring(12, 16),2);
					this.executedCalc=this.operand1*this.operand2;
					System.out.println("EXECUTE : MUL "+ this.operand1+" by "+this.operand2);


				}
				else{
					this.executedCalc=this.operand1&this.operand2;
					System.out.println("EXECUTE : AND "+ this.operand1+" with "+this.operand2);
				}
				break;
			case 1://EOR
				this.executedCalc=this.operand1 ^ this.operand2;
				System.out.println("EXECUTE : EOR "+ this.operand1+" with "+this.operand2);
				break;
			case 2:
				this.executedCalc=this.operand1 - this.operand2;
				System.out.println("EXECUTE : SUB "+ this.operand2+" from "+this.operand1);
				break;
			case 4:
				this.executedCalc=this.operand1 + this.operand2;
				System.out.println("EXECUTE : ADD "+ this.operand1+" with "+this.operand2);
				break;
			case 10:
				this.executedCalc=this.operand1-this.operand2;
				if(this.executedCalc<0)
					this.NegativeFalg=true;
				else
					this.NegativeFalg=false;
				if(this.executedCalc==0)
					this.ZeroFlag=true;
				else
					this.ZeroFlag=false;
				System.out.println("EXECUTE : CMP "+ this.operand1+" with "+this.operand2);
				//System.exit(0);
				break;
			case 12:
				this.executedCalc=this.operand1 | this.operand2;
				System.out.println("EXECUTE : ORR "+ this.operand1+" with "+this.operand2);
				break;
			case 13:
				this.executedCalc=this.operand2;

				System.out.println("EXECUTE : MOV "+ this.operand2+ " to R" +this.destinationRegister);

				break;
			case 15:
				this.executedCalc=~this.operand2;
				System.out.println("EXECUTE : MVN "+ this.operand2);
				break;
			default:
				System.out.println("EXECUTE : GIVEN OPCODE HAS NOT BEEN ADDED");

			}


		} else if (this.DataTransferInstruction) {
			/*
			 * code for execution of data transfer
			 */
			this.register1=Integer.parseInt(this.instruction_word.substring(12, 16),2);
			this.destinationRegister=Integer.parseInt(this.instruction_word.substring(16, 20),2);

		} else if (this.BranchInstruction && this.takeBranch) {
			/*
			 * code for execution of branch instruction
			 */
			//First change PC value to go to previous instruction
			//System.out.println("in branch execution");
			this.R[this.PCregister]=this.R[this.PCregister]-4;
			//now calculate offset where need to jump and then add that to pc
			int offSet=0;
			String offSetString=this.instruction_word.substring(8, 32);
			offSet=Integer.parseInt(offSetString,2);
			//System.out.println(offSet+" OFFSET");
			int s=Integer.parseInt(this.instruction_word.substring(8, 9),2);

			//extend the sign if sign value is 1 or 0 accord.
			if(s==1)
				offSetString="11111111"+offSetString;
			else
				offSetString="00000000"+offSetString;
			offSet=(int)Long.parseLong(offSetString,2);
			//System.out.println(offSet+" OFFSET");
			//shift off set by 4;
			offSet=offSet<<2;
			//offSetString=offSet;
			//System.out.println(offSet+" OFFSET");
			//System.exit(0);
			//now add 2 more index or 2*4 to offset
			offSet=offSet+8;
			if(this.isLinkBranch){
				//
				
				this.R[this.Linkregister]=this.R[this.PCregister]+4;
				for(int i=0;i<16;i++){
					this.programStack.get(i).push(this.R[i]);
				}
				System.out.print("LINKBRANCH : FROM "+this.R[this.PCregister]+" TO ");
				this.R[this.PCregister]=this.R[this.PCregister]+offSet;
				System.out.println(this.R[this.PCregister]);
				
				
			}
			else{
				System.out.print("BRANCH : FROM "+this.R[this.PCregister]+" TO ");
				this.R[this.PCregister]=this.R[this.PCregister]+offSet;
				System.out.println(this.R[this.PCregister]);
			}
			

		} 
		else if(this.swi_read)
			this.swi_read();
		else if(this.swi_print)
			this.swi_print();
		else if (this.swi_exit){
			return 0;
		}
		return 1;
	}


	@Override
	void mem() {
		int i=Integer.parseInt(this.instruction_word.substring(6, 7),2);
		int offSetValue=0;
		if(this.DataTransferInstruction){
			if(i==0){
				//this is load or store using register and shift 

				//get Rm store it
				this.register2=Integer.parseInt(this.instruction_word.substring(28, 32),2);
				this.operand2=(int)this.R[this.register2];
				//to get shift from offset
				String shiftCodeWord = this.instruction_word.substring(20, 28);
				//shiftingtype indicate pre or post indexing
				int shiftingType = Integer.parseInt(shiftCodeWord.substring(7, 8), 2);
				// for shift by immediate or by register value
				int sr = 0, shiftAmount = 0;//shift register and shift amount
				if (shiftingType == 1) {
					//if shiftingtype is 1 means add offset before transfer
					//rs store first 4 bit value of shiftcodeword
					sr = Integer.parseInt(shiftCodeWord.substring(0, 4), 2);
					shiftAmount = (int) this.R[sr];
				} 
				else if (shiftingType == 0) {
					//add offset after transfer
					shiftAmount = Integer.parseInt(shiftCodeWord.substring(0, 5), 2);
				}

				String shift = shiftCodeWord.substring(5, 7);
				/*
				 * need to see this roation is done here
				 */
				//now everything same as data processor
				//shift according to type of shift i.e
				/*
				 * 00 = logical left
				 * 01 = logical right
				 * 11 = rotate right
				 */
				if (shift.equals("11")) {
					//rotating right
					this.operand2 = this.operand2 >> shiftAmount;
					this.operand2 = this.operand2 | this.operand2 << (64 - shiftAmount);
				} 
				else if (shift.equals("00"))// left shifting
					this.operand2 = this.operand2 << shiftAmount;

				else if (shift.equals("01"))// right shift
					this.operand2 = this.operand2 >> shiftAmount;
				offSetValue=(int)this.operand2;

			}
			//if shifting is by imediate
			else if(i==1){
				//for 20 to 32
				//take simply offset value
				offSetValue=Integer.parseInt(this.instruction_word.substring(20, 32),2);
			}
			//now offset and operand has been calculated accoring to immediate or shift and register
			//transfer it according to load or store
			if(this.toLoad){
				int tempaddress=(int)this.R[this.register1]+offSetValue;
				long tempData=read_word_Data(tempaddress);
				this.R[this.destinationRegister]=tempData;
				System.out.println("MEMORY : LOAD "+ tempData+" From 0x" + Integer.toHexString(tempaddress)+ " TO REGISTER " +this.destinationRegister);
			}
			else if(this.toStore){
				int tempaddress=(int)this.R[this.register1]+offSetValue;
				this.write_word_Data(tempaddress, this.R[this.destinationRegister]);
				System.out.println("MEMORY : STORE "+ this.R[this.destinationRegister]+" to 0x" + Integer.toHexString(tempaddress));
				
			}
			else{
				System.out.println("MEMORY : No memory Operation");
			}
		}
		else{
			System.out.println("MEMORY : No memory Operation");
		}

	}

	@Override
	void write_back() {
		// TODO Auto-generated method stub
		//System.out.println("register2 "+14 + "DEST "+destinationRegister);
		//System.out.println("register2 "+this.R[14] + "DEST "+this.R[this.PCregister]);
		
		if(this.register2==14 && this.destinationRegister==15){
			this.R[this.PCregister]=this.R[this.Linkregister];
			for(int i=0;i<15;i++){
				this.R[i]=this.programStack.get(i).pop();
			}

		}
		else if(!this.toStore && !this.toLoad){
			if(this.DataProcessInstruction && this.opcodeValue!=10){
				
				this.R[this.destinationRegister]=this.executedCalc;
				System.out.println("WRITEBACK : write "+ this.executedCalc+" to R"+this.destinationRegister);
			}
			else{
				System.out.println("WRITEBACK : no write back operation");
			}

		}
		this.BranchInstruction = false;
		this.DataProcessInstruction = false;
		this.DataTransferInstruction = false;
		this.swi_exit = false;
		this.swi_print=false;
		this.swi_read=false;
		this.takeBranch = false;
		this.toLoad = false;
		this.toStore = false;
		this.isLinkBranch=false;
		this.instruction_word = "";
		this.executedCalc = 0;
		this.conditionValue = 0;
		this.immediateValue = 0;
		this.opcodeValue = 0;
		this.operand1 = 0;
		this.operand2 = 0;
		this.register1 = 0;
		this.register2 = 0;
		this.destinationRegister = 0;


	}

	@Override
	String read_word_Instruction(int address) {
		// TODO Auto-generated method stub
		return this.MEMFORINST[address];
	}
	long read_word_Data(int address) {
		// TODO Auto-generated method stub
		return this.MEMFORDATA[address];
	}

	@Override
	void write_word_Instruction(int address, String data) {
		// TODO Auto-generated method stub
		this.MEMFORINST[address] = data;
	}
	@Override
	void write_word_Data(int address, long data) {
		// TODO Auto-generated method stub
		this.MEMFORDATA[address] = data;
	}

}
