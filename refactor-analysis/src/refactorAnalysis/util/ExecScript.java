package refactorAnalysis.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ExecScript {
	
	private List<String> commands = new ArrayList<>();
	
	public void addCommand(String command) {
		this.commands.add(command);
	}
	
	public void execAndRedirectOutput(File outputFile) {
		
		ProcessBuilder pBuilder = new ProcessBuilder(this.commands);
		pBuilder.redirectOutput(outputFile);
		pBuilder.redirectErrorStream(true);
		
		try {
			Process process = pBuilder.start();
			process.waitFor();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String exec() {
		ProcessBuilder pBuilder = new ProcessBuilder(this.commands);
		pBuilder.redirectErrorStream(true);
		String output = "";
		try {
			Process process = pBuilder.start();
			process.waitFor();
			
			BufferedReader bReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line = "";
			while ((line = bReader.readLine()) != null) {
				output += line + System.lineSeparator();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return output;
	}
}
