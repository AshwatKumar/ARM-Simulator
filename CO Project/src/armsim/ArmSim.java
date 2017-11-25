package armsim;

public class ArmSim extends ArmVariables {
	//flags
	byte N,Z;
	int PCregister=15;
	int debug=1,status=1;

	ArmSim(){
		for(int i=0;i<16;i++)
			this.R[i]=0;
		for(int i=0;i<4000;i++){
			this.MEM_HEAP[i]=0;
			this.MEM_INST[i]=0;
		}
		this.isBranch=false;
		this.isDataproc=false;
		this.isDatatrans=false;
		this.swi_exit=false;
		this.answer=0;
		this.branchTrue=0;
		this.condition=0;
		this.immediate=0;
		this.instruction_word=0;
		this.loadTrue=0;
		this.opcode=0;
		this.operand1=0;
		this.operand2=0;
		this.register1=0;
		this.register2=0;
		this.registerDest=0;
		this.storeTrue=0;

	}


	@Override
	void swi_exit() {
		// TODO Auto-generated method stub

	}

	@Override
	long fetch() {
		// TODO Auto-generated method stub
		this.instruction_word=this.read_word(this.R[PCregister]);
		String hexInst=Long.toHexString(instruction_word);
		if(status==1)
			//System.out.println("FETCH : FETCHED INSTRUCTION 0x"+hexInst+" FROM ADDRESS "+R[PCregister]);
		this.R[PCregister]+=4;
		return this.instruction_word;
	}

	@Override
	void decode() {
		// TODO Auto-generated method stub
		
		byte decodeInst;
		System.out.println("instruction word: "+this.instruction_word+" & ke baad "+(this.instruction_word & 201326592)+" "
				+ "\nafter shift "+ ((this.instruction_word & 201326592)>> 26));
		decodeInst=(byte)((this.instruction_word & 201326592)>> 26);
		
		switch(decodeInst){
		case 0:this.isDataproc=true;
			if(status==1)
				System.out.println("DECODE : Decoded instruction is of type data process");
			/*
			 * code to decode data process instructions here
			 */
			break;
		case 1:this.isDatatrans=true;
			if(status==1)
				System.out.println("DECODE : Decoded instruction is of type data transfer");
			/*
			 * code to decode data transfer instruction here
			 */
			break;
		case 2:this.isBranch=true;
			if(status==1)
				System.out.println("DECODE : Decoded instruction is of type branch");
			/*
			 * code to decode branch instruction here
			 */
			break;
		case 3:this.swi_exit=true;
			if(status==1)
				//System.out.println("DECODE : Decoded instruction is SWI_EXIT");
			this.swi_exit();
			break;
		}

	}

	@Override
	void shift_operand2() {
		// TODO Auto-generated method stub

	}

	@Override
	boolean execute() {
		// TODO Auto-generated method stub
		if(this.isDataproc)
		{
			/*
			 * code for data process
			 */
		}
		else if(this.isDatatrans){
			/*
			 * code for execution of data transfer
			 */
		}
		else if(this.isBranch){
			/*
			 * code for execution of branch instruction
			 */
		}
		else if(this.swi_exit)
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

	}

	@Override
	long read_word(int address) {
		// TODO Auto-generated method stub
		return this.MEM_INST[address];
	}

	@Override
	void write_word(int address, long data) {
		// TODO Auto-generated method stub
		this.MEM_INST[address]=data;
	}

}





















