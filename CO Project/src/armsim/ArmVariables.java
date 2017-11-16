package armsim; 
// define DEBUG 1
//define STATUS 1
public abstract class ArmVariables {
	//registe files
	int R[]=new int[16];

	

	//memory for both instruction and allocation
	long MEM_HEAP[]=new long[4000];
	long MEM_INST[]=new long[4000];

	//control signals

	long instruction_word;
	int operand1,operand2,answer;

	//registers in instructions

	byte register1,register2,registerDest;

	int branchTrue,storeTrue,loadTrue;

	byte condition,opcode,immediate;


	abstract void swi_exit();
	abstract long fetch();
	abstract void decode();
	abstract void shift_operand2();
	abstract byte execute();
	abstract void update_flags();
	abstract void mem();
	abstract void write_back();
	abstract long read_word(int address);
	//write to array pointer mem+address
	abstract void write_word(int address,long data);

	
}
