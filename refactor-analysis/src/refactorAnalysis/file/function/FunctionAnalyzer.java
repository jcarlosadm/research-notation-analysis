package refactorAnalysis.file.function;

import java.io.File;

import refactorAnalysis.folderManager.FolderManager;

public class FunctionAnalyzer {
	private File folder;
	
	public FunctionAnalyzer(String projectName, String currentCommitHash) {
		String folderPath = FolderManager.createTempProjectFolder(projectName) + File.separator + currentCommitHash;
		this.folder = new File(folderPath);
		if (!this.folder.exists()) {
			this.folder.mkdirs();
		}
	}
	
	public File[] getFunctionFiles(File file, String subfolderPath) {
		// TODO implement
		return null;
	}
}
