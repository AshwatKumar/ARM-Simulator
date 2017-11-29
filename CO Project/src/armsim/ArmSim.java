
package armsim;
import java.util.Scanner;
import java.io.*;
public class ArmSim extends ArmVariables {
	// flags
	boolean NegativeFalg = false, ZeroFlag = false,OverflowFlag=false;
	int PCregister = 15;
	Scanner readFile;
	ArmSim() {
		try{
			File f=new File("dataread.txt");
			this.readFile=new Scanner(f);
		}
		catch(Exception e){
			System.out.println("NO FILE FOR INPUT TERMINATE EXECUTION IF YOU WANT!");
		}
		for (int i = 0; i < 16; i++)
			this.R[i] = 0;
		for (int i = 0; i < 4000; i++) {
			this.MEM_HEAP[i] = 0L;
			this.MEM_INST[i] = new String("0");
		}
		this.isBranch = false;
		this.isDataproc = false;
		this.isDatatrans = false;
		this.swi_exit = false;
		this.answer = 0;
		this.branchTrue = false;
		this.condition = 0;
		this.immediate = 0;
		this.instruction_word = "";
		this.loadTrue = false;
		this.opcode = 0;
		this.operand1 = 0;
		this.operand2 = 0;
		this.register1 = 0;
		this.register2 = 0;
		this.registerDest = 0;
		this.storeTrue = false;

	}

	@Override
	void swi_exit() {
		System.exit(0);

	}

	@Override
	void swi_print() {

		if(this.R[0]==1)
			System.out.println("SWI_PRINT : VALUE IN REGISTER1 "+this.R[1]);
		else
			System.out.println("SWI_PRINT : CAN'T PRINT");

	}

