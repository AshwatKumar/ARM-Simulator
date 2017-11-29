package armsim;

public class ArmSim extends ArmVariables {
	// flags
	boolean NegativeFalg = false, ZeroFlag = false;
	int PCregister = 15;
	int debug = 1, status = 1;

	ArmSim() {
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


	}

	@Override
	void swi_print() {


	}

	@Override
	void swi_read() {


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
		if (status == 1) {
			System.out.println("FETCH : FETCHED INSTRUCTION 0x" + hexString + " FROM ADDRESS " + R[PCregister]);
		}
		this.R[PCregister] += 4;

		return this.instruction_word;
	}

	@Override
	void decode() {
		// TODO Auto-generated method stub
		System.out.println(this.swi_exit+" SWI_EXIT");
		int function;

		function = Integer.parseInt(this.instruction_word.substring(4, 6), 2);
		//
		switch (function) {
		case 0:
			this.isDataproc = true;
			if (status == 1) {
				System.out.println("DECODE : Decoded instruction is of type data process");
			}
			break;
		case 1:
			this.isDatatrans = true;
			if (status == 1) {
				System.out.println("DECODE : Decoded instruction is of type data transfer");
			}
			break;
		case 2:
			this.isBranch = true;
			if (status == 1) {
				System.out.println("DECODE : Decoded instruction is of type branch");

			}
			break;
		case 3:
			this.swi_exit = true;
			if (status == 1) {
				System.out.println("DECODE : Decoded instruction is SWI_EXIT");
				this.swi_exit();
			}
			break;
		}

		if (this.isDataproc) {
			/*
			 * code to decode data process instructions here
			 */

			this.immediate = Integer.parseInt(this.instruction_word.substring(6, 7), 2);
			this.opcode = Integer.parseInt(this.instruction_word.substring(7, 11), 2);
			this.register1 = Integer.parseInt(this.instruction_word.substring(12, 16), 2);
			// System.out.println(register1+" "+this.instruction_word);
			this.registerDest = Integer.parseInt(this.instruction_word.substring(16, 20), 2);
			System.out.println(this.registerDest+" REGISTER DEST " + this.instruction_word);
			if (this.register1 != 0)
				this.operand1 = this.R[register1 - 1];
			if (this.immediate == 1) {
				this.operand2 = Long.parseLong(this.instruction_word.substring(24, 32), 2);
				int rotateImmediateBy = Integer.parseInt(this.instruction_word.substring(20, 24), 2);
				this.operand2 = operand2 << rotateImmediateBy;
			} else {

				this.register2 = Integer.parseInt(this.instruction_word.substring(28, 32), 2);
				// System.out.println(register2+" register2
				// "+this.instruction_word);
				this.operand2 = this.R[register2 - 1];
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
					shiftAmount = (int) this.R[rs - 1];
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

			if (status == 1) {
				System.out.println("DECODE : Register1 is " + this.register1 + " Register2 is " + this.register2
						+ " RegisterDest is " + this.registerDest);
			}

		}
		if (this.isDatatrans) {
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
				System.out.println("Last Bit Of Opcode is :" + LoadOrStore);
			}

		}
		if (this.isBranch) {
			/*
			 * code to decode branch instruction here
			 */
			this.condition = Integer.parseInt(this.instruction_word.substring(0, 4), 2);

			if ((this.condition == 0 && this.ZeroFlag) || (this.condition == 1 && !this.ZeroFlag)
					|| (this.condition == 11 && this.NegativeFalg) || (this.condition == 10 && !this.NegativeFalg)
					|| (this.condition == 12 && !this.NegativeFalg && !this.ZeroFlag)
					|| (this.condition == 13 && (this.NegativeFalg || this.ZeroFlag)) || (this.condition == 14)){
				this.branchTrue = true;
				//System.out.println("Can take Branch");
			}
			else{
				this.branchTrue=false;
				//System.out.println("Branch Can't Be Taken!");
			}

		}

	}

	@Override
	boolean execute() {
		// TODO Auto-generated method stub

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
				System.out.println("EXECUTE : COMPARE"+ this.operand1+", "+this.operand2);
				break;
			case 12:
				this.answer=this.operand1 | this.operand2;
				System.out.println("EXECUTE : OR "+ this.operand1+", "+this.operand2);
				break;
			case 13:
				this.answer=this.operand2;
				System.out.println("EXECUTE : MOVE "+ this.operand2+ " TO " +this.registerDest);
				break;
			case 15:
				this.answer=~this.operand2;
				System.out.println("EXECUTE : NOT "+ this.operand2);
				break;
			default:System.out.println("Wrong Code");

			}


		} else if (this.isDatatrans) {
			/*
			 * code for execution of data transfer
			 */
			this.register1=Integer.parseInt(this.instruction_word.substring(12, 16),2);
			this.registerDest=Integer.parseInt(this.instruction_word.substring(16, 20),2);

		} else if (this.isBranch) {
			/*
			 * code for execution of branch instruction
			 */
			//First change PC value to go to previous instruction
			this.R[this.PCregister]=this.R[this.PCregister]-4;
			//now calculate offset where need to jump and then add that to pc
			int offSet=0;
			String offSetString=this.instruction_word.substring(8, 32);
			offSet=Integer.parseInt(offSetString,2);
			System.out.println(offSet+" OFFSET");
			int s=Integer.parseInt(this.instruction_word.substring(8, 9),2);

			//extend the sign if sign value is 1 or 0 accord.
			if(s==1)
				offSetString="11111111"+offSetString;
			else
				offSetString="00000000"+offSetString;
			offSet=(int)Long.parseLong(offSetString,2);
			System.out.println(offSet+" OFFSET");
			//shift off set by 4;
			offSet=offSet<<2;
			//offSetString=offSet;
			System.out.println(offSet+" OFFSET");
			System.exit(0);
			//now add 2 more index or 2*4 to offset
			offSet=offSet+8;
			this.R[this.PCregister]=this.R[this.PCregister]+offSet;


		} 

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
			
			if(this.immediate==0){
				this.register2=Integer.parseInt(this.instruction_word.substring(28, 32),2);
				this.operand2=(int)this.R[this.register2-1];
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
					shiftAmount = (int) this.R[rs - 1];
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
				offSetValue=(int)this.operand2;

			}
			else if(this.immediate==1)
				//for 20 to 32
				offSetValue=Integer.parseInt(this.instruction_word.substring(20, 32),2);
			if(this.loadTrue){
				int tempaddress=(int)this.R[this.register1-1]+offSetValue;
				long tempData=read_word_Data(tempaddress);
				this.R[this.registerDest-1]=tempData;
				System.out.println("MEMORY : LOAD TO REGISTER " +this.registerDest+ " VALUE"+ tempData+" From 0x" + Integer.toHexString(tempaddress));
			}
			else if(this.storeTrue){
				int tempaddress=(int)this.R[this.register1-1]+offSetValue;
				this.write_word_Data(tempaddress, this.R[this.registerDest-1]);
				
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
			this.R[this.registerDest-1]=this.answer;
			System.out.println("WRITEBACK : WRITE "+ this.answer+" TO REGISTER "+this.registerDest);
		}
		else
			System.out.println("no write back");
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
