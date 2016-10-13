package refactorAnalysis.file;

import refactorAnalysis.git.GitProject;

public class FileTask implements Runnable {

	private String filePath;
	private String commitHash;
	private GitProject gitProject;

	public FileTask(GitProject gitProject, String commitHash, String filePath) {
		this.filePath = filePath;
		this.commitHash = commitHash;
		this.gitProject = gitProject;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

	}

}
