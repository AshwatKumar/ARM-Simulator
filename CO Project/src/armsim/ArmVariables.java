package armsim; 
public abstract class ArmVariables {

	//memory for both instruction and allocation
	//hex string at address
	Long MEMFORDATA[]=new Long[4000];
	String MEMFORINST[]=new String[4000];
	//registe files
	long R[]=new long[16];

	//control signals
	//instuction as binary string
	String instruction_word;
	long operand1,operand2,executedCalc;

	//registers in instructions

	int register1,register2,destinationRegister;

	boolean DataProcessInstruction,BranchInstruction,DataTransferInstruction,takeBranch,toStore,toLoad;
	boolean swi_exit,swi_print,swi_read;
	int conditionValue,opcodeValue,immediateValue;
	
	// flags
	boolean NegativeFalg, ZeroFlag,OverflowFlag;
	//fixes pc register
	final int PCregister = 15;


	abstract void swi_exit();
	abstract void swi_print();
	abstract void swi_read();
	abstract String fetch();
	abstract void decode();
	abstract int execute();
	abstract void mem();
	abstract void write_back();
	abstract String read_word_Instruction(int address);
	abstract long read_word_Data(int address);
	//write to array pointer mem+address
	abstract void write_word_Instruction(int address,String data);
	abstract void write_word_Data(int address,long data);

	
}
