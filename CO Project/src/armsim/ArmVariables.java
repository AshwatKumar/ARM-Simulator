package armsim; 
// define DEBUG 1
//define STATUS 1
public abstract class ArmVariables {
	//registe files
	long R[]=new long[16];
	
	

	//memory for both instruction and allocation
	//hex string at address
	Long MEM_HEAP[]=new Long[4000];
	String MEM_INST[]=new String[4000];

	//control signals
	//instuction as binary string
	String instruction_word;
	long operand1,operand2,answer;

	//registers in instructions

	int register1,register2,registerDest;

	boolean branchTrue,storeTrue,loadTrue;
	boolean isDataproc,isBranch,isDatatrans,swi_exit,swi_print,swi_read;
	int condition,opcode,immediate;


	abstract void swi_exit();
	abstract void swi_print();
	abstract void swi_read();
	abstract String fetch();
	abstract void decode();
	abstract boolean execute();
	abstract void mem();
	abstract void write_back();
	abstract String read_word_Instruction(int address);
	abstract long read_word_Data(int address);
	
	//write to array pointer mem+address
	abstract void write_word_Instruction(int address,String data);
	abstract void write_word_Data(int address,long data);

	
}
