package refactorAnalysis.file.function;

import java.io.File;

import refactorAnalysis.folderManager.FolderManager;

public class FunctionAnalyzer {
	
	// master folder
	private File folder;

	public FunctionAnalyzer(String projectName, String currentCommitHash) {
		String folderPath = FolderManager.createTempProjectFolder(projectName) + File.separator + currentCommitHash;
		this.folder = new File(folderPath);
		if (!this.folder.exists()) {
			this.folder.mkdirs();
		}
	}

	/**
	 * Get a array of files, where each file gets a function. the file name must
	 * be the same of function name, i.e., a function with name "run" turns into
	 * a file with name "run.c". The file must be ended with ".c" extension
	 * 
	 * @param file
	 *            file with all functions
	 * @param subfolderPath
	 *            subfolder where will put the files.
	 * @return array of files
	 */
	public File[] getFunctionFiles(File file, String subfolder) {
		// TODO implement
		return null;
	}
}
