package refactorAnalysis.commit;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.apache.commons.io.FileUtils;

import gitmanager.GitManager;
import gitmanager.properties.PropertiesManager;
import refactorAnalysis.file.FileTask;
import refactorAnalysis.folderManager.FolderManager;
import refactorAnalysis.git.GitExplorer;
import refactorAnalysis.git.GitProject;

public class CommitTask {

	private GitProject gitProject = null;

	private String commitHash = "";

	public CommitTask(GitProject gitProject, String commitHash) {
		this.gitProject = gitProject;
		this.commitHash = commitHash;
	}

	public void runAllFiles() {
		System.out.println("commit: "+this.commitHash);
		
		if (this.setCommitFolders() == false) {
			System.out.println("    error to set commit folders");
			return;
		}

		List<String> filepaths = getRelevantFilepaths();

		int numberOfThreads = this.getNumberOfThreads();

		Stack<Thread> threads = new Stack<>();
		for (String filepath : filepaths) {

			threads.push(new Thread(new FileTask(this.gitProject, this.commitHash, filepath)));
			if (threads.size() >= numberOfThreads) {
				this.runAllThreads(threads);
			}
		}

		if (!threads.isEmpty()) {
			this.runAllThreads(threads);
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

	private void runAllThreads(Stack<Thread> threads) {
		List<Thread> threadList = new ArrayList<>();

		while (!threads.isEmpty()) {
			threadList.add(threads.peek());
			threads.pop().start();
		}

		for (Thread thread : threadList) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private int getNumberOfThreads() {
		String numberOfThreadsString = PropertiesManager.getPropertie("threads.maximum");
		int numberOfThreads;
		try {
			numberOfThreads = Integer.parseInt(numberOfThreadsString);
		} catch (Exception e) {
			numberOfThreads = 1;
		}

		return numberOfThreads;
	}

}
