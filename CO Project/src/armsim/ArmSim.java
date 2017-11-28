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
			this.MEM_HEAP[i] = new String("0");
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
		// TODO Auto-generated method stub

	}

	@Override
	String fetch() {
		String hexString = new String(this.read_word((int) this.R[PCregister]));
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
				System.out.println("Can take Branch");
			}
			else{
				this.branchTrue=false;
				System.out.println("Branch Can't Be Taken!");
			}

		}

	}

	@Override
	void shift_operand2() {
		// TODO Auto-generated method stub

	}

	@Override
	boolean execute() {
		// TODO Auto-generated method stub
		if (this.isDataproc) {
			/*
			 * code for data process
			 */
		} else if (this.isDatatrans) {
			/*
			 * code for execution of data transfer
			 */
		} else if (this.isBranch) {
			/*
			 * code for execution of branch instruction
			 */
		} else if (this.swi_exit)
			return false;
		return true;
	}

	@Override
	void update_flags() {
		// TODO Auto-generated method stub

	}

	@Override
	void mem() {
		// TODO Auto-generated method stub

	}

	@Override
	void write_back() {
		// TODO Auto-generated method stub

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
	String read_word(int address) {
		// TODO Auto-generated method stub
		return this.MEM_INST[address];
	}

	@Override
	void write_word(int address, String data) {
		// TODO Auto-generated method stub
		this.MEM_INST[address] = data;
	}

}
