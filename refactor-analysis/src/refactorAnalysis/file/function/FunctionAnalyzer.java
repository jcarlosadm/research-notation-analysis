package refactorAnalysis.file.function;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import gitmanager.properties.PropertiesManager;
import refactorAnalysis.folderManager.FolderManager;

public class FunctionAnalyzer {

	private static final String XML_NAME_PARAMETER_LIST = "<parameter_list>";
	private static final String XML_NAME_NAME = "<name>";
	private static final String XML_END_NAME_NAME = "</name>";
	private static final String XML_END_NAME_TYPE = "</type>";
	private static final String XML_END_NAME_FUNCTION = "</function>";
	private static final String XML_NAME_FUNCTION = "<function>";
	private File folder;

	public FunctionAnalyzer(String projectName, String currentCommitHash) {
		String folderPath = FolderManager.createTempProjectFolder(projectName) + File.separator + currentCommitHash;
		this.folder = new File(folderPath);
		if (!this.folder.exists()) {
			this.folder.mkdirs();
		}
	}

	/**
	 * Get a list of files, where each file gets a function. the file name must
	 * be the same of function name, i.e., a function with name "run" turns into
	 * a file with name "run.c". The file must be ended with ".c" extension
	 * 
	 * @param file
	 *            file with all functions
	 * @param subfolderPath
	 *            subfolder where will put the files.
	 * @return list of files
	 */
	public List<File> getFunctionFiles(File file, String subfolderName) {
		List<File> functionFiles = new ArrayList<>();

		String filePath = file.getAbsolutePath();
		String subfolderPath = this.folder.getAbsolutePath() + File.separator + subfolderName;
		File subfolder = new File(subfolderPath);
		if (!subfolder.exists()) {
			subfolder.mkdirs();
		}

		String xmlPath = filePath.substring(0, filePath.lastIndexOf(".")) + ".xml";
		File xml = new File(xmlPath);

		try {
			if (!xml.exists()) {
				ProcessBuilder pBuilder = new ProcessBuilder(PropertiesManager.getProperty("src2srcml.path"), filePath);
				pBuilder.redirectOutput(xml);
				pBuilder.redirectErrorStream(true);
				
				Process process = pBuilder.start();
				process.waitFor();
			}
			
			if (!xml.exists()) {
				return null;
			}

			BufferedReader bReader = new BufferedReader(new FileReader(xml));
			String line;
			while ((line = bReader.readLine()) != null) {
				if (line.contains(XML_NAME_FUNCTION)) {
					functionFiles.add(this.extractFunction(line, bReader, subfolderPath));
				}
			}

			bReader.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return functionFiles;
	}

	private File extractFunction(String line, BufferedReader bReader, String subfolderPath) throws Exception {
		BufferedWriter bWriter = null;

		if (line.contains(XML_END_NAME_FUNCTION)) {
			line = line.substring(line.indexOf(XML_NAME_FUNCTION),
					line.indexOf(XML_END_NAME_FUNCTION) + XML_END_NAME_FUNCTION.length()) + System.lineSeparator();
		} else {
			line = line.substring(line.indexOf(XML_NAME_FUNCTION)) + System.lineSeparator();
			while (!line.contains(XML_NAME_PARAMETER_LIST)) {
				line += bReader.readLine() + System.lineSeparator();
			}
		}

		String functionName = line.substring(line.indexOf(XML_END_NAME_TYPE));
		functionName = functionName.substring(0, functionName.indexOf(XML_END_NAME_NAME));
		functionName = functionName.substring(functionName.indexOf(XML_NAME_NAME) + XML_NAME_NAME.length());

		File xmlFunction = this.buildXMLFileFunction(functionName, subfolderPath);
		bWriter = new BufferedWriter(new FileWriter(xmlFunction));

		bWriter.write(line);
		if (!line.contains(XML_END_NAME_FUNCTION)) {
			while ((line = bReader.readLine()) != null && !line.contains(XML_END_NAME_FUNCTION)) {
				bWriter.write(line  + System.lineSeparator());
			}
			if (line != null) {
				line = line.substring(0, line.indexOf(XML_END_NAME_FUNCTION) + XML_END_NAME_FUNCTION.length());
				bWriter.write(line + System.lineSeparator());
			}
		}

		if (bWriter != null)
			bWriter.close();

		ProcessBuilder pBuilder = new ProcessBuilder(PropertiesManager.getProperty("srcml2src.path"),
				xmlFunction.getAbsolutePath());
		File resultFile = this.buildFileFunction(functionName, subfolderPath);
		pBuilder.redirectOutput(resultFile);
		pBuilder.redirectErrorStream(true);
		Process process = pBuilder.start();
		process.waitFor();

		return resultFile;
	}

	private File buildXMLFileFunction(String functionName, String folder) {
		return new File(folder + File.separator + "func_" + functionName + ".xml");
	}

	private File buildFileFunction(String functionName, String folder) {
		return new File(folder + File.separator + "func_" + functionName + ".c");
	}
}
