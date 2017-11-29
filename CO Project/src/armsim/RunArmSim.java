package armsim;

import java.util.Scanner;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class RunArmSim {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		RunArmSim runArmSim = new RunArmSim();
		Scanner in = new Scanner(System.in);
		// System.out.println("Give name of instruction file with path");
		// String path=in.next();
		String path = "arraySum.mem";
		if (path.length() == 0) {
			System.out.println("INVALID FILE PATH PLEASE RESTART");
			System.exit(1);
		}
		// reallocating all variables
		ArmVariables armVar = new ArmSim();
		// loading memory file into memory
		runArmSim.load_program_memory(path, armVar);
		// running ARMSIM
		runArmSim.runarmsim(armVar);
		// writing back
		runArmSim.write_data_memory(armVar);

		in.close();
	}

	void load_program_memory(String path, ArmVariables armVar) {
		try {
			FileReader fileReader = new FileReader(path);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				int i = line.indexOf(" ");
				String s1 = line.substring(2, i);
				String instruction = line.substring(i + 1, line.length());
				int address = Integer.parseInt(s1, 16);
				//System.out.println(instruction+" INStruction");
				armVar.write_word_Instruction(address, instruction);
			}
			fileReader.close();
		} catch (IOException e) {
			System.out.println("CAN'T OPEN FILE PLEASE RESTART");
			System.exit(1);
		}
	}

	void runarmsim(ArmVariables allArmVariables) {
		String instruction;
		boolean executed;
		String zeroString=String.format("%32s", "0").replace(" ", "0");
		// Value in PC counter is smaller than 4000 index of memory coz
		// we have memory size 4000
		while (allArmVariables.R[15] < 4000) {
			instruction = allArmVariables.fetch();
			if (instruction.equals(zeroString))
				return;
			
			allArmVariables.decode();
			
			executed=allArmVariables.execute(); 
			if(!executed) return;
			  
			allArmVariables.mem();
			  
			allArmVariables.write_back();
			 

		}
	}

	void write_data_memory(ArmVariables armVar) {
		try {
			
		}
		catch(Exception e){
			
		}
	}

}
