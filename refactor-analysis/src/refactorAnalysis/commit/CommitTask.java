package refactorAnalysis.commit;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;

import gitmanager.GitManager;
import gitmanager.properties.PropertiesManager;
import refactorAnalysis.file.FileTask;
import refactorAnalysis.file.ThreadCount;
import refactorAnalysis.folderManager.FolderManager;
import refactorAnalysis.git.GitExplorer;
import refactorAnalysis.git.GitProject;

public class CommitTask {

	private static final int SLEEP_SECONDS = 1;

	private GitProject gitProject = null;

	private String commitHash = "";

	public CommitTask(GitProject gitProject, String commitHash) {
		this.gitProject = gitProject;
		this.commitHash = commitHash;
	}

	public void runAllFiles() {
		if (this.setCommitFolders() == false) {
			System.out.println("    error to set commit folders for " + this.commitHash);
			return;
		}

		List<String> filepaths = getRelevantFilepaths();

		ThreadCount threadCount = new ThreadCount(0, false);
		int maxOfThreads = this.getNumberOfThreads();

		for (String filepath : filepaths) {
			Thread thread = new Thread(new FileTask(this.gitProject, this.commitHash, filepath, threadCount));
			threadCount.increment();

			if (threadCount.getNumberOfThreadsRunning() >= maxOfThreads) {
				threadCount.block();
				thread.start();
				while (threadCount.isBlocked()) {
					try {
						Thread.sleep(SLEEP_SECONDS*1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			} else {
				thread.start();
			}
		}

		while (threadCount.getNumberOfThreadsRunning() > 0) {
			try {
				Thread.sleep(SLEEP_SECONDS*1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		this.deleteTempFiles();
	}

	private void deleteTempFiles() {
		String tempFolder = FolderManager.createTempProjectFolder(this.gitProject.getName());
		try {
			FileUtils.deleteDirectory(new File(tempFolder));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private boolean setCommitFolders() {
		String tempFolder = FolderManager.createTempProjectFolder(this.gitProject.getName());
		String currentCommitPath = tempFolder + File.separator + "currentCommit";
		String previousCommitPath = tempFolder + File.separator + "previousCommit";

		File currentCommitFolder = new File(currentCommitPath);
		File previousCommitFolder = new File(previousCommitPath);
		this.deleteFolders(currentCommitFolder);
		this.deleteFolders(previousCommitFolder);

		if (!currentCommitFolder.mkdirs() || !previousCommitFolder.mkdirs()) {
			return false;
		}

		this.copyProjectToFolder(currentCommitFolder);
		this.copyProjectToFolder(previousCommitFolder);

		return this.setNewGitManagers(currentCommitPath, previousCommitPath);
	}

	private boolean setNewGitManagers(String currentCommitPath, String previousCommitPath) {
		this.gitProject.setCurrentCommitFolder(currentCommitPath);
		this.gitProject.setPreviousCommitFolder(previousCommitPath);

		GitManager gitManagerCurrentCommit = this.gitProject.getGitManagerCurrentCommit();
		GitManager gitManagerPreviousCommit = this.gitProject.getGitManagerPreviousCommit();

		GitExplorer gitExplorer = new GitExplorer(this.gitProject.getGitManager());

		if (!gitManagerCurrentCommit.checkout(this.commitHash)) {
			return false;
		}

		if (!gitManagerPreviousCommit.checkout(gitExplorer.getPreviousCommit(this.commitHash))) {
			return false;
		}

		return true;
	}

	private void deleteFolders(File directory) {
		if (!directory.exists()) {
			return;
		}
		try {
			FileUtils.deleteDirectory(directory);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void copyProjectToFolder(File destFolder) {
		try {
			FileUtils.copyDirectory(new File(this.gitProject.getProjectFolder()), destFolder);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private List<String> getRelevantFilepaths() {
		GitManager gitManager = this.gitProject.getGitManagerCurrentCommit();

		return gitManager.getChangedFilesMap().get(commitHash);
	}

	private int getNumberOfThreads() {
		String numberOfThreadsString = PropertiesManager.getProperty("threads.maximum");
		int numberOfThreads;
		try {
			numberOfThreads = Integer.parseInt(numberOfThreadsString);
		} catch (Exception e) {
			numberOfThreads = 1;
		}

		return numberOfThreads;
	}

}
