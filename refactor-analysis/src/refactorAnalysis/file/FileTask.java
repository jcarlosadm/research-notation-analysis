package refactorAnalysis.file;

import java.io.File;
import java.util.List;

import refactorAnalysis.file.function.FunctionAnalyzer;
import refactorAnalysis.file.notation.CppStatsAnalyze;
import refactorAnalysis.file.notation.NotationAnalyzer;
import refactorAnalysis.git.GitExplorer;
import refactorAnalysis.git.GitProject;
import refactorAnalysis.report.Report;

public class FileTask implements Runnable {

	private File fileCurrentCommit;
	private File filePreviousCommit;
	private String currentCommitHash;
	private String previousCommitHash;
	private GitProject gitProject;
	private String filename;

	public FileTask(GitProject gitProject, String commitHash, String filePath) {
		this.fileCurrentCommit = new File(gitProject.getCurrentCommitFolder() + File.separator + filePath);
		this.filename = this.fileCurrentCommit.getName();
		this.currentCommitHash = commitHash;
		this.gitProject = gitProject;
	}

	@Override
	public void run() {
		if (this.setPreviousCommitInfo() == false) {
			return;
		}

		FunctionAnalyzer functionAnalyzer = new FunctionAnalyzer(this.gitProject.getName(), this.currentCommitHash);
		List<File> functionFilesCurrentCommit = functionAnalyzer.getFunctionFiles(this.fileCurrentCommit, "current");
		List<File> functionFilesPreviousCommit = functionAnalyzer.getFunctionFiles(this.filePreviousCommit, "previous");

		if (functionFilesCurrentCommit == null || functionFilesPreviousCommit == null
				|| functionFilesCurrentCommit.isEmpty() || functionFilesPreviousCommit.isEmpty()) {
			return;
		}

		NotationAnalyzer notationAnalyzer = new CppStatsAnalyze();
		boolean writeReport = false;

		for (File functionFileCurrentCommit : functionFilesCurrentCommit) {
			for (File functionFilePreviousCommit : functionFilesPreviousCommit) {

				if (functionFileCurrentCommit.getName().equals(functionFilePreviousCommit.getName())
						&& (notationAnalyzer.analyze(functionFilePreviousCommit, functionFileCurrentCommit) == true)) {
					this.writeOnReport();
					writeReport = true;
					break;
				}
			}
			if (writeReport == true) {
				break;
			}
		}
	}

	private synchronized void writeOnReport() {
		Report report = Report.getCurrentInstance(this.gitProject.getName());
		String link = this.gitProject.getUrl() + "/commit/" + this.currentCommitHash;
		try {
			report.writeLink(link, this.filename);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private boolean setPreviousCommitInfo() {
		GitExplorer gitExplorerCurrentCommit = new GitExplorer(this.gitProject.getGitManagerCurrentCommit());
		this.previousCommitHash = gitExplorerCurrentCommit.getPreviousCommit(currentCommitHash);
		GitExplorer gitExplorerPreviousCommit = new GitExplorer(this.gitProject.getGitManagerPreviousCommit());
		String filePathPreviousCommit = "";

		try {
			filePathPreviousCommit = gitExplorerPreviousCommit.searchFilePathInCurrentCommit(this.filename,
					this.previousCommitHash, ".c");
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (filePathPreviousCommit == null || filePathPreviousCommit.isEmpty())
			return false;
		this.filePreviousCommit = new File(
				this.gitProject.getPreviousCommitFolder() + File.separator + filePathPreviousCommit);

		return true;
	}

}
