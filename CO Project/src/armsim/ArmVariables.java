package armsim; 
// define DEBUG 1
//define STATUS 1
public abstract class ArmVariables {
	//registe files
	long R[]=new long[16];
	
	

	//memory for both instruction and allocation
	//hex string at address
	String MEM_HEAP[]=new String[4000];
	String MEM_INST[]=new String[4000];

	//control signals
	//instuction as binary string
	String instruction_word;
	long operand1,operand2,answer;

	//registers in instructions

	int register1,register2,registerDest;

	boolean branchTrue,storeTrue,loadTrue;
	boolean isDataproc,isBranch,isDatatrans,swi_exit;
	int condition,opcode,immediate;


	abstract void swi_exit();
	abstract String fetch();
	abstract void decode();
	abstract void shift_operand2();
	abstract boolean execute();
	abstract void update_flags();
	abstract void mem();
	abstract void write_back();
	abstract String read_word(int address);
	
	//write to array pointer mem+address
	abstract void write_word(int address,String data);

	
}