	@Override
	void swi_read() {

		if(this.R[0]==0){
			try{
				this.R[0]=this.readFile.nextLong();
				System.out.println("SWI_READ : READ VALUE IS "+ this.R[0]);	
			}
			catch(Exception e){
				System.out.println("SWI_READ : CAN'T READ FROM FILE SOME ERORRRR!");
				System.exit(0);
			}

		}
		else{
			System.out.println("SWI_READ : CAN'T READ FROM FILE SOME ERORRRR!");
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
		System.out.println("FETCH : FETCHED INSTRUCTION 0x" + hexString + " FROM ADDRESS " + R[PCregister]);
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
			this.isDataproc = true;
			System.out.println("DECODE : Decoded instruction is of type data process");
			/*
			 * code to decode data process instructions here
			 */

			this.immediate = Integer.parseInt(this.instruction_word.substring(6, 7), 2);
			this.opcode = Integer.parseInt(this.instruction_word.substring(7, 11), 2);
			this.register1 = Integer.parseInt(this.instruction_word.substring(12, 16), 2);
			// System.out.println(register1+" "+this.instruction_word);
			this.registerDest = Integer.parseInt(this.instruction_word.substring(16, 20), 2);
			//System.out.println("DESTINATION REGISTER :"+this.registerDest+" "+this.instruction_word);
			//System.out.println(this.registerDest+" REGISTER DEST " + this.instruction_word);
			if (this.register1 != 0)
				this.operand1 = this.R[register1];
			if (this.immediate == 1) {
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
				// is
				// 8
				// bits
				// 0
				// 1
				// 2
				// 3
				// 4
				// 5
				// 6
				// 7
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
			System.out.println("DECODE : Register1 is " + this.register1 + " Register2 is " + this.register2
					+ " RegisterDest is " + this.registerDest);


			break;
		case 1:
			this.isDatatrans = true;
			System.out.println("DECODE : Decoded instruction is of type data transfer");
			/*
			 * code to decode transfer instruction here
			 */
			// taking last value of 6 bit opcode as for store it will
			// be 0 and for load it will be 1(24 & 25 are opcodes)
			int LoadOrStore = Integer.parseInt(this.instruction_word.substring(11, 12), 2);
			//System.out.println(this.opcode + " " + this.instruction_word);
			if (LoadOrStore == 1)
				this.loadTrue = true;
			else if (LoadOrStore == 0)
				this.storeTrue = true;
			else {
				//System.out.println("No Load Store");
			}
			break;

		case 2:
			this.isBranch = true;
			System.out.println("DECODE : Decoded instruction is of type branch");
			/*
			 * code to decode branch instruction here
			 */
			this.condition = Integer.parseInt(this.instruction_word.substring(0, 4), 2);

			if ( ( this.condition == 0 && this.ZeroFlag ) || ( this.condition == 1 && !this.ZeroFlag )
					|| ( this.condition == 10 && ( this.NegativeFalg == this.OverflowFlag )) 
					|| ( this.condition == 11 && ( this.NegativeFalg != this.OverflowFlag ))
					|| (this.condition == 12 && ( this.ZeroFlag == false && this.NegativeFalg == this.OverflowFlag))
					|| (this.condition == 13 && ( this.ZeroFlag == true && this.NegativeFalg != this.OverflowFlag) )
					|| (this.condition == 14 ) ){
				this.branchTrue = true;
				//System.out.println("Can take Branch");
			}
			else{
				this.branchTrue=false;
				//System.out.println("Branch Can't Be Taken!");
			}
			break;
		case 3:
			int swiType=Integer.parseInt(this.instruction_word.substring(24, 32),2);
			if(swiType==108){
				this.swi_read=true;
				System.out.println("DECODE : Decoded instruction is SWI_READ");

			}
			else if(swiType==107){
				this.swi_print=true;
				System.out.println("DECODE : Decoded instruction is SWI_PRINT");

			}
			else if(swiType==17){
				this.swi_exit = true;
				System.out.println("DECODE : Decoded instruction is SWI_EXIT");
				this.swi_exit();
			}
			break;
		}

	}

	@Override
	boolean execute() {

		if (this.isDataproc) {
			/*
			 * code for data process
			 */
			switch(this.opcode){
			case 0:
				this.answer=this.operand1&this.operand2;
				System.out.println("EXECUTE : AND "+ this.operand1+", "+this.operand2);
				break;
			case 1:
				this.answer=this.operand1 ^ this.operand2;
				System.out.println("EXECUTE : XOR "+ this.operand1+", "+this.operand2);
				break;
			case 2:
				this.answer=this.operand1 - this.operand2;
				System.out.println("EXECUTE : SUBTRACT "+ this.operand1+", "+this.operand2);
				break;
			case 4:
				this.answer=this.operand1 + this.operand2;
				System.out.println("EXECUTE : ADD "+ this.operand1+", "+this.operand2);
				break;
			case 5:
				this.answer=this.operand1 + this.operand2+ 1;
				System.out.println("EXECUTE : ADD WITH CARRY"+ this.operand1+", "+this.operand2);
				break;
			case 10:
				this.answer=this.operand1-this.operand2;
				if(this.answer<0)
					this.NegativeFalg=true;
				else
					this.NegativeFalg=false;
				if(this.answer==0)
					this.ZeroFlag=true;
				else
					this.ZeroFlag=false;
				System.out.println("EXECUTE : COMPARE "+ this.operand1+", "+this.operand2);
				//System.exit(0);
				break;
			case 12:
				this.answer=this.operand1 | this.operand2;
				System.out.println("EXECUTE : OR "+ this.operand1+", "+this.operand2);
				break;
			case 13:
				this.answer=this.operand2;
				System.out.println("EXECUTE : MOVE "+ this.operand2+ " TO Register" +this.registerDest);

				break;
			case 15:
				this.answer=~this.operand2;
				System.out.println("EXECUTE : NOT "+ this.operand2);
				break;
			default:
				System.out.println("EXECUTE : GIVEN OPCODE HAS NOT BEEN ADDED");

			}


		} else if (this.isDatatrans) {
			/*
			 * code for execution of data transfer
			 */
			this.register1=Integer.parseInt(this.instruction_word.substring(12, 16),2);
			this.registerDest=Integer.parseInt(this.instruction_word.substring(16, 20),2);

		} else if (this.isBranch && this.branchTrue) {
			/*
			 * code for execution of branch instruction
			 */
			//First change PC value to go to previous instruction
			System.out.println("in branch execution");
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
			System.out.print("BRANCH : BRANCH FROM "+this.R[this.PCregister]+" TO ");
			this.R[this.PCregister]=this.R[this.PCregister]+offSet;
			System.out.println(this.R[this.PCregister]);


		} 
		else if(this.swi_read)
			this.swi_read();
		else if(this.swi_print)
			this.swi_print();
		else if (this.swi_exit){
			return false;
		}
		return true;
	}


	@Override
	void mem() {
		int i=Integer.parseInt(this.instruction_word.substring(6, 7),2);
		int offSetValue=0;
		if(this.isDatatrans){
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
			if(this.loadTrue){
				int tempaddress=(int)this.R[this.register1]+offSetValue;
				long tempData=read_word_Data(tempaddress);
				this.R[this.registerDest]=tempData;
				System.out.println("MEMORY : LOAD TO REGISTER " +this.registerDest+ " VALUE"+ tempData+" From 0x" + Integer.toHexString(tempaddress));
			}
			else if(this.storeTrue){
				int tempaddress=(int)this.R[this.register1]+offSetValue;
				this.write_word_Data(tempaddress, this.R[this.registerDest]);

			}
			else{
				System.out.println("no load store");
			}
		}

	}

	@Override
	void write_back() {
		// TODO Auto-generated method stub
		if(!this.storeTrue && !this.loadTrue){
			if(this.isDataproc && this.opcode!=10){
				this.R[this.registerDest]=this.answer;
				System.out.println("WRITEBACK : WRITE "+ this.answer+" TO REGISTER "+this.registerDest);
			}
			else{
				//System.out.println("no write back");
			}

		}
		this.isBranch = false;
		this.isDataproc = false;
		this.isDatatrans = false;
		this.swi_exit = false;
		this.answer = 0;
		this.branchTrue = false;
		this.condition = 0;
		this.immediate = 0;
		this.instruction_word = "";
		this.loadTrue = false;
		this.opcode = 0;
		this.operand1 = 0;
		this.operand2 = 0;
		this.register1 = 0;
		this.register2 = 0;
		this.registerDest = 0;
		this.storeTrue = false;
		this.swi_print=false;
		this.swi_read=false;

	}

	@Override
	String read_word_Instruction(int address) {
		// TODO Auto-generated method stub
		return this.MEM_INST[address];
	}
	long read_word_Data(int address) {
		// TODO Auto-generated method stub
		return this.MEM_HEAP[address];
	}

	@Override
	void write_word_Instruction(int address, String data) {
		// TODO Auto-generated method stub
		this.MEM_INST[address] = data;
	}
	@Override
	void write_word_Data(int address, long data) {
		// TODO Auto-generated method stub
		this.MEM_HEAP[address] = data;
	}

}
